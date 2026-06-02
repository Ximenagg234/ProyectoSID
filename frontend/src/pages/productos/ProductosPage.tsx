import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import * as productoApi from '../../api/productoApi';
import type { ProductoResponse, ProductoRequest } from '../../types/producto.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import ProductoForm from '../../components/productos/ProductoForm';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { Plus, Image } from 'lucide-react';

const ProductosPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const idEmprendimiento = Number(id);

  const [productos, setProductos]           = useState<ProductoResponse[]>([]);
  const [loading, setLoading]               = useState(true);
  const [error, setError]                   = useState<string | null>(null);
  const [mostrarForm, setMostrarForm]       = useState(false);
  const [editando, setEditando]             = useState<ProductoResponse | null>(null);
  const [guardando, setGuardando]           = useState(false);
  const [confirmId, setConfirmId]           = useState<number | null>(null);
  const [recienCreado, setRecienCreado]     = useState(false);

  const cargar = () => {
    setLoading(true);
    productoApi.getAll()
      .then((todos) => setProductos(
        todos.filter((p) => p.idEmprendimiento === idEmprendimiento)
      ))
      .catch(() => setError('Error al cargar los productos'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { cargar(); }, [idEmprendimiento]);

  const handleSave = async (data: ProductoRequest) => {
    setGuardando(true);
    try {
      if (editando) {
        // Editing — update and keep form open
        const updated = await productoApi.update(editando.idProducto, data);
        setEditando(updated);
        setRecienCreado(false);
        cargar();
      } else {
        // Creating — keep form open with new product so user can add photos
        const nuevo = await productoApi.create({ ...data, idEmprendimiento });
        setEditando(nuevo);
        setRecienCreado(true);
        cargar();
      }
    } catch {
      setError('No se pudo guardar el producto');
    } finally {
      setGuardando(false);
    }
  };

  const handleCloseForm = () => {
    setMostrarForm(false);
    setEditando(null);
    setRecienCreado(false);
  };

  const handleDelete = async (idP: number) => {
    try {
      await productoApi.remove(idP);
      setProductos((prev) => prev.filter((p) => p.idProducto !== idP));
    } catch {
      setError('No se pudo eliminar el producto');
    } finally {
      setConfirmId(null);
    }
  };

  return (
    <DashboardLayout
      title="Gestionar Productos"
      action={
        <button
          className="btn btn-primary"
          onClick={() => { setMostrarForm(true); setEditando(null); setRecienCreado(false); }}
        >
          <Plus size={15} /> Nuevo producto
        </button>
      }
    >
      <Link to="/mis-emprendimientos" style={{ fontSize: 13, color: 'var(--primary)', textDecoration: 'none', fontWeight: 600, display: 'inline-block', marginBottom: 20 }}>
        ← Volver a mis emprendimientos
      </Link>

      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {mostrarForm && (
        <div style={{ marginBottom: 24 }}>
          <ProductoForm
            inicial={editando
              ? { nombre: editando.nombre, descripcion: editando.descripcion, precio: editando.precio, stockDisponible: editando.stockDisponible }
              : undefined}
            idEmprendimiento={idEmprendimiento}
            idProducto={editando?.idProducto}
            recienCreado={recienCreado}
            onSubmit={handleSave}
            onCancel={handleCloseForm}
            loading={guardando}
          />
        </div>
      )}

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <table className="table-custom">
          <thead>
            <tr>
              <th style={{ width: 52 }}></th>
              <th>Nombre</th>
              <th>Precio</th>
              <th>Stock</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {productos.map((p) => (
              <tr key={p.idProducto}>
                <td>
                  <div style={{ width: 40, height: 40, borderRadius: 'var(--radius-sm)', overflow: 'hidden', background: 'var(--primary-bg)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    {p.primeraImagenUrl ? (
                      <img src={p.primeraImagenUrl} alt={p.nombre} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                    ) : (
                      <Image size={16} color="var(--primary)" style={{ opacity: 0.4 }} />
                    )}
                  </div>
                </td>
                <td style={{ fontWeight: 600 }}>{p.nombre}</td>
                <td>${p.precio.toLocaleString('es-CO')}</td>
                <td>{p.stockDisponible}</td>
                <td>
                  <span className={`badge badge-${p.nombreEstado}`}>{p.nombreEstado}</span>
                </td>
                <td style={{ textAlign: 'right', whiteSpace: 'nowrap' }}>
                  <button
                    onClick={() => { setEditando(p); setMostrarForm(true); setRecienCreado(false); }}
                    className="btn btn-sm btn-outline-primary"
                    style={{ marginRight: 6 }}
                  >
                    Editar
                  </button>
                  <button
                    onClick={() => setConfirmId(p.idProducto)}
                    className="btn btn-sm btn-outline-danger"
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {!loading && productos.length === 0 && (
          <p style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-muted)', fontSize: 13 }}>No hay productos aún</p>
        )}
      </div>

      {confirmId !== null && (
        <ConfirmDialog
          mensaje="¿Eliminar este producto?"
          onConfirm={() => handleDelete(confirmId)}
          onCancel={() => setConfirmId(null)}
        />
      )}
    </DashboardLayout>
  );
};

export default ProductosPage;
