import axiosInstance from './axiosConfig';
import type { ProductoRequest, ProductoResponse } from '../types/producto.types';

export const getAll = (): Promise<ProductoResponse[]> =>
  axiosInstance.get('/productos').then((r) => r.data);

export const getById = (id: number): Promise<ProductoResponse> =>
  axiosInstance.get(`/productos/${id}`).then((r) => r.data);

export const create = (data: ProductoRequest): Promise<ProductoResponse> =>
  axiosInstance.post('/productos', data).then((r) => r.data);

export const update = (id: number, data: ProductoRequest): Promise<ProductoResponse> =>
  axiosInstance.put(`/productos/${id}`, data).then((r) => r.data);

export const remove = (id: number): Promise<void> =>
  axiosInstance.delete(`/productos/${id}`);
