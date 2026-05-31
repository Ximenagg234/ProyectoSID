import axiosInstance from './axiosConfig';
import type { EmprendimientoRequest, EmprendimientoResponse } from '../types/emprendimiento.types';

export const getAll = (): Promise<EmprendimientoResponse[]> =>
  axiosInstance.get('/emprendimientos').then((r) => r.data);

export const getById = (id: number): Promise<EmprendimientoResponse> =>
  axiosInstance.get(`/emprendimientos/${id}`).then((r) => r.data);

export const create = (data: EmprendimientoRequest): Promise<EmprendimientoResponse> =>
  axiosInstance.post('/emprendimientos', data).then((r) => r.data);

export const update = (id: number, data: EmprendimientoRequest): Promise<EmprendimientoResponse> =>
  axiosInstance.put(`/emprendimientos/${id}`, data).then((r) => r.data);

export const remove = (id: number): Promise<void> =>
  axiosInstance.delete(`/emprendimientos/${id}`);
