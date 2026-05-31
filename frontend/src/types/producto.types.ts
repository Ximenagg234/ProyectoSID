export interface ProductoResponse {
  idProducto: number;
  nombre: string;
  descripcion: string;
  precio: number;
  stockDisponible: number;
  primeraImagenUrl: string | null;
  idEmprendimiento: number;
  nombreEmprendimiento: string;
  nombreEstado: string;
}

export interface ProductoRequest {
  nombre: string;
  descripcion: string;
  precio: number;
  stockDisponible: number;
  idEmprendimiento: number;
  idEstado: number;
}
