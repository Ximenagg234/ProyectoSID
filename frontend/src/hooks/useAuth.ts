import { useSelector, useDispatch } from 'react-redux';
import type { RootState, AppDispatch } from '../store';
import { logout, setCredentials, setFotoPerfil as setFotoPerfilAction } from '../store/authSlice';

const hasRole = (roles: string[] | undefined, role: string) => {
  if (!roles) return false;
  return roles.includes(role) || roles.includes(`ROLE_${role}`);
};

export const useAuth = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { token, correo, rol, roles, idUsuario, fotoPerfil, isAuthenticated } = useSelector(
    (state: RootState) => state.auth
  );

  const safeRoles = roles ?? [];

  return {
    token,
    correo,
    rol,
    roles: safeRoles,
    idUsuario,
    fotoPerfil,
    isAuthenticated,
    isAdmin:       hasRole(safeRoles, 'ADMIN'),
    isEmprendedor: hasRole(safeRoles, 'EMPRENDEDOR'),
    isComprador:   hasRole(safeRoles, 'COMPRADOR'),
    logout: () => dispatch(logout()),
    setCredentials: (p: { token: string; correo: string; roles: string[]; idUsuario?: number }) =>
      dispatch(setCredentials(p)),
    setFotoPerfil: (url: string | null) => dispatch(setFotoPerfilAction(url)),
  };
};
