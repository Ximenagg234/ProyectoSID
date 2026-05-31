import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

interface ProtectedRouteProps {
  children: React.ReactNode;
  rolesPermitidos?: string[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
  children,
  rolesPermitidos = [],
}) => {
  const { isAuthenticated, roles } = useAuth();
  const safeRoles = roles ?? [];
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (rolesPermitidos.length > 0 && !safeRoles.some((r) => rolesPermitidos.includes(r))) {
    return <Navigate to="/acceso-denegado" replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
