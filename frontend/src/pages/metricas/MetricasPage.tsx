import { useEffect, useState, useMemo } from 'react';
import * as pedidoApi from '../../api/pedidoApi';
import * as reporteApi from '../../api/reporteApi';
import type { PedidoResponse } from '../../types/pedido.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import { useAuth } from '../../hooks/useAuth';
import { Package, TrendingUp, DollarSign, ShoppingBag, FileDown, X } from 'lucide-react';

interface ProductoMetrica {
  nombre: string;
  cantidad: number;
  ingresos: number;
}

const ESTADO_COLORS: Record<string, string> = {
  PENDIENTE:  '#F59E0B',
  CONFIRMADO: '#3B82F6',
  PREPARANDO: '#8B5CF6',
  ENTREGADO:  '#22C55E',
  CANCELADO:  '#EF4444',
};

const BarChart: React.FC<{ data: ProductoMetrica[]; max: number }> = ({ data, max }) => (
  <div style={{ padding: '8px 0' }}>
    {data.map((item, i) => (
      <div key={item.nombre} style={{ marginBottom: 14 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12.5, marginBottom: 5 }}>
          <span style={{ fontWeight: 600, color: 'var(--text)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', maxWidth: '60%' }}>
            {i + 1}. {item.nombre}
          </span>
          <span style={{ fontWeight: 700, color: 'var(--primary)' }}>
            {item.cantidad} uds · ${item.ingresos.toLocaleString('es-CO')}
          </span>
        </div>
        <div style={{ height: 10, background: '#F3F4F6', borderRadius: 99, overflow: 'hidden' }}>
          <div style={{
            height: '100%',
            width: `${max > 0 ? (item.cantidad / max) * 100 : 0}%`,
            background: 'linear-gradient(90deg, var(--primary), #818CF8)',
            borderRadius: 99,
            transition: 'width 0.6s ease',
          }} />
        </div>
      </div>
    ))}
  </div>
);

const DonutChart: React.FC<{ counts: Record<string, number>; total: number }> = ({ counts, total }) => {
  const entries = Object.entries(counts).filter(([, v]) => v > 0);
  const size = 140; const r = 50; const cx = size / 2; const cy = size / 2;
  const circumference = 2 * Math.PI * r;
  let offset = 0;
  const segments = entries.map(([estado, count]) => {
    const dashLen = total > 0 ? (count / total) * circumference : 0;
    const seg = { estado, count, dashLen, offset };
    offset += dashLen;
    return seg;
  });
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
        <circle cx={cx} cy={cy} r={r} fill="none" stroke="#F3F4F6" strokeWidth={22} />
        {segments.map((seg) => (
          <circle key={seg.estado} cx={cx} cy={cy} r={r} fill="none"
            stroke={ESTADO_COLORS[seg.estado] ?? '#9CA3AF'} strokeWidth={22}
            strokeDasharray={`${seg.dashLen} ${circumference - seg.dashLen}`}
            strokeDashoffset={-(seg.offset - circumference / 4)} strokeLinecap="butt" />
        ))}
        <text x={cx} y={cy - 6} textAnchor="middle" fontSize={22} fontWeight={800} fill="var(--text)">{total}</text>
        <text x={cx} y={cy + 12} textAnchor="middle" fontSize={9} fill="#9CA3AF" fontWeight={600}>PEDIDOS</text>
      </svg>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
        {entries.map(([estado, count]) => (
          <div key={estado} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <div style={{ width: 10, height: 10, borderRadius: 3, background: ESTADO_COLORS[estado] ?? '#9CA3AF', flexShrink: 0 }} />
            <span style={{ fontSize: 12.5, color: 'var(--text)', fontWeight: 500 }}>{estado}</span>
            <span style={{ marginLeft: 'auto', fontWeight: 700, fontSize: 13, color: ESTADO_COLORS[estado] ?? 'var(--text)' }}>{count}</span>
          </div>
        ))}
      </div>
    </div>
  );
};

