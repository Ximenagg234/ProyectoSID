import { useEffect, useState } from 'react';
import * as pedidoApi from '../../api/pedidoApi';
import type { PedidoResponse } from '../../types/pedido.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import PedidoCard from '../../components/pedidos/PedidoCard';

// Map estado name → id (must match data.sql: ACTIVO=1, INACTIVO=2, PENDIENTE=3, CONFIRMADO=4, PREPARANDO=5, ENTREGADO=6, CANCELADO=7)
const ESTADO_ID_MAP: Record<string, number> = {
  PENDIENTE:  3,
  CONFIRMADO: 4,
  PREPARANDO: 5,
  ENTREGADO:  6,
  CANCELADO:  7,
};

const PedidosRecibidosPage: React.FC = () => {
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    pedidoApi
      .getPedidosRecibidos()
      .then(setPedidos)
      .catch(() => setError('Error al cargar los pedidos recibidos'))
      .finally(() => setLoading(false));
  }, []);

  const handleCambiarEstado = async (idPedido: number, nuevoEstado: string) => {
    const idEstado = ESTADO_ID_MAP[nuevoEstado] ?? 3; // fallback: PENDIENTE
    try {
      await pedidoApi.update(idPedido, { idEstado } as Parameters<typeof pedidoApi.update>[1]);
      const nuevos = await pedidoApi.getPedidosRecibidos();
      setPedidos(nuevos);
    } catch {
      setError('No se pudo actualizar el estado');
    }
  };

  return (
    <DashboardLayout title="Pedidos Recibidos" subtitle="Gestiona los pedidos de tus emprendimientos">
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

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
          <div style={{ fontSize: 48, marginBottom: 16 }}>📭</div>
          <p style={{ fontWeight: 600, fontSize: 16 }}>No hay pedidos recibidos aún</p>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: 20 }}>
        {pedidos.map((p) => (
          <PedidoCard
            key={p.idPedido}
            pedido={p}
            onCambiarEstado={handleCambiarEstado}
          />
        ))}
      </div>
    </DashboardLayout>
  );
};

export default PedidosRecibidosPage;
