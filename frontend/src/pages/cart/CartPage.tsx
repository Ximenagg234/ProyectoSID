import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { Trash2, Plus, Minus, ShoppingBag, ShoppingCart } from 'lucide-react';
import type { RootState, AppDispatch } from '../../store';
import { removeItem, updateCantidad, clearCart } from '../../store/cartSlice';
import * as pedidoApi from '../../api/pedidoApi';
import { useAuth } from '../../hooks/useAuth';
import DashboardLayout from '../../components/common/DashboardLayout';
import type { ProductoResponse } from '../../types/producto.types';

interface GrupoCarrito {
  idEmprendimiento: number;
  nombreEmprendimiento: string;
  items: { producto: ProductoResponse; cantidad: number }[];
}

const CartPage: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { idUsuario } = useAuth();
  const cartItems = useSelector((state: RootState) => state.cart.items);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const total = cartItems.reduce(
    (acc, item) => acc + item.producto.precio * item.cantidad,
    0
  );

  // Group items by emprendimiento using idEmprendimiento (reliable, no name-based lookup)
  const grupos: GrupoCarrito[] = cartItems.reduce<GrupoCarrito[]>((acc, item) => {
    const found = acc.find((g) => g.idEmprendimiento === item.producto.idEmprendimiento);
    if (found) {
      found.items.push(item);
    } else {
      acc.push({
        idEmprendimiento: item.producto.idEmprendimiento,
        nombreEmprendimiento: item.producto.nombreEmprendimiento,
        items: [item],
      });
    }
    return acc;
  }, []);

  const handleConfirmar = async () => {
    if (cartItems.length === 0) return;
    if (!idUsuario) {
      setError('No se pudo identificar el usuario. Inicia sesión de nuevo.');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      for (const grupo of grupos) {
        await pedidoApi.create({
          idEmprendimiento: grupo.idEmprendimiento,
          idUsuario,
          idEstado: 3, // PENDIENTE
          detalles: grupo.items.map((item) => ({
            idProducto: item.producto.idProducto,
            cantidad: item.cantidad,
            precioUnitario: item.producto.precio,
          })),
        });
      }

      dispatch(clearCart());
      navigate('/mis-pedidos');
    } catch {
      setError('Error al confirmar el pedido. Intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <DashboardLayout
      title="Mi Carrito"
      subtitle="Revisa y confirma tu pedido"
      action={
        cartItems.length > 0 ? (
          <button
            className="btn btn-secondary btn-sm"
            onClick={() => dispatch(clearCart())}
          >
            <Trash2 size={13} /> Vaciar carrito
          </button>
        ) : undefined
      }
    >
      {cartItems.length === 0 ? (
        <div
          style={{
            textAlign: 'center',
            padding: '80px 0',
            color: 'var(--text-muted)',
            background: 'white',
            borderRadius: 'var(--radius-xl)',
            border: '1px solid var(--border)',
          }}
        >
          <ShoppingCart
            size={52}
            style={{ opacity: 0.2, display: 'block', margin: '0 auto 20px' }}
          />
          <p style={{ fontWeight: 700, fontSize: 18, marginBottom: 8 }}>
            Tu carrito está vacío
          </p>
          <p style={{ fontSize: 14, marginBottom: 28 }}>
            Explora el marketplace y agrega productos
          </p>
          <button className="btn btn-primary" onClick={() => navigate('/marketplace')}>
            <ShoppingBag size={15} /> Ir al Marketplace
          </button>
        </div>
      ) : (
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: '1fr 340px',
            gap: 24,
            alignItems: 'start',
          }}
        >
          {/* Items list */}
          <div>
            {grupos.map((grupo) => (
              <div
                key={grupo.idEmprendimiento}
                className="card"
                style={{ padding: 0, overflow: 'hidden', marginBottom: 20 }}
              >
                <div
                  style={{
                    padding: '14px 20px',
                    borderBottom: '1px solid var(--border)',
                    background: '#FAFAFA',
                    fontWeight: 700,
                    fontSize: 13,
                    color: 'var(--primary)',
                  }}
                >
                  🏪 {grupo.nombreEmprendimiento}
                </div>
                {grupo.items.map(({ producto, cantidad }) => (
                  <div
                    key={producto.idProducto}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      gap: 16,
                      padding: '16px 20px',
                      borderBottom: '1px solid var(--border)',
                    }}
                  >
                    {/* Product image */}
                    <div
                      style={{
                        width: 64,
                        height: 64,
                        borderRadius: 'var(--radius-md)',
                        background: 'var(--primary-bg)',
                        overflow: 'hidden',
                        flexShrink: 0,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                      }}
                    >
                      {producto.primeraImagenUrl ? (
                        <img
                          src={producto.primeraImagenUrl}
                          alt={producto.nombre}
                          style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                        />
                      ) : (
                        <ShoppingBag
                          size={24}
                          color="var(--primary)"
                          style={{ opacity: 0.4 }}
                        />
                      )}
                    </div>

                    {/* Info */}
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <p style={{ fontWeight: 700, fontSize: 14, marginBottom: 2 }}>
                        {producto.nombre}
                      </p>
                      <p
                        style={{
                          fontSize: 12,
                          color: 'var(--text-muted)',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap',
                        }}
                      >
                        {producto.descripcion}
                      </p>
                    </div>

                    {/* Quantity controls */}
                    <div
                      style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: 8,
                        border: '1.5px solid var(--border)',
                        borderRadius: 50,
                        padding: '4px 8px',
                      }}
                    >
                      <button
                        onClick={() =>
                          cantidad === 1
                            ? dispatch(removeItem(producto.idProducto))
                            : dispatch(updateCantidad({ idProducto: producto.idProducto, cantidad: cantidad - 1 }))
                        }
                        style={{
                          width: 26, height: 26, borderRadius: '50%', border: 'none',
                          background: 'var(--border)', cursor: 'pointer',
                          display: 'flex', alignItems: 'center', justifyContent: 'center',
                        }}
                      >
                        <Minus size={12} />
                      </button>
                      <span style={{ fontWeight: 700, fontSize: 14, minWidth: 20, textAlign: 'center' }}>
                        {cantidad}
                      </span>
                      <button
                        onClick={() =>
                          dispatch(updateCantidad({ idProducto: producto.idProducto, cantidad: cantidad + 1 }))
                        }
                        style={{
                          width: 26, height: 26, borderRadius: '50%', border: 'none',
                          background: 'var(--primary)', cursor: 'pointer',
                          display: 'flex', alignItems: 'center', justifyContent: 'center',
                        }}
                      >
                        <Plus size={12} color="white" />
                      </button>
                    </div>

                    {/* Price */}
                    <div style={{ minWidth: 90, textAlign: 'right' }}>
                      <div style={{ fontWeight: 800, fontSize: 15, color: 'var(--primary)' }}>
                        ${(producto.precio * cantidad).toLocaleString('es-CO')}
                      </div>
                      <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>
                        ${producto.precio.toLocaleString('es-CO')} c/u
                      </div>
                    </div>

                    {/* Remove */}
                    <button
                      onClick={() => dispatch(removeItem(producto.idProducto))}
                      style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--danger)', padding: 4 }}
                    >
                      <Trash2 size={15} />
                    </button>
                  </div>
                ))}
              </div>
            ))}
          </div>

          {/* Order summary */}
          <div className="card" style={{ position: 'sticky', top: 24 }}>
            <h3 style={{ fontWeight: 700, fontSize: 16, marginBottom: 20 }}>
              Resumen del pedido
            </h3>

            {cartItems.map(({ producto, cantidad }) => (
              <div
                key={producto.idProducto}
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  fontSize: 13,
                  marginBottom: 8,
                  color: 'var(--text-muted)',
                }}
              >
                <span>{producto.nombre} ×{cantidad}</span>
                <span style={{ fontWeight: 600, color: 'var(--text)' }}>
                  ${(producto.precio * cantidad).toLocaleString('es-CO')}
                </span>
              </div>
            ))}

            <div
              style={{
                borderTop: '1.5px solid var(--border)',
                marginTop: 16,
                paddingTop: 16,
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
              }}
            >
              <span style={{ fontWeight: 600, fontSize: 14 }}>Total</span>
              <span style={{ fontWeight: 800, fontSize: 20, color: 'var(--primary)' }}>
                ${total.toLocaleString('es-CO')}
              </span>
            </div>

            {grupos.length > 1 && (
              <div
                style={{
                  background: 'var(--primary-bg)',
                  borderRadius: 'var(--radius-md)',
                  padding: '10px 14px',
                  marginTop: 12,
                  fontSize: 12.5,
                  color: 'var(--primary)',
                }}
              >
                📦 Se crearán {grupos.length} pedidos separados (uno por emprendimiento)
              </div>
            )}

            {error && (
              <div
                style={{
                  background: 'var(--danger-bg)',
                  color: 'var(--danger)',
                  padding: '10px 14px',
                  borderRadius: 'var(--radius-md)',
                  fontSize: 12.5,
                  marginTop: 16,
                }}
              >
                {error}
              </div>
            )}

            <button
              className="btn btn-primary"
              style={{
                width: '100%', justifyContent: 'center',
                padding: '13px', marginTop: 20,
                opacity: loading ? 0.7 : 1,
              }}
              onClick={handleConfirmar}
              disabled={loading}
            >
              {loading ? 'Procesando...' : '✓ Confirmar pedido'}
            </button>

            <button
              className="btn btn-secondary"
              style={{ width: '100%', justifyContent: 'center', padding: '11px', marginTop: 10 }}
              onClick={() => navigate('/marketplace')}
            >
              ← Seguir comprando
            </button>
          </div>
        </div>
      )}
    </DashboardLayout>
  );
};

export default CartPage;
