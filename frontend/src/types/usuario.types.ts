export interface UsuarioResponse {
  idUsuario: number;
  nombreCompleto: string;
  correoInstitucional: string;
  programaAcademico: string;
  semestreAcademico: number;
  fotoPerfil: string;
  roles: string[];
}

export interface UsuarioRequest {
  nombreCompleto: string;
  correoInstitucional: string;
  programaAcademico: string;
  semestreAcademico: number;
  fotoPerfil: string;
  clave: string;
}
