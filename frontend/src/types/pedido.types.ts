export interface DetallePedidoResponse {
  idDetalle: number;
  idProducto: number;
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

export interface PedidoResponse {
  idPedido: number;
  idEmprendimiento: number;
  nombreEmprendimiento: string;
  nombreUsuario: string;
  nombreEstado: string;
  fechaPedido: string;
  detalles: DetallePedidoResponse[];
  total: number;
}

export interface DetallePedidoRequest {
  idProducto: number;
  cantidad: number;
  precioUnitario: number;
}

export interface PedidoRequest {
  idEmprendimiento: number;
  idUsuario: number;
  idEstado: number;
  detalles: DetallePedidoRequest[];
}
