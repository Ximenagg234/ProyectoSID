import { useEffect, useState } from 'react';
import { Star, MessageSquare, User } from 'lucide-react';
import * as calificacionApi from '../../api/calificacionApi';
import type { CalificacionResponse } from '../../api/calificacionApi';

interface Props {
  idEmprendimiento: number;
}

const Estrellas: React.FC<{ valor: number; max?: number; size?: number; interactive?: boolean; onChange?: (v: number) => void }> = ({
  valor, max = 5, size = 16, interactive = false, onChange,
}) => {
  const [hover, setHover] = useState(0);
  return (
    <div style={{ display: 'flex', gap: 2 }}>
      {Array.from({ length: max }, (_, i) => i + 1).map((i) => (
        <Star
          key={i}
          size={size}
          fill={(interactive ? (hover || valor) : valor) >= i ? '#F59E0B' : 'none'}
          color={(interactive ? (hover || valor) : valor) >= i ? '#F59E0B' : '#D1D5DB'}
          style={{ cursor: interactive ? 'pointer' : 'default', transition: 'all 0.1s' }}
          onMouseEnter={() => interactive && setHover(i)}
          onMouseLeave={() => interactive && setHover(0)}
          onClick={() => interactive && onChange?.(i)}
        />
      ))}
    </div>
  );
};

const CalificacionesSection: React.FC<Props> = ({ idEmprendimiento }) => {
  const [calificaciones, setCalificaciones] = useState<CalificacionResponse[]>([]);
  const [promedio, setPromedio] = useState<{ promedio: number; total: number }>({ promedio: 0, total: 0 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    void (async () => {
      setLoading(true);
      try {
        const [cals, prom] = await Promise.all([
          calificacionApi.getCalificaciones(idEmprendimiento),
          calificacionApi.getPromedio(idEmprendimiento),
        ]);
        setCalificaciones(cals);
        setPromedio(prom);
      } catch {
        // API failed — show empty state
      } finally {
        setLoading(false);
      }
    })();
  }, [idEmprendimiento]);

  return (
    <div style={{ marginTop: 32 }}>
      {/* Encabezado */}
      <div
        style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          marginBottom: 20,
        }}
      >
        <h2 style={{ fontWeight: 700, fontSize: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
          <MessageSquare size={16} color="var(--primary)" />
          Reseñas y calificaciones
        </h2>

        {/* Promedio general */}
        {promedio.total > 0 && (
          <div
            style={{
              display: 'flex', alignItems: 'center', gap: 10,
              background: '#FFFBEB', border: '1px solid #FDE68A',
              borderRadius: 'var(--radius-md)', padding: '8px 16px',
            }}
          >
            <span style={{ fontSize: 26, fontWeight: 900, color: '#D97706', lineHeight: 1 }}>
              {promedio.promedio.toFixed(1)}
            </span>
            <div>
              <Estrellas valor={Math.round(promedio.promedio)} size={14} />
              <div style={{ fontSize: 11, color: '#92400E', marginTop: 2 }}>
                {promedio.total} reseña{promedio.total !== 1 ? 's' : ''}
              </div>
            </div>
          </div>
        )}
      </div>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '32px 0', color: 'var(--text-muted)', fontSize: 13 }}>
          Cargando reseñas...
        </div>
      ) : calificaciones.length === 0 ? (
        <div
          style={{
            textAlign: 'center', padding: '40px 0',
            color: 'var(--text-muted)',
            background: '#FAFAFA',
            borderRadius: 'var(--radius-lg)',
            border: '1px dashed var(--border)',
          }}
        >
          <Star size={36} style={{ opacity: 0.2, display: 'block', margin: '0 auto 12px' }} />
          <p style={{ fontWeight: 600 }}>Aún no hay reseñas</p>
          <p style={{ fontSize: 13, marginTop: 4 }}>
            Realiza un pedido y, una vez entregado, podrás calificar este emprendimiento.
          </p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          {calificaciones.map((cal) => (
            <div
              key={cal.idCalificacion}
              className="card"
              style={{ padding: '16px 20px' }}
            >
              <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: 8 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <div
                    style={{
                      width: 36, height: 36, borderRadius: '50%',
                      background: 'var(--primary-bg)',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                      flexShrink: 0,
                    }}
                  >
                    <User size={16} color="var(--primary)" />
                  </div>
                  <div>
                    <div style={{ fontWeight: 700, fontSize: 13 }}>{cal.nombreUsuario}</div>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>
                      {new Date(cal.fecha).toLocaleDateString('es-CO', {
                        year: 'numeric', month: 'long', day: 'numeric',
                      })}
                    </div>
                  </div>
                </div>
                <Estrellas valor={cal.puntuacion} size={15} />
              </div>

              {cal.comentario && (
                <p
                  style={{
                    fontSize: 13.5, color: 'var(--text)',
                    lineHeight: 1.6,
                    margin: 0,
                    paddingLeft: 46,
                  }}
                >
                  {cal.comentario}
                </p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export { Estrellas };
export default CalificacionesSection;
