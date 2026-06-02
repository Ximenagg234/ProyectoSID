import { useEffect, useRef, useState } from 'react';
import { X, Upload, Trash2, ImageOff, Plus, CheckCircle2 } from 'lucide-react';
import type { ProductoRequest } from '../../types/producto.types';
import * as imagenApi from '../../api/imagenApi';
import type { ImagenResponse } from '../../api/imagenApi';

interface Props {
  inicial?: Partial<ProductoRequest>;
  idEmprendimiento: number;
  idProducto?: number;           // undefined = creando, number = editando
  onSubmit: (data: ProductoRequest) => Promise<void>;
  onCancel: () => void;
  loading: boolean;
  recienCreado?: boolean;        // flag to show "saved!" banner after create
}

// ── Inline image manager ──────────────────────────────────────────
const GestorImagenes: React.FC<{ idProducto: number }> = ({ idProducto }) => {
  const [imagenes, setImagenes] = useState<ImagenResponse[]>([]);
  const [cargando, setCargando] = useState(true);
  const [subiendo, setSubiendo] = useState(false);
  const [error, setError]       = useState<string | null>(null);
  const fileRef = useRef<HTMLInputElement>(null);

  const cargar = () => {
    setCargando(true);
    imagenApi.getImagenesProducto(idProducto)
      .then(setImagenes)
      .catch(() => setError('No se pudieron cargar las imágenes'))
      .finally(() => setCargando(false));
  };

  useEffect(() => { cargar(); }, [idProducto]);

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;
    setError(null);
    setSubiendo(true);
    try {
      for (const file of Array.from(files)) {
        await imagenApi.uploadImagenProducto(idProducto, file);
      }
      cargar();
    } catch {
      setError('Error al subir la imagen');
    } finally {
      setSubiendo(false);
      if (fileRef.current) fileRef.current.value = '';
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
    <div style={{ marginTop: 24, borderTop: '1px solid var(--border)', paddingTop: 20 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 14 }}>
        <label className="form-label" style={{ margin: 0 }}>
          Fotos del producto
          <span style={{ marginLeft: 6, fontWeight: 400, color: 'var(--text-muted)' }}>
            ({imagenes.length} imagen{imagenes.length !== 1 ? 'es' : ''})
          </span>
        </label>
        <button
          type="button"
          className="btn btn-sm btn-outline"
          onClick={() => fileRef.current?.click()}
          disabled={subiendo}
          style={{ display: 'flex', alignItems: 'center', gap: 5, fontSize: 12 }}
        >
          <Upload size={13} />
          {subiendo ? 'Subiendo...' : 'Subir fotos'}
        </button>
      </div>

      <input
        ref={fileRef}
        type="file"
        accept="image/*"
        multiple
        style={{ display: 'none' }}
        onChange={handleUpload}
      />

      {error && (
        <div style={{ background: '#FEF2F2', border: '1px solid #FECACA', borderRadius: 'var(--radius-md)', padding: '8px 12px', fontSize: 12, color: '#DC2626', marginBottom: 12 }}>
          {error}
        </div>
      )}

      {cargando ? (
        <div style={{ fontSize: 12, color: 'var(--text-muted)', padding: '12px 0' }}>Cargando imágenes...</div>
      ) : imagenes.length === 0 ? (
        <button
          type="button"
          onClick={() => fileRef.current?.click()}
          disabled={subiendo}
          style={{
            width: '100%', padding: '28px 0', border: '2px dashed var(--border)', borderRadius: 'var(--radius-lg)',
            background: 'white', cursor: 'pointer', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8,
            color: 'var(--text-muted)', fontSize: 13,
          }}
        >
          <ImageOff size={28} style={{ opacity: 0.3 }} />
          <span>Sin fotos — haz clic para subir la primera</span>
        </button>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(90px, 1fr))', gap: 10 }}>
          {imagenes.map((img) => (
            <div
              key={img.idImagen}
              style={{ position: 'relative', aspectRatio: '1', borderRadius: 'var(--radius-md)', overflow: 'hidden', border: '1px solid var(--border)', background: '#F3F4F6' }}
            >
              <img src={img.urlImagen} alt="" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
              <button
                type="button"
                onClick={() => handleDelete(img.idImagen)}
                style={{
                  position: 'absolute', top: 4, right: 4,
                  background: 'rgba(239,68,68,.9)', border: 'none', borderRadius: '50%',
                  width: 24, height: 24, display: 'flex', alignItems: 'center', justifyContent: 'center',
                  cursor: 'pointer', color: 'white',
                }}
                title="Eliminar"
              >
                <Trash2 size={11} />
              </button>
            </div>
          ))}

          {/* Add tile */}
          <button
            type="button"
            onClick={() => fileRef.current?.click()}
            disabled={subiendo}
            style={{
              aspectRatio: '1', borderRadius: 'var(--radius-md)', border: '2px dashed var(--primary)',
              background: 'var(--primary-bg)', cursor: subiendo ? 'not-allowed' : 'pointer',
              display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
              gap: 4, opacity: subiendo ? 0.6 : 1,
            }}
          >
            <Plus size={18} color="var(--primary)" />
            <span style={{ fontSize: 10, fontWeight: 600, color: 'var(--primary)' }}>
              {subiendo ? '...' : 'Agregar'}
            </span>
          </button>
        </div>
      )}
    </div>
  );
};

