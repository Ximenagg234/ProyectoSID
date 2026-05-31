import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import * as productoApi from '../../api/productoApi';
import type { ProductoResponse, ProductoRequest } from '../../types/producto.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import ProductoForm from '../../components/productos/ProductoForm';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import ImagenesProductoModal from '../../components/productos/ImagenesProductoModal';
import { Plus, Image } from 'lucide-react';

const ProductosPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const idEmprendimiento = Number(id);

  const [productos, setProductos] = useState<ProductoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [mostrarForm, setMostrarForm] = useState(false);
  const [editando, setEditando] = useState<ProductoResponse | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [confirmId, setConfirmId] = useState<number | null>(null);
  const [imagenModalProducto, setImagenModalProducto] = useState<ProductoResponse | null>(null);

  const cargar = () => {
    productoApi.getAll()
      .then((todos) => setProductos(todos))
      .catch(() => setError('Error al cargar los productos'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { cargar(); }, []);

  const handleSave = async (data: ProductoRequest) => {
    setGuardando(true);
    try {
      if (editando) {
        await productoApi.update(editando.idProducto, data);
      } else {
        await productoApi.create({ ...data, idEmprendimiento });
      }
      await cargar();
      setMostrarForm(false);
      setEditando(null);
    } catch {
      setError('No se pudo guardar el producto');
    } finally {
      setGuardando(false);
    }
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
          onClick={() => { setMostrarForm(true); setEditando(null); }}
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
            inicial={editando ? { nombre: editando.nombre, descripcion: editando.descripcion, precio: editando.precio, stockDisponible: editando.stockDisponible } : undefined}
            idEmprendimiento={idEmprendimiento}
            onSubmit={handleSave}
            onCancel={() => { setMostrarForm(false); setEditando(null); }}
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
                    onClick={() => setImagenModalProducto(p)}
                    className="btn btn-sm btn-secondary"
                    style={{ marginRight: 6 }}
                    title="Gestionar imágenes"
                  >
                    <Image size={13} /> Imágenes
                  </button>
                  <button
                    onClick={() => { setEditando(p); setMostrarForm(true); }}
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

      {imagenModalProducto && (
        <ImagenesProductoModal
          idProducto={imagenModalProducto.idProducto}
          nombreProducto={imagenModalProducto.nombre}
          onClose={() => {
            setImagenModalProducto(null);
            cargar(); // Refresh so primeraImagenUrl updates
          }}
        />
      )}
    </DashboardLayout>
  );
};

export default ProductosPage;
