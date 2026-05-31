export interface LoginRequest {
  correoInstitucional: string;
  clave: string;
}

export interface LoginResponse {
  token: string;
  tipo: string;
  correoInstitucional: string;
  roles: string[];
  idUsuario?: number;
}

export interface AuthState {
  token: string | null;
  correo: string | null;
  rol: string | null;       // rol principal para mostrar en UI (el de mayor privilegio)
  roles: string[];          // todos los roles del usuario
  idUsuario: number | null;
  fotoPerfil: string | null;
  isAuthenticated: boolean;
}