// ── Main form ─────────────────────────────────────────────────────
const ProductoForm: React.FC<Props> = ({
  inicial, idEmprendimiento, idProducto, onSubmit, onCancel, loading, recienCreado,
}) => {
  const [form, setForm] = useState<ProductoRequest>({
    nombre:           inicial?.nombre           ?? '',
    descripcion:      inicial?.descripcion      ?? '',
    precio:           inicial?.precio           ?? 0,
    stockDisponible:  inicial?.stockDisponible  ?? 0,
    idEmprendimiento,
    idEstado:         inicial?.idEstado         ?? 1,
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: ['precio', 'stockDisponible', 'idEstado'].includes(name) ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onSubmit(form);
  };

  return (
    <div
      className="card"
      style={{ border: '2px solid var(--primary)', marginBottom: 24, background: 'var(--primary-bg)' }}
    >
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <h3 style={{ fontWeight: 700, fontSize: 15, color: 'var(--primary)' }}>
          {idProducto ? '✏️ Editar producto' : '+ Nuevo producto'}
        </h3>
        <button type="button" onClick={onCancel} style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)' }}>
          <X size={18} />
        </button>
      </div>

      {/* "Saved!" banner shown after creating */}
      {recienCreado && (
        <div style={{
          display: 'flex', alignItems: 'center', gap: 8,
          background: '#D1FAE5', border: '1px solid #6EE7B7',
          borderRadius: 'var(--radius-md)', padding: '10px 14px',
          fontSize: 13, color: '#065F46', fontWeight: 600, marginBottom: 16,
        }}>
          <CheckCircle2 size={16} />
          Producto guardado. Ahora puedes agregar las fotos.
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
          <div>
            <label className="form-label">Nombre del producto *</label>
            <input className="form-input" name="nombre" value={form.nombre} onChange={handleChange} required placeholder="Ej: Mouse inalámbrico" style={{ background: 'white' }} />
          </div>
          <div>
            <label className="form-label">Precio (COP) *</label>
            <input className="form-input" name="precio" type="number" min={0} value={form.precio} onChange={handleChange} required placeholder="0" style={{ background: 'white' }} />
          </div>
          <div>
            <label className="form-label">Stock disponible *</label>
            <input className="form-input" name="stockDisponible" type="number" min={0} value={form.stockDisponible} onChange={handleChange} required style={{ background: 'white' }} />
          </div>
          <div style={{ gridColumn: '1 / -1' }}>
            <label className="form-label">Descripción</label>
            <textarea className="form-input" name="descripcion" value={form.descripcion} onChange={handleChange} rows={3} placeholder="Describe el producto..." style={{ resize: 'vertical', background: 'white' }} />
          </div>
        </div>

        <div style={{ display: 'flex', gap: 10, marginTop: 20 }}>
          <button type="submit" className="btn btn-primary" disabled={loading} style={{ opacity: loading ? 0.7 : 1 }}>
            {loading ? 'Guardando...' : '✓ Guardar'}
          </button>
          <button type="button" className="btn btn-secondary" onClick={onCancel}>
            {idProducto ? 'Cerrar' : 'Cancelar'}
          </button>
        </div>
      </form>

      {/* Image manager — only shown when product already exists */}
      {idProducto ? (
        <GestorImagenes idProducto={idProducto} />
      ) : (
        <div style={{ marginTop: 20, borderTop: '1px solid var(--border)', paddingTop: 16, fontSize: 12, color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: 6 }}>
          <ImageOff size={14} style={{ flexShrink: 0 }} />
          Guarda el producto primero para poder agregar fotos.
        </div>
      )}
    </div>
  );
};

export default ProductoForm;
