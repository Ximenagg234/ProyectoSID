import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import * as pedidoApi from '../../api/pedidoApi';
import * as calificacionApi from '../../api/calificacionApi';
import type { PedidoResponse } from '../../types/pedido.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import PedidoCard from '../../components/pedidos/PedidoCard';
import CalificarModal from '../../components/calificaciones/CalificarModal';
import { useAuth } from '../../hooks/useAuth';
import { ShoppingBag, Package, Star } from 'lucide-react';

const MisPedidosPage: React.FC = () => {
  const { isEmprendedor } = useAuth();
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Track which delivered orders have already been rated
  const [yaCalificados, setYaCalificados] = useState<Set<number>>(new Set());
  // Modal state
  const [modalPedido, setModalPedido] = useState<PedidoResponse | null>(null);

  useEffect(() => {
    void (async () => {
      setLoading(true);
      try {
        const data = await pedidoApi.getMisPedidos();
        setPedidos(data);
        const entregados = data.filter((p) => p.nombreEstado === 'ENTREGADO');
        const checks = await Promise.all(
          entregados.map((p) =>
            calificacionApi.yaCalifico(p.idPedido).then((r) => ({ id: p.idPedido, ya: r.yaCalifico }))
          )
        );
        const yaSet = new Set(checks.filter((c) => c.ya).map((c) => c.id));
        setYaCalificados(yaSet);
      } catch {
        setError('Error al cargar tus compras');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const handleCalificacionExitosa = () => {
    if (modalPedido) {
      setYaCalificados((prev) => new Set([...prev, modalPedido.idPedido]));
    }
    setModalPedido(null);
  };

  return (
    <DashboardLayout title="Mis Compras" subtitle="Pedidos que has realizado en el marketplace">
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {/* Aviso para emprendedores: sus pedidos recibidos están en otro lado */}
      {isEmprendedor && !loading && !error && (
        <div
          style={{
            background: 'var(--primary-bg)',
            border: '1px solid #C7D2FE',
            borderRadius: 'var(--radius-md)',
            padding: '12px 18px',
            marginBottom: 24,
            fontSize: 13,
            color: 'var(--primary)',
            display: 'flex',
            alignItems: 'center',
            gap: 10,
          }}
        >
          <Package size={16} style={{ flexShrink: 0 }} />
          <span>
            ¿Buscas los pedidos que <strong>te han hecho a ti</strong>? Esos están en{' '}
            <Link to="/pedidos-recibidos" style={{ color: 'var(--primary)', fontWeight: 700, textDecoration: 'underline' }}>
              Pedidos Recibidos
            </Link>
            , en la sección Mi Negocio del menú.
          </span>
        </div>
      )}

      {!loading && !error && pedidos.length === 0 && (
        <div
          style={{
            textAlign: 'center',
            padding: '64px 0',
            color: 'var(--text-muted)',
            background: 'white',
            borderRadius: 'var(--radius-xl)',
            border: '1px solid var(--border)',
          }}
        >
          <ShoppingBag size={48} style={{ opacity: 0.2, display: 'block', margin: '0 auto 16px' }} />
          <p style={{ fontWeight: 600, fontSize: 16, marginBottom: 8 }}>Aún no has realizado compras</p>
          <p style={{ fontSize: 13, color: 'var(--text-muted)', marginBottom: 24 }}>
            Explora el marketplace y agrega productos a tu carrito
          </p>
          <Link to="/marketplace" className="btn btn-primary" style={{ textDecoration: 'none', display: 'inline-flex' }}>
            <ShoppingBag size={14} /> Ir al Marketplace
          </Link>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: 20 }}>
        {pedidos.map((p) => (
          <div key={p.idPedido}>
            <PedidoCard pedido={p} />
            {/* Calificar button for ENTREGADO orders */}
            {p.nombreEstado === 'ENTREGADO' && (
              <div style={{ marginTop: 8 }}>
                {yaCalificados.has(p.idPedido) ? (
                  <div
                    style={{
                      display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6,
                      padding: '8px 16px',
                      background: '#FFFBEB', border: '1px solid #FDE68A',
                      borderRadius: 'var(--radius-md)',
                      fontSize: 13, color: '#92400E', fontWeight: 600,
                    }}
                  >
                    <Star size={14} fill="#F59E0B" color="#F59E0B" />
                    Ya calificaste este pedido
                  </div>
                ) : (
                  <button
                    onClick={() => setModalPedido(p)}
                    className="btn btn-outline"
                    style={{
                      width: '100%',
                      display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6,
                      borderColor: '#F59E0B', color: '#D97706',
                    }}
                  >
                    <Star size={14} fill="none" color="#D97706" />
                    Calificar emprendimiento
                  </button>
                )}
              </div>
            )}
          </div>
        ))}
      </div>

      {/* Rating modal */}
      {modalPedido && (
        <CalificarModal
          idPedido={modalPedido.idPedido}
          idEmprendimiento={modalPedido.idEmprendimiento}
          nombreEmprendimiento={modalPedido.nombreEmprendimiento}
          onClose={() => setModalPedido(null)}
          onSuccess={handleCalificacionExitosa}
        />
      )}
    </DashboardLayout>
  );
};

export default MisPedidosPage;
