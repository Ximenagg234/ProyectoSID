import { useEffect, useRef, useState } from 'react';
import { X, Upload, Trash2, ImageOff, Plus } from 'lucide-react';
import * as imagenApi from '../../api/imagenApi';
import type { ImagenResponse } from '../../api/imagenApi';

interface Props {
  idProducto: number;
  nombreProducto: string;
  onClose: () => void;
}

const ImagenesProductoModal: React.FC<Props> = ({ idProducto, nombreProducto, onClose }) => {
  const [imagenes, setImagenes] = useState<ImagenResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    imagenApi
      .getImagenesProducto(idProducto)
      .then(setImagenes)
      .catch(() => setError('No se pudieron cargar las imágenes'))
      .finally(() => setLoading(false));
  }, [idProducto]);

  const cargar = () => {
    setLoading(true);
    imagenApi
      .getImagenesProducto(idProducto)
      .then(setImagenes)
      .catch(() => setError('No se pudieron cargar las imágenes'))
      .finally(() => setLoading(false));
  };

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;
    setError(null);
    setUploading(true);
    try {
      // Upload all selected files sequentially
      for (const file of Array.from(files)) {
        await imagenApi.uploadImagenProducto(idProducto, file);
      }
      cargar();
    } catch {
      setError('Error al subir la imagen');
    } finally {
      setUploading(false);
      // Reset file input so same file can be selected again
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  const handleDelete = async (idImagen: number) => {
    try {
      await imagenApi.deleteImagenProducto(idProducto, idImagen);
      setImagenes((prev) => prev.filter((i) => i.idImagen !== idImagen));
    } catch {
      setError('No se pudo eliminar la imagen');
    }
  };

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        background: 'rgba(0,0,0,.5)',
        zIndex: 9998,
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
          maxWidth: 620,
          maxHeight: '85vh',
          display: 'flex',
          flexDirection: 'column',
          overflow: 'hidden',
          boxShadow: 'var(--shadow-lg)',
        }}
      >
        {/* Header */}
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '18px 24px',
            borderBottom: '1px solid var(--border)',
          }}
        >
          <div>
            <h2 style={{ fontWeight: 700, fontSize: 16 }}>Imágenes del producto</h2>
            <p style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 2 }}>{nombreProducto}</p>
          </div>
          <button
            onClick={onClose}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)' }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Body */}
        <div style={{ flex: 1, overflowY: 'auto', padding: 24 }}>
          {error && (
            <div
              style={{
                background: 'var(--danger-bg)',
                color: 'var(--danger)',
                padding: '10px 14px',
                borderRadius: 'var(--radius-md)',
                fontSize: 13,
                marginBottom: 16,
              }}
            >
              {error}
            </div>
          )}

          {loading ? (
            <div style={{ textAlign: 'center', padding: '32px 0', color: 'var(--text-muted)' }}>
              Cargando imágenes...
            </div>
          ) : (
            <>
              {/* Image grid */}
              {imagenes.length === 0 ? (
                <div
                  style={{
                    textAlign: 'center',
                    padding: '48px 0',
                    color: 'var(--text-muted)',
                    border: '2px dashed var(--border)',
                    borderRadius: 'var(--radius-lg)',
                    marginBottom: 20,
                  }}
                >
                  <ImageOff size={40} style={{ opacity: 0.25, display: 'block', margin: '0 auto 12px' }} />
                  <p style={{ fontWeight: 600, marginBottom: 4 }}>Sin imágenes todavía</p>
                  <p style={{ fontSize: 12.5 }}>Sube la primera imagen de tu producto</p>
                </div>
              ) : (
                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))',
                    gap: 14,
                    marginBottom: 20,
                  }}
                >
                  {imagenes.map((img) => (
                    <div
                      key={img.idImagen}
                      style={{
                        borderRadius: 'var(--radius-md)',
                        overflow: 'hidden',
                        border: '1px solid var(--border)',
                        position: 'relative',
                        aspectRatio: '1',
                        background: '#F3F4F6',
                      }}
                    >
                      <img
                        src={img.urlImagen}
                        alt="Imagen del producto"
                        style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                      />
                      <button
                        onClick={() => handleDelete(img.idImagen)}
                        style={{
                          position: 'absolute',
                          top: 6,
                          right: 6,
                          background: 'rgba(239,68,68,.9)',
                          border: 'none',
                          borderRadius: '50%',
                          width: 28,
                          height: 28,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          cursor: 'pointer',
                          color: 'white',
                        }}
                        title="Eliminar imagen"
                      >
                        <Trash2 size={13} />
                      </button>
                    </div>
                  ))}

                  {/* Add more tile */}
                  <button
                    onClick={() => fileInputRef.current?.click()}
                    disabled={uploading}
                    style={{
                      borderRadius: 'var(--radius-md)',
                      border: '2px dashed var(--primary)',
                      background: 'var(--primary-bg)',
                      cursor: uploading ? 'not-allowed' : 'pointer',
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'center',
                      justifyContent: 'center',
                      gap: 8,
                      aspectRatio: '1',
                      opacity: uploading ? 0.6 : 1,
                    }}
                  >
                    <Plus size={20} color="var(--primary)" />
                    <span style={{ fontSize: 11, fontWeight: 600, color: 'var(--primary)' }}>
                      {uploading ? 'Subiendo...' : 'Agregar'}
                    </span>
                  </button>
                </div>
              )}
            </>
          )}
        </div>

        {/* Footer */}
        <div
          style={{
            padding: '16px 24px',
            borderTop: '1px solid var(--border)',
            display: 'flex',
            gap: 12,
          }}
        >
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            multiple
            style={{ display: 'none' }}
            onChange={handleUpload}
          />
          <button
            className="btn btn-primary"
            onClick={() => fileInputRef.current?.click()}
            disabled={uploading}
            style={{ opacity: uploading ? 0.7 : 1 }}
          >
            <Upload size={14} />
            {uploading ? 'Subiendo...' : 'Subir imagen(es)'}
          </button>
          <button className="btn btn-secondary" onClick={onClose}>
            Cerrar
          </button>
          <span style={{ fontSize: 12, color: 'var(--text-muted)', display: 'flex', alignItems: 'center', marginLeft: 'auto' }}>
            {imagenes.length} imagen{imagenes.length !== 1 ? 'es' : ''}
          </span>
        </div>
      </div>
    </div>
  );
};

export default ImagenesProductoModal;
