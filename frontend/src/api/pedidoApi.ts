import axiosInstance from './axiosConfig';
import type { PedidoRequest, PedidoResponse } from '../types/pedido.types';

export const getAll = (): Promise<PedidoResponse[]> =>
  axiosInstance.get('/pedidos').then((r) => r.data);

export const getById = (id: number): Promise<PedidoResponse> =>
  axiosInstance.get(`/pedidos/${id}`).then((r) => r.data);

export const getMisPedidos = (): Promise<PedidoResponse[]> =>
  axiosInstance.get('/pedidos/mis-pedidos').then((r) => r.data);

export const getPedidosRecibidos = (): Promise<PedidoResponse[]> =>
  axiosInstance.get('/pedidos/recibidos').then((r) => r.data);

export const create = (data: PedidoRequest): Promise<PedidoResponse> =>
  axiosInstance.post('/pedidos', data).then((r) => r.data);

export const update = (id: number, data: Partial<PedidoRequest>): Promise<PedidoResponse> =>
  axiosInstance.put(`/pedidos/${id}`, data).then((r) => r.data);

export const remove = (id: number): Promise<void> =>
  axiosInstance.delete(`/pedidos/${id}`);
