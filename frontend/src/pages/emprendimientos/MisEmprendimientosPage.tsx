import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import * as emprendimientoApi from '../../api/emprendimientoApi';
import type { EmprendimientoResponse } from '../../types/emprendimiento.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import EmprendimientoCard from '../../components/emprendimientos/EmprendimientoCard';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { useAuth } from '../../hooks/useAuth';
import { Building2, Plus } from 'lucide-react';

const MisEmprendimientosPage: React.FC = () => {
  const { correo } = useAuth();
  const [emprendimientos, setEmprendimientos] = useState<EmprendimientoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [confirmId, setConfirmId] = useState<number | null>(null);

  useEffect(() => {
    emprendimientoApi.getAll()
      .then((todos) => {
        const mios = todos.filter(
          (e) =>
            correo &&
            (e.nombreUsuario === correo ||
              e.nombreUsuario?.toLowerCase().includes(correo.split('@')[0].toLowerCase()))
        );
        setEmprendimientos(mios);
      })
      .catch(() => setError('Error al cargar tus emprendimientos'))
      .finally(() => setLoading(false));
  }, [correo]);

  const handleDelete = async (id: number) => {
    try {
      await emprendimientoApi.remove(id);
      setEmprendimientos((prev) => prev.filter((e) => e.idEmprendimiento !== id));
    } catch {
      setError('No se pudo eliminar el emprendimiento');
    } finally {
      setConfirmId(null);
    }
  };

  return (
    <DashboardLayout
      title="Mis Emprendimientos"
      subtitle="Gestiona tus emprendimientos y productos"
      action={
        <Link to="/mis-emprendimientos/nuevo" className="btn btn-primary">
          <Plus size={15} /> Nuevo emprendimiento
        </Link>
      }
    >
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {!loading && !error && emprendimientos.length === 0 && (
        <div style={{ textAlign: 'center', padding: '64px 0' }}>
          <div className="card" style={{ maxWidth: 400, margin: '0 auto', textAlign: 'center' }}>
            <div style={{ width: 64, height: 64, background: 'var(--primary-bg)', borderRadius: 'var(--radius-lg)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 16px' }}>
              <Building2 size={32} color="var(--primary)" style={{ opacity: 0.6 }} />
            </div>
            <p style={{ fontWeight: 600, fontSize: 16, marginBottom: 8 }}>Aún no tienes emprendimientos</p>
            <p style={{ color: 'var(--text-muted)', fontSize: 13, marginBottom: 24 }}>
              Crea tu primer emprendimiento y comienza a vender tus productos
            </p>
            <Link to="/mis-emprendimientos/nuevo" className="btn btn-primary">
              <Plus size={15} /> Crear emprendimiento
            </Link>
          </div>
        </div>
      )}

      {emprendimientos.length > 0 && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 24 }}>
          {emprendimientos.map((emp) => (
            <EmprendimientoCard
              key={emp.idEmprendimiento}
              emprendimiento={emp}
              showActions={true}
              onDelete={(id) => setConfirmId(id)}
            />
          ))}
        </div>
      )}

      {confirmId !== null && (
        <ConfirmDialog
          mensaje="¿Seguro que quieres eliminar este emprendimiento? Esta acción no se puede deshacer."
          onConfirm={() => handleDelete(confirmId)}
          onCancel={() => setConfirmId(null)}
        />
      )}
    </DashboardLayout>
  );
};

export default MisEmprendimientosPage;
