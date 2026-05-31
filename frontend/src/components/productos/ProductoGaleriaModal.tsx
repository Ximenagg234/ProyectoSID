import { useEffect, useState } from 'react';
import { X, ChevronLeft, ChevronRight, ImageOff } from 'lucide-react';
import * as imagenApi from '../../api/imagenApi';
import type { ImagenResponse } from '../../api/imagenApi';

interface Props {
  idProducto: number;
  nombreProducto: string;
  primeraImagenUrl?: string;
  onClose: () => void;
}

const ProductoGaleriaModal: React.FC<Props> = ({ idProducto, nombreProducto, primeraImagenUrl, onClose }) => {
  const [imagenes, setImagenes] = useState<ImagenResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [current, setCurrent] = useState(0);

  useEffect(() => {
    imagenApi
      .getImagenesProducto(idProducto)
      .then((imgs) => {
        setImagenes(imgs);
        setCurrent(0);
      })
      .catch(() => {
        // Si no hay imágenes desde la API, intentar mostrar la primera imagen
        if (primeraImagenUrl) {
          setImagenes([{ idImagen: -1, urlImagen: primeraImagenUrl }]);
        }
      })
      .finally(() => setLoading(false));
  }, [idProducto, primeraImagenUrl]);

  // Cerrar con Escape
  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
      if (e.key === 'ArrowLeft') setCurrent((c) => Math.max(0, c - 1));
      if (e.key === 'ArrowRight') setCurrent((c) => Math.min(imagenes.length - 1, c + 1));
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [onClose, imagenes.length]);

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        background: 'rgba(0,0,0,0.85)',
        zIndex: 9999,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        padding: 16,
      }}
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <div
        style={{
          background: 'white',
          borderRadius: 'var(--radius-xl)',
          width: '100%',
          maxWidth: 720,
          maxHeight: '90vh',
          display: 'flex',
          flexDirection: 'column',
          overflow: 'hidden',
          boxShadow: '0 25px 60px rgba(0,0,0,0.5)',
        }}
      >
        {/* Header */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '14px 20px',
            borderBottom: '1px solid var(--border)',
          }}
        >
          <div>
            <h3 style={{ fontWeight: 700, fontSize: 15 }}>{nombreProducto}</h3>
            {!loading && imagenes.length > 0 && (
              <p style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 1 }}>
                {current + 1} / {imagenes.length}
              </p>
            )}
          </div>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)', padding: 4 }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Main image */}
        <div
          style={{
            flex: 1,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            background: '#F9FAFB',
            position: 'relative',
            minHeight: 320,
          }}
        >
          {loading ? (
            <div style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: 13 }}>
              <div style={{ width: 24, height: 24, border: '3px solid var(--border)', borderTopColor: 'var(--primary)', borderRadius: '50%', animation: 'spin 0.7s linear infinite', margin: '0 auto 12px' }} />
              Cargando imágenes...
            </div>
          ) : imagenes.length === 0 ? (
            <div style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
              <ImageOff size={48} style={{ opacity: 0.2, display: 'block', margin: '0 auto 12px' }} />
              <p style={{ fontWeight: 600 }}>Sin imágenes disponibles</p>
            </div>
          ) : (
            <>
              <img
                src={imagenes[current].urlImagen}
                alt={`${nombreProducto} - foto ${current + 1}`}
                style={{
                  maxWidth: '100%',
                  maxHeight: 420,
                  objectFit: 'contain',
                  display: 'block',
                  borderRadius: 'var(--radius-md)',
                }}
              />

              {/* Arrow left */}
              {current > 0 && (
                <button
                  onClick={() => setCurrent((c) => c - 1)}
                  style={{
                    position: 'absolute',
                    left: 12,
                    background: 'white',
                    border: '1px solid var(--border)',
                    borderRadius: '50%',
                    width: 40,
                    height: 40,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    cursor: 'pointer',
                    boxShadow: 'var(--shadow-md)',
                  }}
                >
                  <ChevronLeft size={20} />
                </button>
              )}

              {/* Arrow right */}
              {current < imagenes.length - 1 && (
                <button
                  onClick={() => setCurrent((c) => c + 1)}
                  style={{
                    position: 'absolute',
                    right: 12,
                    background: 'white',
                    border: '1px solid var(--border)',
                    borderRadius: '50%',
                    width: 40,
                    height: 40,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    cursor: 'pointer',
                    boxShadow: 'var(--shadow-md)',
                  }}
                >
                  <ChevronRight size={20} />
                </button>
              )}
            </>
          )}
        </div>

        {/* Thumbnail strip */}
        {imagenes.length > 1 && (
          <div
            style={{
              display: 'flex',
              gap: 8,
              padding: '12px 16px',
              borderTop: '1px solid var(--border)',
              overflowX: 'auto',
              background: 'white',
            }}
          >
            {imagenes.map((img, idx) => (
              <button
                key={img.idImagen}
                onClick={() => setCurrent(idx)}
                style={{
                  width: 60,
                  height: 60,
                  borderRadius: 'var(--radius-sm)',
                  overflow: 'hidden',
                  flexShrink: 0,
                  border: idx === current ? '2.5px solid var(--primary)' : '2px solid var(--border)',
                  cursor: 'pointer',
                  padding: 0,
                  background: '#F3F4F6',
                  opacity: idx === current ? 1 : 0.65,
                  transition: 'all 0.15s',
                }}
              >
                <img
                  src={img.urlImagen}
                  alt={`Miniatura ${idx + 1}`}
                  style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                />
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default ProductoGaleriaModal;
