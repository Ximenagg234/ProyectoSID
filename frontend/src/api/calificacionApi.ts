import axiosInstance from './axiosConfig';

export interface CalificacionResponse {
  idCalificacion: number;
  puntuacion: number;
  comentario: string | null;
  fecha: string;
  idUsuario: number;
  nombreUsuario: string;
  idEmprendimiento: number;
  idPedido: number;
}

export interface CalificacionRequest {
  puntuacion: number;
  comentario: string;
  idUsuario: number;
  idPedido: number;
}

export const getCalificaciones = (idEmprendimiento: number): Promise<CalificacionResponse[]> =>
  axiosInstance.get(`/emprendimientos/${idEmprendimiento}/calificaciones`).then((r) => r.data);

export const getPromedio = (idEmprendimiento: number): Promise<{ promedio: number; total: number }> =>
  axiosInstance.get(`/emprendimientos/${idEmprendimiento}/calificaciones/promedio`).then((r) => r.data);

export const yaCalifico = (idPedido: number): Promise<{ yaCalifico: boolean }> =>
  axiosInstance.get(`/pedidos/${idPedido}/ya-califico`).then((r) => r.data);

export const calificar = (
  idEmprendimiento: number,
  data: CalificacionRequest
): Promise<CalificacionResponse> =>
  axiosInstance.post(`/emprendimientos/${idEmprendimiento}/calificaciones`, data).then((r) => r.data);

export const actualizar = (
  idEmprendimiento: number,
  idCalificacion: number,
  data: CalificacionRequest
): Promise<CalificacionResponse> =>
  axiosInstance.put(`/emprendimientos/${idEmprendimiento}/calificaciones/${idCalificacion}`, data).then((r) => r.data);

export const eliminar = (
  idEmprendimiento: number,
  idCalificacion: number,
  idUsuario: number
): Promise<void> =>
  axiosInstance.delete(`/emprendimientos/${idEmprendimiento}/calificaciones/${idCalificacion}`, {
    params: { idUsuario },
  }).then(() => undefined);
