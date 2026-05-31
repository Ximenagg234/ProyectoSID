import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import * as emprendimientoApi from '../../api/emprendimientoApi';
import type { EmprendimientoRequest } from '../../types/emprendimiento.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import EmprendimientoForm from '../../components/emprendimientos/EmprendimientoForm';
import ErrorMessage from '../../components/common/ErrorMessage';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { useAuth } from '../../hooks/useAuth';

const EmprendimientoFormPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { idUsuario } = useAuth();
  const isEditing = Boolean(id);

  const [inicial, setInicial] = useState<Partial<EmprendimientoRequest> | undefined>(undefined);
  const [loading, setLoading] = useState(false);
  const [cargando, setCargando] = useState(isEditing);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!isEditing || !id) return;
    emprendimientoApi
      .getById(Number(id))
      .then((emp) => {
        setInicial({
          nombre: emp.nombre,
          descripcion: emp.descripcion,
          logoUrl: emp.logoUrl,
        });
      })
      .catch(() => setError('Error al cargar el emprendimiento'))
      .finally(() => setCargando(false));
  }, [id, isEditing]);

  const handleSubmit = async (data: EmprendimientoRequest) => {
    setLoading(true);
    setError(null);
    try {
      if (isEditing && id) {
        await emprendimientoApi.update(Number(id), data);
      } else {
        await emprendimientoApi.create(data);
      }
      navigate('/mis-emprendimientos');
    } catch {
      setError('No se pudo guardar el emprendimiento');
    } finally {
      setLoading(false);
    }
  };

  return (
    <DashboardLayout
      title={isEditing ? 'Editar Emprendimiento' : 'Nuevo Emprendimiento'}
      subtitle={isEditing ? 'Modifica los datos de tu emprendimiento' : 'Crea un nuevo emprendimiento'}
    >
      <Link
        to="/mis-emprendimientos"
        style={{ fontSize: 13, color: 'var(--primary)', textDecoration: 'none', fontWeight: 600, display: 'inline-block', marginBottom: 24 }}
      >
        ← Volver a mis emprendimientos
      </Link>

      {/* Two-column layout: form left, tips right */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 320px', gap: 24, alignItems: 'start' }}>
        <div className="card">
          {error && <ErrorMessage mensaje={error} />}
          {cargando ? (
            <LoadingSpinner />
          ) : (
            <EmprendimientoForm
              inicial={inicial}
              onSubmit={handleSubmit}
              loading={loading}
              idUsuario={idUsuario ?? 0}
            />
          )}
        </div>

        {/* Tips panel */}
        <div className="card" style={{ background: 'var(--primary-bg)', border: '1px solid #C7D2FE' }}>
          <h3 style={{ fontWeight: 700, fontSize: 14, color: 'var(--primary)', marginBottom: 16 }}>
            💡 Consejos
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            {[
              { emoji: '🏷️', tip: 'Elige un nombre memorable y fácil de pronunciar.' },
              { emoji: '📝', tip: 'La descripción debe explicar qué vendes y a quién va dirigido.' },
              { emoji: '🖼️', tip: 'Un buen logo genera confianza. Usa imagen cuadrada de al menos 200×200px.' },
              { emoji: '📂', tip: 'Selecciona la categoría correcta para aparecer en los filtros del marketplace.' },
            ].map(({ emoji, tip }) => (
              <div key={tip} style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
                <span style={{ fontSize: 18, flexShrink: 0 }}>{emoji}</span>
                <p style={{ fontSize: 12.5, color: 'var(--text-muted)', lineHeight: 1.5 }}>{tip}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default EmprendimientoFormPage;
