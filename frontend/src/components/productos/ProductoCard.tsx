import { useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';
import { Package, ShoppingCart, CheckCircle2, Image, Building2 } from 'lucide-react';
import { useState } from 'react';
import type { AppDispatch } from '../../store';
import { addItem } from '../../store/cartSlice';
import { useAuth } from '../../hooks/useAuth';
import type { ProductoResponse } from '../../types/producto.types';
import type { EmprendimientoResponse } from '../../types/emprendimiento.types';
import ProductoGaleriaModal from './ProductoGaleriaModal';

interface Props {
  producto: ProductoResponse;
  emprendimiento?: EmprendimientoResponse;
  onPedir?: (producto: ProductoResponse) => void;
}

const ProductoCard: React.FC<Props> = ({ producto, emprendimiento, onPedir }) => {
  const dispatch = useDispatch<AppDispatch>();
  const { isAuthenticated } = useAuth();
  const [added, setAdded] = useState(false);
  const [galeriaAbierta, setGaleriaAbierta] = useState(false);
  const sinStock = producto.stockDisponible === 0;
  const inactivo = producto.nombreEstado === 'INACTIVO';

  const handleAgregarAlCarrito = () => {
    if (sinStock || inactivo) return;
    if (onPedir) {
      onPedir(producto);
      return;
    }
    dispatch(addItem(producto));
    setAdded(true);
    setTimeout(() => setAdded(false), 1800);
  };

  const showCartButton = isAuthenticated && !sinStock && !inactivo;

  return (
    <>
      <div className="product-card" style={{ display: 'flex', flexDirection: 'column' }}>
        {/* Image — clickable to open gallery */}
        <div
          style={{ position: 'relative', cursor: 'pointer' }}
          onClick={() => setGaleriaAbierta(true)}
        >
          {producto.primeraImagenUrl ? (
            <img
              src={producto.primeraImagenUrl}
              alt={producto.nombre}
              className="product-card-img"
              style={{ display: 'block' }}
            />
          ) : (
            <div
              className="product-card-img-placeholder"
              style={{ background: 'linear-gradient(135deg, var(--primary-bg), #EDE9FE)' }}
            >
              <Package size={36} color="var(--primary)" style={{ opacity: 0.4 }} />
            </div>
          )}

          {/* "Ver fotos" badge */}
          <div
            style={{
              position: 'absolute', bottom: 8, right: 8,
              background: 'rgba(0,0,0,0.55)', color: 'white',
              fontSize: 10, fontWeight: 600, padding: '3px 8px',
              borderRadius: 50, display: 'flex', alignItems: 'center', gap: 4,
              backdropFilter: 'blur(2px)',
            }}
          >
            <Image size={10} />
            Ver fotos
          </div>

          {/* Categoría badge */}
          {emprendimiento?.nombreCategoria && (
            <div
              style={{
                position: 'absolute', top: 8, left: 8,
                background: 'var(--primary)', color: 'white',
                fontSize: 9, fontWeight: 700, padding: '2px 8px',
                borderRadius: 50, textTransform: 'uppercase', letterSpacing: '0.5px',
              }}
            >
              {emprendimiento.nombreCategoria}
            </div>
          )}
        </div>

        {/* Body */}
        <div className="product-card-body" style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
          <h3 style={{ fontWeight: 700, fontSize: 14, marginBottom: 4, color: 'var(--text)' }}>
            {producto.nombre}
          </h3>
          <p
            style={{
              fontSize: 12.5, color: 'var(--text-muted)', flex: 1,
              overflow: 'hidden', display: '-webkit-box',
              WebkitLineClamp: 2, WebkitBoxOrient: 'vertical' as const,
              marginBottom: 10,
            }}
          >
            {producto.descripcion}
          </p>

          {/* Precio */}
          <div style={{ fontSize: 18, fontWeight: 800, color: 'var(--primary)', marginBottom: 8 }}>
            ${producto.precio.toLocaleString('es-CO')}
          </div>

          {/* Stock badge */}
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 10 }}>
            <span className={`badge ${sinStock ? 'badge-danger' : 'badge-success'}`}>
              {sinStock ? 'Sin stock' : `Stock: ${producto.stockDisponible}`}
            </span>
          </div>

          {/* Emprendimiento info */}
          <Link
            to={`/emprendimientos/${producto.idEmprendimiento}`}
            onClick={(e) => e.stopPropagation()}
            style={{
              display: 'flex', alignItems: 'center', gap: 8,
              textDecoration: 'none',
              padding: '8px 10px',
              borderRadius: 'var(--radius-sm)',
              background: '#F9FAFB',
              border: '1px solid var(--border)',
              marginBottom: 12,
              transition: 'background 0.15s',
            }}
            onMouseEnter={(e) => { e.currentTarget.style.background = 'var(--primary-bg)'; }}
            onMouseLeave={(e) => { e.currentTarget.style.background = '#F9FAFB'; }}
          >
            {/* Logo pequeño */}
            <div
              style={{
                width: 28, height: 28, borderRadius: 'var(--radius-sm)',
                overflow: 'hidden', flexShrink: 0,
                background: 'var(--primary-bg)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}
            >
              {emprendimiento?.logoUrl ? (
                <img
                  src={emprendimiento.logoUrl}
                  alt={emprendimiento.nombre}
                  style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                />
              ) : (
                <Building2 size={14} color="var(--primary)" style={{ opacity: 0.6 }} />
              )}
            </div>
            <div style={{ minWidth: 0 }}>
              <div
                style={{
                  fontSize: 12, fontWeight: 700, color: 'var(--primary)',
                  overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
                }}
              >
                {producto.nombreEmprendimiento}
              </div>
              {emprendimiento?.nombreUsuario && (
                <div style={{ fontSize: 10.5, color: 'var(--text-muted)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                  {emprendimiento.nombreUsuario}
                </div>
              )}
            </div>
          </Link>

          {/* Botón carrito */}
          {showCartButton && (
            <button
              onClick={handleAgregarAlCarrito}
              className={`btn btn-sm ${added ? 'btn-success' : 'btn-primary'}`}
              style={{ width: '100%', justifyContent: 'center' }}
            >
              {added ? (
                <><CheckCircle2 size={13} /> Agregado</>
              ) : (
                <><ShoppingCart size={13} /> Agregar al carrito</>
              )}
            </button>
          )}
        </div>
      </div>

      {galeriaAbierta && (
        <ProductoGaleriaModal
          idProducto={producto.idProducto}
          nombreProducto={producto.nombre}
          primeraImagenUrl={producto.primeraImagenUrl}
          onClose={() => setGaleriaAbierta(false)}
        />
      )}
    </>
  );
};

export default ProductoCard;
