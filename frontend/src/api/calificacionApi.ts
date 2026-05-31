import axiosInstance from './axiosConfig';

export interface CalificacionResponse {
  idCalificacion: number;
  puntuacion: number;
  comentario: string | null;
  fecha: string;
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
  axiosInstance
    .post(`/emprendimientos/${idEmprendimiento}/calificaciones`, data)
    .then((r) => r.data);
