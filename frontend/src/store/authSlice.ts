import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import type { AuthState } from '../types/auth.types';

/** Elige el rol de mayor privilegio para mostrar en la UI */
const rolPrincipal = (roles: string[]): string => {
  if (roles.includes('ROLE_ADMIN')) return 'ROLE_ADMIN';
  if (roles.includes('ROLE_EMPRENDEDOR')) return 'ROLE_EMPRENDEDOR';
  if (roles.includes('ROLE_COMPRADOR')) return 'ROLE_COMPRADOR';
  // Nombres sin prefijo ROLE_
  if (roles.includes('ADMIN')) return 'ROLE_ADMIN';
  if (roles.includes('EMPRENDEDOR')) return 'ROLE_EMPRENDEDOR';
  if (roles.includes('COMPRADOR')) return 'ROLE_COMPRADOR';
  return roles[0] ?? '';
};

const initialState: AuthState = {
  token: null,
  correo: null,
  rol: null,
  roles: [],
  idUsuario: null,
  fotoPerfil: null,
  isAuthenticated: false,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials: (
      state,
      action: PayloadAction<{ token: string; correo: string; roles: string[]; idUsuario?: number }>
    ) => {
      state.token = action.payload.token;
      state.correo = action.payload.correo;
      state.roles = action.payload.roles;
      state.rol = rolPrincipal(action.payload.roles);
      state.idUsuario = action.payload.idUsuario ?? null;
      state.isAuthenticated = true;
      localStorage.setItem('token', action.payload.token);
    },
    setFotoPerfil: (state, action: PayloadAction<string | null>) => {
      state.fotoPerfil = action.payload;
    },
    logout: (state) => {
      state.token = null;
      state.correo = null;
      state.rol = null;
      state.roles = [];
      state.idUsuario = null;
      state.fotoPerfil = null;
      state.isAuthenticated = false;
      localStorage.removeItem('token');
    },
  },
});

export const { setCredentials, setFotoPerfil, logout } = authSlice.actions;
export default authSlice.reducer;
