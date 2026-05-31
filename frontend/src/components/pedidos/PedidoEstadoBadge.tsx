interface Props {
  estado: string;
}

const PedidoEstadoBadge: React.FC<Props> = ({ estado }) => {
  const classMap: Record<string, string> = {
    PENDIENTE: 'badge badge-PENDIENTE',
    CONFIRMADO: 'badge badge-CONFIRMADO',
    PREPARANDO: 'badge badge-PREPARANDO',
    ENTREGADO: 'badge badge-ENTREGADO',
    CANCELADO: 'badge badge-CANCELADO',
  };
  const cls = classMap[estado] ?? 'badge badge-secondary';
  return <span className={cls}>{estado}</span>;
};

export default PedidoEstadoBadge;