// ── Weekly revenue line chart ───────────────────────────────────
const LineChart: React.FC<{ semanas: { label: string; ingresos: number }[] }> = ({ semanas }) => {
  const max = Math.max(...semanas.map((s) => s.ingresos), 1);
  const W = 480; const H = 100; const PAD = 10;
  const xs = semanas.map((_, i) => PAD + (i / (semanas.length - 1 || 1)) * (W - PAD * 2));
  const ys = semanas.map((s) => H - PAD - ((s.ingresos / max) * (H - PAD * 2)));
  const path = xs.map((x, i) => `${i === 0 ? 'M' : 'L'}${x},${ys[i]}`).join(' ');
  const area = `${path} L${xs[xs.length - 1]},${H - PAD} L${xs[0]},${H - PAD} Z`;
  return (
    <div style={{ overflowX: 'auto' }}>
      <svg viewBox={`0 0 ${W} ${H}`} style={{ width: '100%', minWidth: 300, height: 110 }}>
        <defs>
          <linearGradient id="areaGrad" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="var(--primary)" stopOpacity="0.25" />
            <stop offset="100%" stopColor="var(--primary)" stopOpacity="0.03" />
          </linearGradient>
        </defs>
        <path d={area} fill="url(#areaGrad)" />
        <path d={path} fill="none" stroke="var(--primary)" strokeWidth={2} strokeLinecap="round" strokeLinejoin="round" />
        {xs.map((x, i) => (
          <g key={i}>
            <circle cx={x} cy={ys[i]} r={3.5} fill="var(--primary)" />
            <text x={x} y={H} textAnchor="middle" fontSize={8} fill="#9CA3AF">{semanas[i].label}</text>
          </g>
        ))}
      </svg>
    </div>
  );
};

