import axiosInstance from './axiosConfig';
import type { LoginRequest, LoginResponse } from '../types/auth.types';
import type { UsuarioRequest, UsuarioResponse } from '../types/usuario.types';

export const login = (data: LoginRequest): Promise<LoginResponse> =>
  axiosInstance.post('/auth/login', data).then((r) => r.data);

export const register = (data: UsuarioRequest, idRol = 3): Promise<UsuarioResponse> =>
  axiosInstance.post(`/auth/register?idRol=${idRol}`, data).then((r) => r.data);
