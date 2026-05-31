import axiosInstance from './axiosConfig';

export interface RolResponse {
  idRol: number;
  nombre: string;
}

export const getRoles = (): Promise<RolResponse[]> =>
  axiosInstance.get('/roles').then((r) => r.data);

export const asignarRol = (idUsuario: number, idRol: number): Promise<unknown> =>
  axiosInstance.post(`/usuarios/${idUsuario}/roles/${idRol}`).then((r) => r.data);

export const quitarRol = (idUsuario: number, idRol: number): Promise<unknown> =>
  axiosInstance.delete(`/usuarios/${idUsuario}/roles/${idRol}`).then((r) => r.data);
