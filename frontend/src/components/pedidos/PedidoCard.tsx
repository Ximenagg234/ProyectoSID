import type { PedidoResponse } from '../../types/pedido.types';
import PedidoEstadoBadge from './PedidoEstadoBadge';
import { Package, User, Building2 } from 'lucide-react';

interface Props {
  pedido: PedidoResponse;
  onCambiarEstado?: (id: number, nuevoEstado: string) => void;
}

const ESTADOS = ['PENDIENTE', 'CONFIRMADO', 'PREPARANDO', 'ENTREGADO'];

const ESTADO_COLORS: Record<string, string> = {
  PENDIENTE: '#D97706',
  CONFIRMADO: '#1D4ED8',
  PREPARANDO: '#7C3AED',
  ENTREGADO: '#16A34A',
};

const ESTADO_ICONS: Record<string, string> = {
  PENDIENTE: '⏳',
  CONFIRMADO: '✅',
  PREPARANDO: '📦',
  ENTREGADO: '🚀',
};

const StatusTracker: React.FC<{ estadoActual: string }> = ({ estadoActual }) => {
  const currentIndex = ESTADOS.indexOf(estadoActual);
  const isCanceled = estadoActual === 'CANCELADO';

  if (isCanceled) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '10px 0', color: 'var(--danger)', fontWeight: 600, fontSize: 13 }}>
        ❌ Pedido cancelado
      </div>
    );
  }

  // Estado no reconocido en el flujo normal (ACTIVO, INACTIVO, etc.)
  if (currentIndex === -1) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '10px 0', color: 'var(--warning)', fontWeight: 600, fontSize: 13, background: 'var(--warning-bg)', borderRadius: 'var(--radius-sm)', paddingLeft: 12 }}>
        ⚠️ Estado incorrecto: {estadoActual} — usa el botón para restablecer
      </div>
    );
  }

  return (
    <div style={{ padding: '14px 0 4px', overflow: 'hidden' }}>
      <div style={{ display: 'flex', alignItems: 'center', position: 'relative' }}>
        {ESTADOS.map((estado, idx) => {
          const isCompleted = idx <= currentIndex;
          const isActive = idx === currentIndex;
          const color = isCompleted ? ESTADO_COLORS[estado] ?? 'var(--primary)' : '#D1D5DB';

          return (
            <div
              key={estado}
              style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', position: 'relative' }}
            >
              {/* Connector line */}
              {idx < ESTADOS.length - 1 && (
                <div
                  style={{
                    position: 'absolute',
                    left: '50%',
                    top: 13,
                    width: '100%',
                    height: 3,
                    background: idx < currentIndex ? ESTADO_COLORS[ESTADOS[idx + 1]] ?? 'var(--primary)' : '#E5E7EB',
                    zIndex: 0,
                    transition: 'background 0.3s',
                  }}
                />
              )}

              {/* Circle */}
              <div
                style={{
                  width: 28,
                  height: 28,
                  borderRadius: '50%',
                  background: isCompleted ? color : 'white',
                  border: `2.5px solid ${color}`,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: 12,
                  position: 'relative',
                  zIndex: 1,
                  transform: isActive ? 'scale(1.15)' : 'scale(1)',
                  transition: 'all 0.2s',
                  boxShadow: isActive ? `0 0 0 3px ${color}30` : 'none',
                }}
              >
                {isCompleted ? (
                  <span style={{ fontSize: 12 }}>{ESTADO_ICONS[estado]}</span>
                ) : (
                  <div style={{ width: 8, height: 8, borderRadius: '50%', background: '#D1D5DB' }} />
                )}
              </div>

              {/* Label */}
              <div
                style={{
                  marginTop: 6,
                  fontSize: 10,
                  fontWeight: isActive ? 700 : 500,
                  color: isCompleted ? color : '#9CA3AF',
                  textAlign: 'center',
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px',
                  lineHeight: 1.2,
                }}
              >
                {estado}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

const PedidoCard: React.FC<Props> = ({ pedido, onCambiarEstado }) => {
  const currentIdx = ESTADOS.indexOf(pedido.nombreEstado);
  const canAdvance =
    onCambiarEstado &&
    currentIdx >= 0 &&
    currentIdx < ESTADOS.length - 1 &&
    pedido.nombreEstado !== 'CANCELADO';

  return (
    <div
      style={{
        background: 'white',
        borderRadius: 'var(--radius-xl)',
        border: '1px solid var(--border)',
        boxShadow: 'var(--shadow-md)',
        overflow: 'hidden',
      }}
    >
      {/* Header */}
      <div
        style={{
          padding: '16px 20px',
          borderBottom: '1px solid var(--border)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          background: '#FAFAFA',
        }}
      >
        <div style={{ fontWeight: 800, fontSize: 14 }}>Pedido #{pedido.idPedido}</div>
        <PedidoEstadoBadge estado={pedido.nombreEstado} />
      </div>

      <div style={{ padding: '16px 20px' }}>
        {/* Info row */}
        <div style={{ display: 'flex', gap: 20, marginBottom: 16 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 13, color: 'var(--text-muted)' }}>
            <Building2 size={13} color="var(--primary)" />
            <span style={{ fontWeight: 600, color: 'var(--text)' }}>{pedido.nombreEmprendimiento}</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 13, color: 'var(--text-muted)' }}>
            <User size={13} />
            <span>{pedido.nombreUsuario}</span>
          </div>
        </div>

        {/* Status tracker */}
        <StatusTracker estadoActual={pedido.nombreEstado} />

        {/* Products list */}
        {pedido.detalles?.length > 0 && (
          <div
            style={{
              background: '#F9FAFB',
              borderRadius: 'var(--radius-md)',
              padding: '12px 14px',
              marginTop: 14,
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 8 }}>
              <Package size={13} color="var(--text-muted)" />
              <span style={{ fontSize: 11, fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.7px', color: 'var(--text-muted)' }}>
                Productos
              </span>
            </div>
            {pedido.detalles.map((d) => (
              <div
                key={d.idDetalle}
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  fontSize: 13,
                  paddingBottom: 6,
                  marginBottom: 6,
                  borderBottom: '1px solid var(--border)',
                }}
              >
                <span style={{ color: 'var(--text)' }}>
                  {d.nombreProducto}
                  <span style={{ color: 'var(--text-muted)', marginLeft: 4 }}>×{d.cantidad}</span>
                </span>
                <span style={{ fontWeight: 600 }}>${d.subtotal?.toLocaleString('es-CO')}</span>
              </div>
            ))}
          </div>
        )}

        {/* Total + action */}
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginTop: 16,
          }}
        >
          <div>
            <span style={{ fontSize: 11, color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>
              Total
            </span>
            <div style={{ fontSize: 20, fontWeight: 800, color: 'var(--primary)' }}>
              ${pedido.total?.toLocaleString('es-CO')}
            </div>
          </div>

            {canAdvance && (
            <button
              className="btn btn-primary btn-sm"
              onClick={() =>
                onCambiarEstado(pedido.idPedido, ESTADOS[currentIdx + 1])
              }
            >
              Marcar como {ESTADOS[currentIdx + 1]}
            </button>
          )}
          {/* Safety valve: estado no reconocido (ej. INACTIVO/ACTIVO por bug anterior) */}
          {onCambiarEstado && currentIdx === -1 && pedido.nombreEstado !== 'CANCELADO' && (
            <button
              className="btn btn-warning btn-sm"
              style={{ background: 'var(--warning-bg)', color: '#92400E', border: '1px solid #FCD34D' }}
              onClick={() => onCambiarEstado(pedido.idPedido, 'PENDIENTE')}
            >
              ↩ Restablecer a PENDIENTE
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default PedidoCard;
