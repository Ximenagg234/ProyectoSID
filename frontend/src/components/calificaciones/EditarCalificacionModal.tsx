import { useState } from 'react';
import { X, Star } from 'lucide-react';
import { Estrellas } from './CalificacionesSection';

interface Props {
  puntuacionInicial: number;
  comentarioInicial: string;
  titulo: string;
  subtitulo?: string;
  onClose: () => void;
  onGuardar: (puntuacion: number, comentario: string) => Promise<void>;
}

const EditarCalificacionModal: React.FC<Props> = ({
  puntuacionInicial,
  comentarioInicial,
  titulo,
  subtitulo,
  onClose,
  onGuardar,
}) => {
  const [puntuacion, setPuntuacion] = useState(puntuacionInicial);
  const [comentario, setComentario] = useState(comentarioInicial ?? '');
  const [guardando, setGuardando]   = useState(false);
  const [error, setError]           = useState<string | null>(null);

  const labels = ['', 'Muy malo', 'Malo', 'Regular', 'Bueno', 'Excelente'];

  const handleGuardar = async () => {
    if (puntuacion === 0) { setError('Selecciona una puntuación'); return; }
    setGuardando(true);
    setError(null);
    try {
      await onGuardar(puntuacion, comentario);
    } catch {
      setError('Error al guardar la calificación');
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div
      style={{ position: 'fixed', inset: 0, zIndex: 1000, background: 'rgba(0,0,0,0.45)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 16 }}
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <div className="card" style={{ width: '100%', maxWidth: 460, padding: '28px 32px', position: 'relative' }}>
        <button onClick={onClose} style={{ position: 'absolute', top: 14, right: 14, background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)', padding: 4 }}>
          <X size={18} />
        </button>

        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <div style={{ width: 52, height: 52, background: '#FFFBEB', border: '2px solid #FDE68A', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 12px' }}>
            <Star size={24} fill="#F59E0B" color="#F59E0B" />
          </div>
          <h2 style={{ fontWeight: 700, fontSize: 17, marginBottom: 4 }}>{titulo}</h2>
          {subtitulo && <p style={{ fontSize: 13, color: 'var(--text-muted)' }}>{subtitulo}</p>}
        </div>

        <div style={{ textAlign: 'center', marginBottom: 8 }}>
          <div style={{ display: 'flex', justifyContent: 'center', marginBottom: 6 }}>
            <Estrellas valor={puntuacion} size={36} interactive onChange={setPuntuacion} />
          </div>
          <div style={{ fontSize: 13, color: '#D97706', fontWeight: 600, minHeight: 20 }}>
            {puntuacion > 0 ? labels[puntuacion] : 'Selecciona una puntuación'}
          </div>
        </div>

        <div style={{ marginTop: 20, marginBottom: 20 }}>
          <label style={{ fontSize: 13, fontWeight: 600, color: 'var(--text)', display: 'block', marginBottom: 6 }}>
            Comentario <span style={{ color: 'var(--text-muted)', fontWeight: 400 }}>(opcional)</span>
          </label>
          <textarea
            value={comentario}
            onChange={(e) => setComentario(e.target.value)}
            placeholder="Cuéntanos tu experiencia..."
            rows={3}
            maxLength={500}
            style={{ width: '100%', padding: '10px 12px', border: '1px solid var(--border)', borderRadius: 'var(--radius-md)', fontSize: 13, resize: 'vertical', fontFamily: 'inherit', color: 'var(--text)', boxSizing: 'border-box', outline: 'none' }}
          />
          <div style={{ fontSize: 11, color: 'var(--text-muted)', textAlign: 'right', marginTop: 2 }}>{comentario.length}/500</div>
        </div>

        {error && (
          <div style={{ background: '#FEF2F2', border: '1px solid #FECACA', borderRadius: 'var(--radius-md)', padding: '8px 12px', fontSize: 13, color: '#DC2626', marginBottom: 16 }}>
            {error}
          </div>
        )}

        <div style={{ display: 'flex', gap: 10 }}>
          <button onClick={onClose} className="btn btn-outline" style={{ flex: 1 }} disabled={guardando}>Cancelar</button>
          <button onClick={handleGuardar} className="btn btn-primary" style={{ flex: 1 }} disabled={guardando || puntuacion === 0}>
            {guardando ? 'Guardando...' : 'Guardar'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default EditarCalificacionModal;
