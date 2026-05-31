import axiosInstance from './axiosConfig';
import type { UsuarioRequest, UsuarioResponse } from '../types/usuario.types';

export const getAll = (): Promise<UsuarioResponse[]> =>
  axiosInstance.get('/usuarios').then((r) => r.data);

export const getById = (id: number): Promise<UsuarioResponse> =>
  axiosInstance.get(`/usuarios/${id}`).then((r) => r.data);

export const create = (data: UsuarioRequest): Promise<UsuarioResponse> =>
  axiosInstance.post('/usuarios', data).then((r) => r.data);

export const update = (id: number, data: UsuarioRequest): Promise<UsuarioResponse> =>
  axiosInstance.put(`/usuarios/${id}`, data).then((r) => r.data);

export const remove = (id: number): Promise<void> =>
  axiosInstance.delete(`/usuarios/${id}`);
