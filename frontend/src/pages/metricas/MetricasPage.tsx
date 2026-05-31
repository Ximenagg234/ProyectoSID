import { useEffect, useState } from 'react';
import * as pedidoApi from '../../api/pedidoApi';
import * as reporteApi from '../../api/reporteApi';
import type { PedidoResponse } from '../../types/pedido.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import { useAuth } from '../../hooks/useAuth';
import { Package, TrendingUp, DollarSign, ShoppingBag, FileDown } from 'lucide-react';

interface ProductoMetrica {
  nombre: string;
  cantidad: number;
  ingresos: number;
}

const ESTADO_COLORS: Record<string, string> = {
  PENDIENTE: '#F59E0B',
  CONFIRMADO: '#3B82F6',
  PREPARANDO: '#8B5CF6',
  ENTREGADO: '#22C55E',
  CANCELADO: '#EF4444',
};

// Inline bar chart using pure CSS
const BarChart: React.FC<{ data: ProductoMetrica[]; max: number }> = ({ data, max }) => (
  <div style={{ padding: '8px 0' }}>
    {data.map((item, i) => (
      <div key={item.nombre} style={{ marginBottom: 14 }}>
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            fontSize: 12.5,
            marginBottom: 5,
          }}
        >
          <span style={{ fontWeight: 600, color: 'var(--text)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: '60%' }}>
            {i + 1}. {item.nombre}
          </span>
          <span style={{ fontWeight: 700, color: 'var(--primary)' }}>
            {item.cantidad} uds · ${item.ingresos.toLocaleString('es-CO')}
          </span>
        </div>
        <div
          style={{
            height: 10,
            background: '#F3F4F6',
            borderRadius: 99,
            overflow: 'hidden',
          }}
        >
          <div
            style={{
              height: '100%',
              width: `${max > 0 ? (item.cantidad / max) * 100 : 0}%`,
              background: `linear-gradient(90deg, var(--primary), #818CF8)`,
              borderRadius: 99,
              transition: 'width 0.6s ease',
            }}
          />
        </div>
      </div>
    ))}
  </div>
);

