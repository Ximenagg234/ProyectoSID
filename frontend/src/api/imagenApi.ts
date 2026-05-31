import axiosInstance from './axiosConfig';
import type { UsuarioResponse } from '../types/usuario.types';

export interface ImagenResponse {
  idImagen: number;
  urlImagen: string;
}

export const getImagenesProducto = (idProducto: number): Promise<ImagenResponse[]> =>
  axiosInstance.get(`/productos/${idProducto}/imagenes`).then((r) => r.data);

export const uploadImagenProducto = (
  idProducto: number,
  file: File
): Promise<ImagenResponse> => {
  const form = new FormData();
  form.append('file', file);
  return axiosInstance
    .post(`/productos/${idProducto}/imagenes`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    .then((r) => r.data);
};

export const deleteImagenProducto = (idProducto: number, idImagen: number): Promise<void> =>
  axiosInstance.delete(`/productos/${idProducto}/imagenes/${idImagen}`);

export const uploadFotoPerfil = (idUsuario: number, file: File): Promise<UsuarioResponse> => {
  const form = new FormData();
  form.append('file', file);
  return axiosInstance
    .post(`/usuarios/${idUsuario}/foto`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    .then((r) => r.data);
};
