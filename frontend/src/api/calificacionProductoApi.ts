import axiosInstance from './axiosConfig';

export interface CalificacionProductoResponse {
  idCalificacionProducto: number;
  puntuacion: number;
  comentario: string | null;
  fecha: string;
  idUsuario: number;
  nombreUsuario: string;
  idProducto: number;
}

export interface CalificacionProductoRequest {
  puntuacion: number;
  comentario: string;
  idUsuario: number;
}

export const getCalificacionesProducto = (idProducto: number): Promise<CalificacionProductoResponse[]> =>
  axiosInstance.get(`/productos/${idProducto}/calificaciones`).then((r) => r.data);

export const getPromedioProducto = (idProducto: number): Promise<{ promedio: number; total: number }> =>
  axiosInstance.get(`/productos/${idProducto}/calificaciones/promedio`).then((r) => r.data);

export const yaCalificoProducto = (idProducto: number, idUsuario: number): Promise<{ yaCalifico: boolean }> =>
  axiosInstance.get(`/productos/${idProducto}/ya-califico`, { params: { idUsuario } }).then((r) => r.data);

export const calificarProducto = (
  idProducto: number,
  data: CalificacionProductoRequest
): Promise<CalificacionProductoResponse> =>
  axiosInstance.post(`/productos/${idProducto}/calificaciones`, data).then((r) => r.data);

export const actualizarProducto = (
  idProducto: number,
  idCalificacion: number,
  data: CalificacionProductoRequest
): Promise<CalificacionProductoResponse> =>
  axiosInstance.put(`/productos/${idProducto}/calificaciones/${idCalificacion}`, data).then((r) => r.data);

export const eliminarProducto = (
  idProducto: number,
  idCalificacion: number,
  idUsuario: number
): Promise<void> =>
  axiosInstance.delete(`/productos/${idProducto}/calificaciones/${idCalificacion}`, {
    params: { idUsuario },
  }).then(() => undefined);
