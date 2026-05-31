import axiosInstance from './axiosConfig';
import type { CategoriaRequest, CategoriaResponse } from '../types/categoria.types';

export const getAll = (): Promise<CategoriaResponse[]> =>
  axiosInstance.get('/categorias').then((r) => r.data);

export const getById = (id: number): Promise<CategoriaResponse> =>
  axiosInstance.get(`/categorias/${id}`).then((r) => r.data);

export const create = (data: CategoriaRequest): Promise<CategoriaResponse> =>
  axiosInstance.post('/categorias', data).then((r) => r.data);

export const update = (id: number, data: CategoriaRequest): Promise<CategoriaResponse> =>
  axiosInstance.put(`/categorias/${id}`, data).then((r) => r.data);

export const remove = (id: number): Promise<void> =>
  axiosInstance.delete(`/categorias/${id}`);
