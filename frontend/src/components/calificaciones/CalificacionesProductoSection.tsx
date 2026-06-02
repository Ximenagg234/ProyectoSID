import { useEffect, useState } from 'react';
import { Star, MessageSquare, User, Pencil, Trash2 } from 'lucide-react';
import * as calApi from '../../api/calificacionProductoApi';
import type { CalificacionProductoResponse } from '../../api/calificacionProductoApi';
import { useAuth } from '../../hooks/useAuth';
import { Estrellas } from './CalificacionesSection';
import EditarCalificacionModal from './EditarCalificacionModal';
import CalificarProductoModal from './CalificarProductoModal';

interface Props {
  idProducto: number;
  nombreProducto: string;
}

const CalificacionesProductoSection: React.FC<Props> = ({ idProducto, nombreProducto }) => {
  const { idUsuario, isAuthenticated } = useAuth();
  const [calificaciones, setCalificaciones] = useState<CalificacionProductoResponse[]>([]);
  const [promedio, setPromedio]             = useState<{ promedio: number; total: number }>({ promedio: 0, total: 0 });
  const [loading, setLoading]               = useState(true);
  const [yaCalifico, setYaCalifico]         = useState(false);
  const [editando, setEditando]             = useState<CalificacionProductoResponse | null>(null);
  const [eliminando, setEliminando]         = useState<number | null>(null);
  const [mostrarModal, setMostrarModal]     = useState(false);

  const cargar = async () => {
    setLoading(true);
    try {
      const [cals, prom] = await Promise.all([
        calApi.getCalificacionesProducto(idProducto),
        calApi.getPromedioProducto(idProducto),
      ]);
      setCalificaciones(cals);
      setPromedio(prom);
      if (idUsuario) {
        const { yaCalifico: ya } = await calApi.yaCalificoProducto(idProducto, idUsuario);
        setYaCalifico(ya);
      }
    } catch {
      // show empty state
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { void cargar(); }, [idProducto, idUsuario]);

  const handleEditar = async (puntuacion: number, comentario: string) => {
    if (!editando || !idUsuario) return;
    await calApi.actualizarProducto(idProducto, editando.idCalificacionProducto, {
      puntuacion, comentario, idUsuario,
    });
    setEditando(null);
    void cargar();
  };

  const handleEliminar = async (idCalificacion: number) => {
    if (!idUsuario) return;
    setEliminando(idCalificacion);
    try {
      await calApi.eliminarProducto(idProducto, idCalificacion, idUsuario);
      void cargar();
    } finally {
      setEliminando(null);
    }
  };

  return (
    <div style={{ marginTop: 20 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 14 }}>
        <h3 style={{ fontWeight: 700, fontSize: 14, display: 'flex', alignItems: 'center', gap: 6 }}>
          <MessageSquare size={14} color="var(--primary)" />
          Reseñas del producto
        </h3>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          {promedio.total > 0 && (
            <div style={{ display: 'flex', alignItems: 'center', gap: 6, background: '#FFFBEB', border: '1px solid #FDE68A', borderRadius: 'var(--radius-md)', padding: '4px 10px' }}>
              <span style={{ fontSize: 16, fontWeight: 900, color: '#D97706' }}>{promedio.promedio.toFixed(1)}</span>
              <Estrellas valor={Math.round(promedio.promedio)} size={12} />
              <span style={{ fontSize: 11, color: '#92400E' }}>({promedio.total})</span>
            </div>
          )}
          {isAuthenticated && !yaCalifico && (
            <button onClick={() => setMostrarModal(true)} className="btn btn-outline" style={{ fontSize: 12, padding: '5px 12px', display: 'flex', alignItems: 'center', gap: 5, borderColor: '#F59E0B', color: '#D97706' }}>
              <Star size={12} /> Calificar
            </button>
          )}
          {yaCalifico && (
            <span style={{ fontSize: 11, color: '#92400E', background: '#FFFBEB', border: '1px solid #FDE68A', borderRadius: 999, padding: '3px 8px' }}>
              Ya calificaste
            </span>
          )}
        </div>
      </div>

      {loading ? (
        <div style={{ fontSize: 12, color: 'var(--text-muted)', padding: '8px 0' }}>Cargando reseñas...</div>
      ) : calificaciones.length === 0 ? (
        <div style={{ fontSize: 12, color: 'var(--text-muted)', padding: '8px 0', textAlign: 'center' }}>
          Sin reseñas aún — ¡sé el primero en calificarlo!
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {calificaciones.map((cal) => {
            const esMia = idUsuario === cal.idUsuario;
            return (
              <div key={cal.idCalificacionProducto} style={{ background: '#FAFAFA', borderRadius: 'var(--radius-md)', padding: '10px 14px', border: '1px solid var(--border)' }}>
                <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: 4 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <div style={{ width: 28, height: 28, borderRadius: '50%', background: 'var(--primary-bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                      <User size={12} color="var(--primary)" />
                    </div>
                    <div>
                      <span style={{ fontWeight: 700, fontSize: 12 }}>
                        {cal.nombreUsuario}
                        {esMia && <span style={{ marginLeft: 5, fontSize: 10, background: 'var(--primary-bg)', color: 'var(--primary)', padding: '1px 5px', borderRadius: 999 }}>Tú</span>}
                      </span>
                      <div style={{ fontSize: 10, color: 'var(--text-muted)' }}>
                        {new Date(cal.fecha).toLocaleDateString('es-CO', { year: 'numeric', month: 'short', day: 'numeric' })}
                      </div>
                    </div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                    <Estrellas valor={cal.puntuacion} size={12} />
                    {esMia && (
                      <div style={{ display: 'flex', gap: 2 }}>
                        <button onClick={() => setEditando(cal)} title="Editar" style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--primary)', padding: 3 }}>
                          <Pencil size={12} />
                        </button>
                        <button onClick={() => handleEliminar(cal.idCalificacionProducto)} disabled={eliminando === cal.idCalificacionProducto} title="Eliminar" style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--danger)', padding: 3 }}>
                          <Trash2 size={12} />
                        </button>
                      </div>
                    )}
                  </div>
                </div>
                {cal.comentario && (
                  <p style={{ fontSize: 12, color: 'var(--text)', lineHeight: 1.5, margin: 0, paddingLeft: 36 }}>
                    {cal.comentario}
                  </p>
                )}
              </div>
            );
          })}
        </div>
      )}

      {mostrarModal && idUsuario && (
        <CalificarProductoModal
          idProducto={idProducto}
          nombreProducto={nombreProducto}
          idUsuario={idUsuario}
          onClose={() => setMostrarModal(false)}
          onSuccess={() => { setMostrarModal(false); void cargar(); }}
        />
      )}

      {editando && (
        <EditarCalificacionModal
          puntuacionInicial={editando.puntuacion}
          comentarioInicial={editando.comentario ?? ''}
          titulo="Editar reseña del producto"
          subtitulo={nombreProducto}
          onClose={() => setEditando(null)}
          onGuardar={handleEditar}
        />
      )}
    </div>
  );
};

export default CalificacionesProductoSection;
