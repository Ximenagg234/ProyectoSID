import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './components/auth/ProtectedRoute';
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import HomePage from './pages/home/HomePage';
import MarketplacePage from './pages/marketplace/MarketplacePage';
import MisEmprendimientosPage from './pages/emprendimientos/MisEmprendimientosPage';
import EmprendimientoFormPage from './pages/emprendimientos/EmprendimientoFormPage';
import EmprendimientoDetailPage from './pages/emprendimientos/EmprendimientoDetailPage';
import ProductosPage from './pages/productos/ProductosPage';
import MisPedidosPage from './pages/pedidos/MisPedidosPage';
import PedidosRecibidosPage from './pages/pedidos/PedidosRecibidosPage';
import MetricasPage from './pages/metricas/MetricasPage';
import AdminUsuariosPage from './pages/admin/AdminUsuariosPage';
import AdminCategoriasPage from './pages/admin/AdminCategoriasPage';
import AdminEmprendimientosPage from './pages/admin/AdminEmprendimientosPage';
import CartPage from './pages/cart/CartPage';
import ProfilePage from './pages/profile/ProfilePage';
import NotFoundPage from './pages/NotFoundPage';

function App(): JSX.Element {
  return (
    <BrowserRouter>
      <Routes>
        {/* ── Públicas ── */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/registro" element={<RegisterPage />} />
        <Route path="/" element={<Navigate to="/home" replace />} />

        {/* ── Cualquier usuario autenticado ── */}
        <Route
          path="/home"
          element={
            <ProtectedRoute>
              <HomePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/marketplace"
          element={
            <ProtectedRoute>
              <MarketplacePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/emprendimientos/:id"
          element={
            <ProtectedRoute>
              <EmprendimientoDetailPage />
            </ProtectedRoute>
          }
        />

        {/* ── EMPRENDEDOR + ADMIN ── */}
        <Route
          path="/mis-emprendimientos"
          element={
            <ProtectedRoute rolesPermitidos={['ROLE_EMPRENDEDOR', 'ROLE_ADMIN']}>
              <MisEmprendimientosPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/mis-emprendimientos/nuevo"
          element={
            <ProtectedRoute rolesPermitidos={['ROLE_EMPRENDEDOR', 'ROLE_ADMIN']}>
              <EmprendimientoFormPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/mis-emprendimientos/editar/:id"
          element={
            <ProtectedRoute rolesPermitidos={['ROLE_EMPRENDEDOR', 'ROLE_ADMIN']}>
              <EmprendimientoFormPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/mis-emprendimientos/:id/productos"
          element={
            <ProtectedRoute rolesPermitidos={['ROLE_EMPRENDEDOR', 'ROLE_ADMIN']}>
              <ProductosPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/pedidos-recibidos"
          element={
            <ProtectedRoute rolesPermitidos={['ROLE_EMPRENDEDOR', 'ROLE_ADMIN']}>
              <PedidosRecibidosPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/metricas"
          element={
            <ProtectedRoute rolesPermitidos={['ROLE_EMPRENDEDOR', 'ROLE_ADMIN']}>
              <MetricasPage />
            </ProtectedRoute>
          }
        />

        {/* ── Carrito y pedidos (todos los autenticados) ── */}
        <Route
          path="/carrito"
          element={
            <ProtectedRoute>
              <CartPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/mis-pedidos"
          element={
            <ProtectedRoute>
              <MisPedidosPage />
            </ProtectedRoute>
          }
        />

        {/* ── Solo ADMIN ── */}
        <Route
          path="/admin/usuarios"
          element={
            <ProtectedRoute rolesPermitidos={['ROLE_ADMIN']}>
              <AdminUsuariosPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/categorias"
          element={
            <ProtectedRoute rolesPermitidos={['ROLE_ADMIN']}>
              <AdminCategoriasPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/emprendimientos"
          element={
            <ProtectedRoute rolesPermitidos={['ROLE_ADMIN']}>
              <AdminEmprendimientosPage />
            </ProtectedRoute>
          }
        />

        {/* ── Perfil (todos los autenticados) ── */}
        <Route
          path="/perfil"
          element={
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          }
        />

        {/* ── Fallbacks ── */}
        <Route
          path="/acceso-denegado"
          element={
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', background: 'var(--bg)' }}>
              <div style={{ textAlign: 'center', padding: 40 }}>
                <div style={{ fontSize: 64, marginBottom: 16 }}>🚫</div>
                <h1 style={{ fontSize: 56, fontWeight: 800, color: 'var(--danger)', marginBottom: 8 }}>403</h1>
                <p style={{ fontSize: 18, color: 'var(--text-muted)', marginBottom: 28 }}>No tienes permiso para acceder aquí.</p>
                <a href="/home" className="btn btn-primary">← Volver al inicio</a>
              </div>
            </div>
          }
        />
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
