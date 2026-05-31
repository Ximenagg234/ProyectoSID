import { useEffect, useState } from 'react';
import * as categoriaApi from '../../api/categoriaApi';
import type { CategoriaResponse, CategoriaRequest } from '../../types/categoria.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { Plus } from 'lucide-react';

const AdminCategoriasPage: React.FC = () => {
  const [categorias, setCategorias] = useState<CategoriaResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editando, setEditando] = useState<CategoriaResponse | null>(null);
  const [form, setForm] = useState<CategoriaRequest>({ nombre: '', descripcion: '' });
  const [mostrarForm, setMostrarForm] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [confirmId, setConfirmId] = useState<number | null>(null);

  const cargar = async () => {
    try {
      setCategorias(await categoriaApi.getAll());
    } catch {
      setError('Error al cargar categorías');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    categoriaApi.getAll()
      .then((data) => setCategorias(data))
      .catch(() => setError('Error al cargar categorías'))
      .finally(() => setLoading(false));
  }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setGuardando(true);
    try {
      if (editando) {
        await categoriaApi.update(editando.idCategoria, form);
      } else {
        await categoriaApi.create(form);
      }
      await cargar();
      setMostrarForm(false);
      setEditando(null);
      setForm({ nombre: '', descripcion: '' });
    } catch {
      setError('No se pudo guardar la categoría');
    } finally {
      setGuardando(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await categoriaApi.remove(id);
      setCategorias((prev) => prev.filter((c) => c.idCategoria !== id));
    } catch {
      setError('No se pudo eliminar la categoría');
    } finally {
      setConfirmId(null);
    }
  };

  return (
    <DashboardLayout
      title="Categorías"
      subtitle="Administra las categorías del marketplace"
      action={
        <button
          className="btn btn-primary"
          onClick={() => { setMostrarForm(true); setEditando(null); setForm({ nombre: '', descripcion: '' }); }}
        >
          <Plus size={15} /> Nueva categoría
        </button>
      }
    >
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {mostrarForm && (
        <form onSubmit={handleSave} className="card" style={{ marginBottom: 24 }}>
          <h3 style={{ fontWeight: 700, fontSize: 14, marginBottom: 16 }}>{editando ? 'Editar' : 'Nueva'} categoría</h3>
          <div style={{ marginBottom: 12 }}>
            <label className="form-label">Nombre</label>
            <input
              className="form-input"
              placeholder="Nombre"
              value={form.nombre}
              onChange={(e) => setForm((p) => ({ ...p, nombre: e.target.value }))}
              required
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label className="form-label">Descripción</label>
            <input
              className="form-input"
              placeholder="Descripción"
              value={form.descripcion}
              onChange={(e) => setForm((p) => ({ ...p, descripcion: e.target.value }))}
            />
          </div>
          <div style={{ display: 'flex', gap: 10 }}>
            <button type="button" onClick={() => setMostrarForm(false)} className="btn btn-secondary btn-sm">Cancelar</button>
            <button type="submit" disabled={guardando} className="btn btn-primary btn-sm">
              {guardando ? 'Guardando...' : 'Guardar'}
            </button>
          </div>
        </form>
      )}

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <table className="table-custom">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Descripción</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {categorias.map((c) => (
              <tr key={c.idCategoria}>
                <td style={{ fontWeight: 600 }}>{c.nombre}</td>
                <td style={{ color: 'var(--text-muted)' }}>{c.descripcion}</td>
                <td style={{ textAlign: 'right' }}>
                  <button
                    onClick={() => { setEditando(c); setForm({ nombre: c.nombre, descripcion: c.descripcion }); setMostrarForm(true); }}
                    className="btn btn-sm btn-outline-primary"
                    style={{ marginRight: 8 }}
                  >
                    Editar
                  </button>
                  <button
                    onClick={() => setConfirmId(c.idCategoria)}
                    className="btn btn-sm btn-outline-danger"
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {confirmId !== null && (
        <ConfirmDialog
          mensaje="¿Eliminar esta categoría?"
          onConfirm={() => handleDelete(confirmId)}
          onCancel={() => setConfirmId(null)}
        />
      )}
    </DashboardLayout>
  );
};

export default AdminCategoriasPage;
