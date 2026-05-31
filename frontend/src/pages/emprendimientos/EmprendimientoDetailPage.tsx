import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import * as emprendimientoApi from '../../api/emprendimientoApi';
import * as productoApi from '../../api/productoApi';
import type { EmprendimientoResponse } from '../../types/emprendimiento.types';
import type { ProductoResponse } from '../../types/producto.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import ProductoCard from '../../components/productos/ProductoCard';
import CalificacionesSection from '../../components/calificaciones/CalificacionesSection';
import { Building2, User, CalendarDays, CheckCircle2, Package } from 'lucide-react';

const EmprendimientoDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [emprendimiento, setEmprendimiento] = useState<EmprendimientoResponse | null>(null);
  const [productos, setProductos] = useState<ProductoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const cargar = async () => {
      try {
        const idNum = Number(id);
        const [emp, prods] = await Promise.all([
          emprendimientoApi.getById(idNum),
          productoApi.getAll(),
        ]);
        setEmprendimiento(emp);
        // Filter by idEmprendimiento (reliable) and only show ACTIVO products
        setProductos(
          prods.filter(
            (p) => p.idEmprendimiento === idNum && p.nombreEstado !== 'INACTIVO'
          )
        );
      } catch {
        setError('No se pudo cargar el emprendimiento');
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, [id]);

  return (
    <DashboardLayout title={emprendimiento?.nombre} subtitle={emprendimiento?.nombreCategoria}>
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {emprendimiento && (
        <>
          {/* Info card */}
          <div className="card" style={{ textAlign: 'center', marginBottom: 28 }}>
            <div style={{ marginBottom: 20 }}>
              {emprendimiento.logoUrl ? (
                <img
                  src={emprendimiento.logoUrl}
                  alt={emprendimiento.nombre}
                  style={{ width: 100, height: 100, borderRadius: 'var(--radius-lg)', objectFit: 'cover', border: '3px solid var(--border)', margin: '0 auto' }}
                />
              ) : (
                <div style={{ width: 100, height: 100, borderRadius: 'var(--radius-lg)', background: 'var(--primary-bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto' }}>
                  <Building2 size={48} color="var(--primary)" style={{ opacity: 0.5 }} />
                </div>
              )}
            </div>
            <span className="badge badge-primary" style={{ marginBottom: 12 }}>{emprendimiento.nombreCategoria}</span>
            <h1 style={{ fontSize: 24, fontWeight: 800, marginBottom: 8 }}>{emprendimiento.nombre}</h1>
            <p style={{ color: 'var(--text-muted)', maxWidth: 600, margin: '0 auto 24px', fontSize: 14 }}>{emprendimiento.descripcion}</p>

            <div style={{ display: 'flex', justifyContent: 'center', gap: 40 }}>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4 }}>
                <User size={18} color="var(--primary)" />
                <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 500 }}>{emprendimiento.nombreUsuario}</span>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4 }}>
                <CalendarDays size={18} color="var(--primary)" />
                <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 500 }}>{emprendimiento.nombreSemestre}</span>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4 }}>
                <CheckCircle2 size={18} color={emprendimiento.nombreEstado === 'ACTIVO' ? 'var(--secondary)' : 'var(--text-muted)'} />
                <span className={`badge badge-${emprendimiento.nombreEstado}`}>{emprendimiento.nombreEstado}</span>
              </div>
            </div>
          </div>

          {/* Productos section */}
          <h2 style={{ fontWeight: 700, fontSize: 16, marginBottom: 20 }}>Productos disponibles</h2>
          {productos.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '48px 0', color: 'var(--text-muted)', background: 'white', borderRadius: 'var(--radius-xl)', border: '1px solid var(--border)' }}>
              <Package size={40} style={{ opacity: 0.3, display: 'block', margin: '0 auto 12px' }} />
              <p style={{ fontWeight: 600 }}>Este emprendimiento aún no tiene productos</p>
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', gap: 20 }}>
              {productos.map((p) => (
                <ProductoCard key={p.idProducto} producto={p} emprendimiento={emprendimiento ?? undefined} />
              ))}
            </div>
          )}

          {/* Reviews & ratings section */}
          <CalificacionesSection idEmprendimiento={Number(id)} />
        </>
      )}
    </DashboardLayout>
  );
};

export default EmprendimientoDetailPage;