// Donut chart for order statuses using SVG
const DonutChart: React.FC<{ counts: Record<string, number>; total: number }> = ({
  counts,
  total,
}) => {
  const entries = Object.entries(counts).filter(([, v]) => v > 0);
  const size = 140;
  const r = 50;
  const cx = size / 2;
  const cy = size / 2;
  const circumference = 2 * Math.PI * r;

  let offset = 0;
  const segments = entries.map(([estado, count]) => {
    const pct = total > 0 ? count / total : 0;
    const dashLen = pct * circumference;
    const seg = { estado, count, dashLen, offset };
    offset += dashLen;
    return seg;
  });

  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
        {/* Background circle */}
        <circle cx={cx} cy={cy} r={r} fill="none" stroke="#F3F4F6" strokeWidth={22} />
        {segments.map((seg) => (
          <circle
            key={seg.estado}
            cx={cx}
            cy={cy}
            r={r}
            fill="none"
            stroke={ESTADO_COLORS[seg.estado] ?? '#9CA3AF'}
            strokeWidth={22}
            strokeDasharray={`${seg.dashLen} ${circumference - seg.dashLen}`}
            strokeDashoffset={-(seg.offset - circumference / 4)}
            strokeLinecap="butt"
          />
        ))}
        {/* Center text */}
        <text x={cx} y={cy - 6} textAnchor="middle" fontSize={22} fontWeight={800} fill="var(--text)">
          {total}
        </text>
        <text x={cx} y={cy + 12} textAnchor="middle" fontSize={9} fill="#9CA3AF" fontWeight={600}>
          PEDIDOS
        </text>
      </svg>

      {/* Legend */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
        {entries.map(([estado, count]) => (
          <div key={estado} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <div
              style={{
                width: 10,
                height: 10,
                borderRadius: 3,
                background: ESTADO_COLORS[estado] ?? '#9CA3AF',
                flexShrink: 0,
              }}
            />
            <span style={{ fontSize: 12.5, color: 'var(--text)', fontWeight: 500 }}>
              {estado}
            </span>
            <span
              style={{
                marginLeft: 'auto',
                fontWeight: 700,
                fontSize: 13,
                color: ESTADO_COLORS[estado] ?? 'var(--text)',
              }}
            >
              {count}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
};

const MetricasPage: React.FC = () => {
  const { idUsuario } = useAuth();
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [exportando, setExportando] = useState(false);

  useEffect(() => {
    pedidoApi
      .getPedidosRecibidos()
      .then(setPedidos)
      .catch(() => setError('Error al cargar las métricas'))
      .finally(() => setLoading(false));
  }, []);

  const handleExportarPDF = async () => {
    if (!idUsuario) return;
    setExportando(true);
    try {
      await reporteApi.descargarReporteMetricas(idUsuario);
    } catch {
      setError('Error al generar el reporte PDF');
    } finally {
      setExportando(false);
    }
  };

  const totalPedidos = pedidos.length;
  const ingresosAcumulados = pedidos.reduce((acc, p) => acc + (p.total ?? 0), 0);
  const totalUnidades = pedidos.reduce((acc, p) => acc + (p.detalles?.length ?? 0), 0);
  const ticketPromedio = totalPedidos > 0 ? ingresosAcumulados / totalPedidos : 0;

  // Products metrics
  const productoMap: Record<string, ProductoMetrica> = {};
  pedidos.forEach((p) => {
    p.detalles?.forEach((d) => {
      if (!productoMap[d.nombreProducto]) {
        productoMap[d.nombreProducto] = { nombre: d.nombreProducto, cantidad: 0, ingresos: 0 };
      }
      productoMap[d.nombreProducto].cantidad += d.cantidad;
      productoMap[d.nombreProducto].ingresos += d.subtotal ?? 0;
    });
  });
  const topProductos = Object.values(productoMap)
    .sort((a, b) => b.cantidad - a.cantidad)
    .slice(0, 6);
  const maxCantidad = topProductos[0]?.cantidad ?? 1;

  // Estado counts
  const estadoCounts: Record<string, number> = {};
  pedidos.forEach((p) => {
    estadoCounts[p.nombreEstado] = (estadoCounts[p.nombreEstado] ?? 0) + 1;
  });

  return (
    <DashboardLayout
      title="Métricas"
      subtitle="Panel de rendimiento de tus emprendimientos"
      action={
        <button
          onClick={handleExportarPDF}
          disabled={exportando || loading}
          className="btn btn-primary"
          style={{ display: 'flex', alignItems: 'center', gap: 8 }}
        >
          <FileDown size={16} />
          {exportando ? 'Generando PDF...' : 'Exportar PDF'}
        </button>
      }
    >
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {!loading && !error && (
        <>
          {/* KPI Cards */}
          <div
            style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(190px, 1fr))',
              gap: 18,
              marginBottom: 28,
            }}
          >
            <div className="metric-card">
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
                <div className="metric-title">Total Pedidos</div>
                <div className="metric-icon" style={{ background: 'var(--primary-bg)' }}>
                  <Package size={22} color="var(--primary)" />
                </div>
              </div>
              <div className="metric-value">{totalPedidos}</div>
            </div>

            <div className="metric-card">
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
                <div className="metric-title">Ingresos Totales</div>
                <div className="metric-icon" style={{ background: 'var(--secondary-bg)' }}>
                  <DollarSign size={22} color="var(--secondary)" />
                </div>
              </div>
              <div className="metric-value" style={{ fontSize: 22 }}>
                ${ingresosAcumulados.toLocaleString('es-CO')}
              </div>
            </div>

            <div className="metric-card">
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
                <div className="metric-title">Productos Vendidos</div>
                <div className="metric-icon" style={{ background: '#EDE9FE' }}>
                  <ShoppingBag size={22} color="#7C3AED" />
                </div>
              </div>
              <div className="metric-value">{totalUnidades}</div>
            </div>

            <div className="metric-card">
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
                <div className="metric-title">Ticket Promedio</div>
                <div className="metric-icon" style={{ background: '#FEF3C7' }}>
                  <TrendingUp size={22} color="#D97706" />
                </div>
              </div>
              <div className="metric-value" style={{ fontSize: 20 }}>
                ${Math.round(ticketPromedio).toLocaleString('es-CO')}
              </div>
            </div>
          </div>

          {/* Charts row */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 360px', gap: 20, marginBottom: 20 }}>
            {/* Bar chart */}
            <div className="card" style={{ marginBottom: 0 }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
                <h2 style={{ fontWeight: 700, fontSize: 15 }}>🏆 Productos más vendidos</h2>
              </div>
              {topProductos.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '32px 0', color: 'var(--text-muted)', fontSize: 13 }}>
                  Aún no hay ventas registradas
                </div>
              ) : (
                <BarChart data={topProductos} max={maxCantidad} />
              )}
            </div>

            {/* Donut chart */}
            <div className="card" style={{ marginBottom: 0 }}>
              <h2 style={{ fontWeight: 700, fontSize: 15, marginBottom: 20 }}>📊 Estado de pedidos</h2>
              {totalPedidos === 0 ? (
                <div style={{ textAlign: 'center', padding: '32px 0', color: 'var(--text-muted)', fontSize: 13 }}>
                  Sin datos todavía
                </div>
              ) : (
                <DonutChart counts={estadoCounts} total={totalPedidos} />
              )}
            </div>
          </div>

          {/* Top productos table */}
          <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
            <div style={{ padding: '18px 24px', borderBottom: '1px solid var(--border)' }}>
              <h2 style={{ fontWeight: 700, fontSize: 15 }}>Detalle de ventas por producto</h2>
            </div>
            {topProductos.length === 0 ? (
              <p style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-muted)', fontSize: 13 }}>
                Sin ventas aún
              </p>
            ) : (
              <table className="table-custom">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>Producto</th>
                    <th>Unidades vendidas</th>
                    <th>Ingresos generados</th>
                    <th>Participación</th>
                  </tr>
                </thead>
                <tbody>
                  {topProductos.map((p, i) => (
                    <tr key={p.nombre}>
                      <td style={{ color: 'var(--text-muted)', fontWeight: 700 }}>{i + 1}</td>
                      <td style={{ fontWeight: 600 }}>{p.nombre}</td>
                      <td>{p.cantidad}</td>
                      <td style={{ fontWeight: 700, color: 'var(--secondary)' }}>
                        ${p.ingresos.toLocaleString('es-CO')}
                      </td>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                          <div style={{ flex: 1, height: 6, background: '#F3F4F6', borderRadius: 99, overflow: 'hidden' }}>
                            <div
                              style={{
                                height: '100%',
                                width: `${ingresosAcumulados > 0 ? (p.ingresos / ingresosAcumulados) * 100 : 0}%`,
                                background: 'var(--primary)',
                                borderRadius: 99,
                              }}
                            />
                          </div>
                          <span style={{ fontSize: 12, color: 'var(--text-muted)', minWidth: 36 }}>
                            {ingresosAcumulados > 0
                              ? `${Math.round((p.ingresos / ingresosAcumulados) * 100)}%`
                              : '0%'}
                          </span>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </>
      )}
    </DashboardLayout>
  );
};

export default MetricasPage;