const MetricasPage: React.FC = () => {
  const { idUsuario } = useAuth();
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading]           = useState(true);
  const [error, setError]               = useState<string | null>(null);
  const [exportandoPdf, setExportandoPdf] = useState(false);
  const [exportandoCsv, setExportandoCsv] = useState(false);

  // Date filters
  const [desde, setDesde] = useState('');
  const [hasta, setHasta] = useState('');

  useEffect(() => {
    void (async () => {
      setLoading(true);
      try {
        const data = await pedidoApi.getPedidosRecibidos();
        setPedidos(data);
      } catch {
        setError('Error al cargar las métricas');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  // Filter pedidos client-side based on date range
  const pedidosFiltrados = useMemo(() => {
    if (!desde && !hasta) return pedidos;
    return pedidos.filter((p) => {
      if (!p.fechaPedido) return true;
      const fecha = new Date(p.fechaPedido);
      if (desde && fecha < new Date(desde)) return false;
      if (hasta) {
        const hastaFin = new Date(hasta);
        hastaFin.setHours(23, 59, 59, 999);
        if (fecha > hastaFin) return false;
      }
      return true;
    });
  }, [pedidos, desde, hasta]);

  const hayFiltro = desde || hasta;

  const limpiarFiltro = () => { setDesde(''); setHasta(''); };

  const filtro = hayFiltro ? { desde: desde || undefined, hasta: hasta || undefined } : undefined;

  const handleExportarPDF = async () => {
    if (!idUsuario) return;
    setExportandoPdf(true);
    try {
      await reporteApi.descargarReporteMetricas(idUsuario, filtro);
    } catch {
      setError('Error al generar el reporte PDF');
    } finally {
      setExportandoPdf(false);
    }
  };

  const handleExportarCSV = async () => {
    if (!idUsuario) return;
    setExportandoCsv(true);
    try {
      await reporteApi.descargarReporteCsv(idUsuario, filtro);
    } catch {
      setError('Error al generar el reporte CSV');
    } finally {
      setExportandoCsv(false);
    }
  };

  // Derived metrics from filtered data
  const totalPedidos       = pedidosFiltrados.length;
  const ingresosAcumulados = pedidosFiltrados.reduce((acc, p) => acc + (p.total ?? 0), 0);
  const totalUnidades      = pedidosFiltrados.reduce((acc, p) => acc + (p.detalles?.length ?? 0), 0);
  const ticketPromedio     = totalPedidos > 0 ? ingresosAcumulados / totalPedidos : 0;

  const productoMap: Record<string, ProductoMetrica> = {};
  pedidosFiltrados.forEach((p) => {
    p.detalles?.forEach((d) => {
      if (!productoMap[d.nombreProducto]) {
        productoMap[d.nombreProducto] = { nombre: d.nombreProducto, cantidad: 0, ingresos: 0 };
      }
      productoMap[d.nombreProducto].cantidad += d.cantidad;
      productoMap[d.nombreProducto].ingresos += d.subtotal ?? 0;
    });
  });
  const topProductos = Object.values(productoMap).sort((a, b) => b.cantidad - a.cantidad).slice(0, 6);
  const maxCantidad  = topProductos[0]?.cantidad ?? 1;

  const estadoCounts: Record<string, number> = {};
  pedidosFiltrados.forEach((p) => {
    estadoCounts[p.nombreEstado] = (estadoCounts[p.nombreEstado] ?? 0) + 1;
  });

  const [vistaGrafica, setVistaGrafica] = useState<'semanas' | 'meses'>('semanas');

  const puntosGrafica = useMemo(() => {
    const now = new Date();
    if (vistaGrafica === 'semanas') {
      return Array.from({ length: 8 }, (_, idx) => {
        const w = 7 - idx;
        const start = new Date(now); start.setDate(now.getDate() - w * 7 - 6); start.setHours(0, 0, 0, 0);
        const end   = new Date(now); end.setDate(now.getDate() - w * 7);       end.setHours(23, 59, 59, 999);
        const ingresos = pedidosFiltrados
          .filter((p) => { const f = new Date(p.fechaPedido); return f >= start && f <= end; })
          .reduce((acc, p) => acc + (p.total ?? 0), 0);
        return { label: `${start.getDate()}/${start.getMonth() + 1}`, ingresos };
      });
    }
    return Array.from({ length: 6 }, (_, idx) => {
      const m = 5 - idx;
      const start = new Date(now.getFullYear(), now.getMonth() - m, 1);
      const end   = new Date(now.getFullYear(), now.getMonth() - m + 1, 0, 23, 59, 59);
      const ingresos = pedidosFiltrados
        .filter((p) => { const f = new Date(p.fechaPedido); return f >= start && f <= end; })
        .reduce((acc, p) => acc + (p.total ?? 0), 0);
      const mes = start.toLocaleDateString('es-CO', { month: 'short' });
      return { label: `${mes} ${start.getFullYear()}`, ingresos };
    });
  }, [pedidosFiltrados, vistaGrafica]);

  const inputStyle: React.CSSProperties = {
    padding: '7px 10px',
    border: '1.5px solid var(--border)',
    borderRadius: 'var(--radius-md)',
    fontSize: 13,
    color: 'var(--text)',
    background: 'white',
    outline: 'none',
  };

  return (
    <DashboardLayout
      title="Métricas"
      subtitle="Panel de rendimiento de tus emprendimientos"
      action={
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, flexWrap: 'wrap' }}>
          <button
            onClick={handleExportarPDF}
            disabled={exportandoPdf || loading}
            className="btn btn-primary"
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <FileDown size={15} />
            {exportandoPdf ? 'Generando...' : 'PDF'}
          </button>
          <button
            onClick={handleExportarCSV}
            disabled={exportandoCsv || loading}
            className="btn btn-outline"
            style={{ display: 'flex', alignItems: 'center', gap: 6 }}
          >
            <FileDown size={15} />
            {exportandoCsv ? 'Generando...' : 'CSV'}
          </button>
        </div>
      }
    >
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {!loading && !error && (
        <>
          {/* ── Filtro de fechas ───────────────────────────────── */}
          <div className="card" style={{ marginBottom: 24, padding: '16px 20px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 16, flexWrap: 'wrap' }}>
              <span style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-muted)', whiteSpace: 'nowrap' }}>
                Filtrar por fecha:
              </span>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <label style={{ fontSize: 12, color: 'var(--text-muted)' }}>Desde</label>
                <input
                  type="date"
                  value={desde}
                  onChange={(e) => setDesde(e.target.value)}
                  max={hasta || undefined}
                  style={inputStyle}
                />
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <label style={{ fontSize: 12, color: 'var(--text-muted)' }}>Hasta</label>
                <input
                  type="date"
                  value={hasta}
                  onChange={(e) => setHasta(e.target.value)}
                  min={desde || undefined}
                  style={inputStyle}
                />
              </div>
              {hayFiltro && (
                <button
                  onClick={limpiarFiltro}
                  style={{
                    display: 'flex', alignItems: 'center', gap: 4,
                    padding: '6px 12px',
                    border: '1px solid #FECACA',
                    borderRadius: 'var(--radius-md)',
                    background: 'white',
                    color: 'var(--danger)',
                    fontSize: 12, fontWeight: 600,
                    cursor: 'pointer',
                  }}
                >
                  <X size={13} /> Quitar filtro
                </button>
              )}
              {hayFiltro && (
                <span style={{
                  padding: '4px 10px',
                  background: 'var(--primary-bg)',
                  color: 'var(--primary)',
                  borderRadius: 999,
                  fontSize: 12, fontWeight: 600,
                }}>
                  {totalPedidos} pedido{totalPedidos !== 1 ? 's' : ''} en el rango
                </span>
              )}
            </div>
          </div>

          {/* ── KPI Cards ──────────────────────────────────────── */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(190px, 1fr))', gap: 18, marginBottom: 28 }}>
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

          {/* ── Charts ─────────────────────────────────────────── */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 360px', gap: 20, marginBottom: 20 }}>
            <div className="card" style={{ marginBottom: 0 }}>
              <h2 style={{ fontWeight: 700, fontSize: 15, marginBottom: 20 }}>Productos más vendidos</h2>
              {topProductos.length === 0
                ? <div style={{ textAlign: 'center', padding: '32px 0', color: 'var(--text-muted)', fontSize: 13 }}>Aún no hay ventas registradas</div>
                : <BarChart data={topProductos} max={maxCantidad} />}
            </div>
            <div className="card" style={{ marginBottom: 0 }}>
              <h2 style={{ fontWeight: 700, fontSize: 15, marginBottom: 20 }}>Estado de pedidos</h2>
              {totalPedidos === 0
                ? <div style={{ textAlign: 'center', padding: '32px 0', color: 'var(--text-muted)', fontSize: 13 }}>Sin datos todavía</div>
                : <DonutChart counts={estadoCounts} total={totalPedidos} />}
            </div>
          </div>

          {/* ── Gráfica de actividad ───────────────────────────── */}
          <div className="card" style={{ marginBottom: 20 }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 16 }}>
              <h2 style={{ fontWeight: 700, fontSize: 15 }}>
                Ingresos por {vistaGrafica === 'semanas' ? 'semana' : 'mes'}
              </h2>
              <div style={{ display: 'flex', border: '1px solid var(--border)', borderRadius: 'var(--radius-md)', overflow: 'hidden' }}>
                {(['semanas', 'meses'] as const).map((v) => (
                  <button key={v} onClick={() => setVistaGrafica(v)} style={{
                    padding: '5px 14px', fontSize: 12, fontWeight: 600, border: 'none', cursor: 'pointer',
                    background: vistaGrafica === v ? 'var(--primary)' : 'white',
                    color: vistaGrafica === v ? 'white' : 'var(--text-muted)',
                  }}>
                    {v === 'semanas' ? 'Semanal' : 'Mensual'}
                  </button>
                ))}
              </div>
            </div>
            {puntosGrafica.every((s) => s.ingresos === 0)
              ? <div style={{ textAlign: 'center', padding: '24px 0', color: 'var(--text-muted)', fontSize: 13 }}>Sin datos en el período</div>
              : <LineChart semanas={puntosGrafica} />}
          </div>

          {/* ── Tabla de productos ─────────────────────────────── */}
          <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
            <div style={{ padding: '18px 24px', borderBottom: '1px solid var(--border)' }}>
              <h2 style={{ fontWeight: 700, fontSize: 15 }}>Detalle de ventas por producto</h2>
            </div>
            {topProductos.length === 0
              ? <p style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-muted)', fontSize: 13 }}>Sin ventas aún</p>
              : (
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
                              <div style={{
                                height: '100%',
                                width: `${ingresosAcumulados > 0 ? (p.ingresos / ingresosAcumulados) * 100 : 0}%`,
                                background: 'var(--primary)', borderRadius: 99,
                              }} />
                            </div>
                            <span style={{ fontSize: 12, color: 'var(--text-muted)', minWidth: 36 }}>
                              {ingresosAcumulados > 0 ? `${Math.round((p.ingresos / ingresosAcumulados) * 100)}%` : '0%'}
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
