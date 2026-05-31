import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Briefcase, ShoppingBag, Package, TrendingUp, Store } from 'lucide-react';
import DashboardLayout from '../../components/common/DashboardLayout';
import { useAuth } from '../../hooks/useAuth';
import * as emprendimientoApi from '../../api/emprendimientoApi';
import * as pedidoApi from '../../api/pedidoApi';
import type { EmprendimientoResponse } from '../../types/emprendimiento.types';
import type { PedidoResponse } from '../../types/pedido.types';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const HomePage: React.FC = () => {
  const { correo, isAdmin, isEmprendedor, isComprador } = useAuth();
  const [emprendimientos, setEmprendimientos] = useState<EmprendimientoResponse[]>([]);
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const empsPromise = emprendimientoApi.getAll();
        let pedsPromise: Promise<PedidoResponse[]>;
        if (isComprador) {
          pedsPromise = pedidoApi.getMisPedidos();
        } else if (isEmprendedor) {
          pedsPromise = pedidoApi.getPedidosRecibidos();
        } else {
          pedsPromise = Promise.resolve([]);
        }
        const [emps, peds] = await Promise.all([empsPromise, pedsPromise]);
        setEmprendimientos(emps);
        setPedidos(peds);
      } catch {
        // ignore
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [isComprador, isEmprendedor]);

  const misEmps = emprendimientos.filter(e =>
    correo && (e.nombreUsuario === correo || e.nombreUsuario?.toLowerCase().includes(correo.split('@')[0].toLowerCase()))
  );
  const destacados = emprendimientos.filter(e => e.destacado).slice(0, 4);

  if (loading) return <DashboardLayout><LoadingSpinner /></DashboardLayout>;

  const hour = new Date().getHours();
  const greeting = hour < 12 ? '¡Buenos días' : hour < 18 ? '¡Buenas tardes' : '¡Buenas noches';

  return (
    <DashboardLayout>
      {/* Greeting */}
      <div style={{ background: 'linear-gradient(135deg, var(--primary), #4F46E5)', borderRadius: 'var(--radius-xl)', padding: '28px 32px', marginBottom: 28, color: 'white' }}>
        <div style={{ fontSize: 22, fontWeight: 800 }}>{greeting}, {correo?.split('@')[0]}! 👋</div>
        <div style={{ fontSize: 14, opacity: 0.85, marginTop: 4 }}>Bienvenido a Emprende ICESI — la plataforma de emprendimientos universitarios.</div>
        <div style={{ display: 'flex', gap: 12, marginTop: 20, flexWrap: 'wrap' }}>
          <Link to="/marketplace" className="btn" style={{ background: 'white', color: 'var(--primary)' }}><ShoppingBag size={15} /> Ir al Marketplace</Link>
          {isEmprendedor && <Link to="/mis-emprendimientos" className="btn" style={{ background: 'rgba(255,255,255,.15)', color: 'white' }}><Briefcase size={15} /> Mis Emprendimientos</Link>}
        </div>
      </div>

      {/* Stats row */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 20, marginBottom: 28 }}>
        <div className="metric-card">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
            <div className="metric-title">Emprendimientos</div>
            <div className="metric-icon" style={{ background: 'var(--primary-bg)' }}><Store size={22} color="var(--primary)" /></div>
          </div>
          <div className="metric-value">{emprendimientos.length}</div>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>activos en la plataforma</div>
        </div>
        {(isEmprendedor || isAdmin) && (
          <div className="metric-card">
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
              <div className="metric-title">Mis Emprendimientos</div>
              <div className="metric-icon" style={{ background: '#EDE9FE' }}><Briefcase size={22} color="#7C3AED" /></div>
            </div>
            <div className="metric-value">{misEmps.length}</div>
            <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>registrados por ti</div>
          </div>
        )}
        <div className="metric-card">
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
            <div className="metric-title">Pedidos</div>
            <div className="metric-icon" style={{ background: 'var(--secondary-bg)' }}><Package size={22} color="var(--secondary)" /></div>
          </div>
          <div className="metric-value">{pedidos.length}</div>
          <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>{isComprador ? 'realizados' : 'recibidos'}</div>
        </div>
        {isAdmin && (
          <div className="metric-card">
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
              <div className="metric-title">Destacados</div>
              <div className="metric-icon" style={{ background: 'var(--warning-bg)' }}><TrendingUp size={22} color="var(--warning)" /></div>
            </div>
            <div className="metric-value">{emprendimientos.filter(e => e.destacado).length}</div>
            <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>emprendimientos destacados</div>
          </div>
        )}
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 24 }}>
        {/* Destacados */}
        <div className="card" style={{ margin: 0 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
            <h3 style={{ fontWeight: 700, fontSize: 15, margin: 0 }}>⭐ Destacados de la Semana</h3>
            <Link to="/marketplace" style={{ fontSize: 12, color: 'var(--primary)', textDecoration: 'none', fontWeight: 600 }}>Ver todos →</Link>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            {destacados.map(emp => (
              <Link
                key={emp.idEmprendimiento}
                to={`/emprendimientos/${emp.idEmprendimiento}`}
                style={{ textDecoration: 'none', display: 'flex', alignItems: 'center', gap: 12, padding: '10px 12px', borderRadius: 'var(--radius-md)', transition: 'background .15s' }}
                onMouseEnter={e => (e.currentTarget.style.background = 'var(--bg)')}
                onMouseLeave={e => (e.currentTarget.style.background = 'transparent')}
              >
                {emp.logoUrl
                  ? <img src={emp.logoUrl} alt={emp.nombre} style={{ width: 44, height: 44, borderRadius: 'var(--radius-md)', objectFit: 'cover' }} />
                  : <div style={{ width: 44, height: 44, borderRadius: 'var(--radius-md)', background: 'var(--primary-bg)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Store size={20} color="var(--primary)" /></div>
                }
                <div>
                  <div style={{ fontWeight: 600, fontSize: 13.5, color: 'var(--text)' }}>{emp.nombre}</div>
                  <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>{emp.nombreCategoria}</div>
                </div>
              </Link>
            ))}
            {destacados.length === 0 && <p style={{ color: 'var(--text-muted)', fontSize: 13, textAlign: 'center', padding: '16px 0' }}>No hay emprendimientos destacados aún</p>}
          </div>
        </div>

        {/* Recent activity */}
        <div className="card" style={{ margin: 0 }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 20 }}>
            <h3 style={{ fontWeight: 700, fontSize: 15, margin: 0 }}>
              {isComprador ? '🛍️ Mis Pedidos Recientes' : '📦 Pedidos Recientes'}
            </h3>
            <Link to={isComprador ? '/mis-pedidos' : '/pedidos-recibidos'} style={{ fontSize: 12, color: 'var(--primary)', textDecoration: 'none', fontWeight: 600 }}>Ver todos →</Link>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {pedidos.slice(0, 5).map(p => (
              <div key={p.idPedido} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '10px 12px', borderRadius: 'var(--radius-md)', background: 'var(--bg)' }}>
                <div>
                  <div style={{ fontWeight: 600, fontSize: 13 }}>Pedido #{p.idPedido}</div>
                  <div style={{ fontSize: 11.5, color: 'var(--text-muted)' }}>{p.nombreEmprendimiento || 'Emprendimiento'}</div>
                </div>
                <span className={`badge badge-${p.nombreEstado}`}>{p.nombreEstado}</span>
              </div>
            ))}
            {pedidos.length === 0 && <p style={{ color: 'var(--text-muted)', fontSize: 13, textAlign: 'center', padding: '16px 0' }}>No tienes pedidos aún</p>}
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};

export default HomePage;
