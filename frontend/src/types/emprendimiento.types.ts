export interface EmprendimientoResponse {
  idEmprendimiento: number;
  nombre: string;
  descripcion: string;
  logoUrl: string;
  destacado: boolean;
  nombreUsuario: string;
  nombreCategoria: string;
  nombreSemestre: string;
  nombreEstado: string;
}

export interface EmprendimientoRequest {
  nombre: string;
  descripcion: string;
  logoUrl: string;
  idUsuario: number;
  idCategoria: number;
  idSemestre: number;
  idEstado: number;
}
