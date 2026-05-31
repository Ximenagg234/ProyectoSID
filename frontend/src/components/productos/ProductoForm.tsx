import { useState } from 'react';
import { X } from 'lucide-react';
import type { ProductoRequest } from '../../types/producto.types';

interface Props {
  inicial?: Partial<ProductoRequest>;
  idEmprendimiento: number;
  onSubmit: (data: ProductoRequest) => Promise<void>;
  onCancel: () => void;
  loading: boolean;
}

const ProductoForm: React.FC<Props> = ({ inicial, idEmprendimiento, onSubmit, onCancel, loading }) => {
  const [form, setForm] = useState<ProductoRequest>({
    nombre: inicial?.nombre ?? '',
    descripcion: inicial?.descripcion ?? '',
    precio: inicial?.precio ?? 0,
    stockDisponible: inicial?.stockDisponible ?? 0,
    idEmprendimiento,
    idEstado: inicial?.idEstado ?? 1,
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
      style={{
        border: '2px solid var(--primary)',
        marginBottom: 24,
        background: 'var(--primary-bg)',
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
        <h3 style={{ fontWeight: 700, fontSize: 15, color: 'var(--primary)' }}>
          {inicial ? '✏️ Editar producto' : '+ Nuevo producto'}
        </h3>
        <button
          type="button"
          onClick={onCancel}
          style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)' }}
        >
          <X size={18} />
        </button>
      </div>

      <form onSubmit={handleSubmit}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
          <div>
            <label className="form-label">Nombre del producto *</label>
            <input
              className="form-input"
              name="nombre"
              value={form.nombre}
              onChange={handleChange}
              required
              placeholder="Ej: Mouse inalámbrico"
              style={{ background: 'white' }}
            />
          </div>

          <div>
            <label className="form-label">Precio (COP) *</label>
            <input
              className="form-input"
              name="precio"
              type="number"
              min={0}
              value={form.precio}
              onChange={handleChange}
              required
              placeholder="0"
              style={{ background: 'white' }}
            />
          </div>

          <div>
            <label className="form-label">Stock disponible *</label>
            <input
              className="form-input"
              name="stockDisponible"
              type="number"
              min={0}
              value={form.stockDisponible}
              onChange={handleChange}
              required
              style={{ background: 'white' }}
            />
          </div>

          <div style={{ gridColumn: '1 / -1' }}>
            <label className="form-label">Descripción</label>
            <textarea
              className="form-input"
              name="descripcion"
              value={form.descripcion}
              onChange={handleChange}
              rows={3}
              placeholder="Describe el producto..."
              style={{ resize: 'vertical', background: 'white' }}
            />
          </div>
        </div>

        <div style={{ display: 'flex', gap: 10, marginTop: 20 }}>
          <button
            type="submit"
            className="btn btn-primary"
            disabled={loading}
            style={{ opacity: loading ? 0.7 : 1 }}
          >
            {loading ? 'Guardando...' : '✓ Guardar'}
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={onCancel}
          >
            Cancelar
          </button>
        </div>
      </form>
    </div>
  );
};

export default ProductoForm;
