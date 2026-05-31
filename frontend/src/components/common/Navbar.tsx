import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import NotificacionBell from '../notificaciones/NotificacionBell';
import {
  Store,
  ShoppingBag,
  Briefcase,
  Package,
  BarChart3,
  ShoppingCart,
  Shield,
  Users,
  Tag,
  Building2,
  User,
  LogOut,
  Menu,
  X,
  ChevronDown,
} from 'lucide-react';

const Navbar: React.FC = () => {
  const { correo, isAdmin, isEmprendedor, isComprador, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="bg-white border-b border-gray-100 shadow-sm sticky top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link
            to="/marketplace"
            className="flex items-center gap-2 font-bold text-blue-600 text-lg hover:text-blue-700 transition-colors"
          >
            <Store className="w-6 h-6" />
            <span>Emprende ICESI</span>
          </Link>

          {/* Desktop links */}
          <div className="hidden md:flex items-center gap-1">
            <Link
              to="/marketplace"
              className="flex items-center gap-1.5 text-gray-600 hover:text-blue-600 hover:bg-blue-50 px-3 py-2 rounded-lg transition-all text-sm font-medium"
            >
              <ShoppingBag className="w-4 h-4" />
              Marketplace
            </Link>

            {isEmprendedor && (
              <>
                <Link
                  to="/mis-emprendimientos"
                  className="flex items-center gap-1.5 text-gray-600 hover:text-blue-600 hover:bg-blue-50 px-3 py-2 rounded-lg transition-all text-sm font-medium"
                >
                  <Briefcase className="w-4 h-4" />
                  Mis Emprendimientos
                </Link>
                <Link
                  to="/pedidos-recibidos"
                  className="flex items-center gap-1.5 text-gray-600 hover:text-blue-600 hover:bg-blue-50 px-3 py-2 rounded-lg transition-all text-sm font-medium"
                >
                  <Package className="w-4 h-4" />
                  Pedidos Recibidos
                </Link>
                <Link
                  to="/metricas"
                  className="flex items-center gap-1.5 text-gray-600 hover:text-blue-600 hover:bg-blue-50 px-3 py-2 rounded-lg transition-all text-sm font-medium"
                >
                  <BarChart3 className="w-4 h-4" />
                  Métricas
                </Link>
              </>
            )}

            {isComprador && (
              <Link
                to="/mis-pedidos"
                className="flex items-center gap-1.5 text-gray-600 hover:text-blue-600 hover:bg-blue-50 px-3 py-2 rounded-lg transition-all text-sm font-medium"
              >
                <ShoppingCart className="w-4 h-4" />
                Mis Pedidos
              </Link>
            )}

            {isAdmin && (
              <div className="relative group">
                <button className="flex items-center gap-1.5 text-gray-600 hover:text-blue-600 hover:bg-blue-50 px-3 py-2 rounded-lg transition-all text-sm font-medium">
                  <Shield className="w-4 h-4" />
                  Admin
                  <ChevronDown className="w-3 h-3" />
                </button>
                <div className="absolute hidden group-hover:block top-full left-0 w-52 bg-white shadow-lg rounded-xl border border-gray-100 py-2 mt-1 z-50">
                  <Link
                    to="/admin/usuarios"
                    className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 transition-colors"
                  >
                    <Users className="w-4 h-4" />
                    Usuarios
                  </Link>
                  <Link
                    to="/admin/categorias"
                    className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 transition-colors"
                  >
                    <Tag className="w-4 h-4" />
                    Categorías
                  </Link>
                  <Link
                    to="/admin/emprendimientos"
                    className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 transition-colors"
                  >
                    <Building2 className="w-4 h-4" />
                    Emprendimientos
                  </Link>
                </div>
              </div>
            )}
          </div>

          {/* Right side */}
          <div className="flex items-center gap-2">
            {isEmprendedor && <NotificacionBell />}
            <div className="hidden md:flex items-center gap-1.5 bg-gray-100 px-3 py-1.5 rounded-full text-xs text-gray-600 max-w-40 truncate">
              <User className="w-3 h-3 shrink-0" />
              <span className="truncate">{correo}</span>
            </div>
            <button
              onClick={handleLogout}
              className="flex items-center gap-1.5 bg-red-50 text-red-600 hover:bg-red-100 px-3 py-1.5 rounded-xl text-sm font-semibold transition-all"
            >
              <LogOut className="w-4 h-4" />
              <span className="hidden sm:inline">Salir</span>
            </button>

            {/* Mobile hamburger */}
            <button
              onClick={() => setMenuOpen((o) => !o)}
              className="md:hidden p-2 text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
              aria-label="Menú"
            >
              {menuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>
          </div>
        </div>

        {/* Mobile menu */}
        {menuOpen && (
          <div className="md:hidden border-t border-gray-100 py-3 flex flex-col gap-1 pb-4">
            <Link
              to="/marketplace"
              onClick={() => setMenuOpen(false)}
              className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 rounded-lg transition-colors"
            >
              <ShoppingBag className="w-4 h-4" />
              Marketplace
            </Link>
            {isEmprendedor && (
              <>
                <Link
                  to="/mis-emprendimientos"
                  onClick={() => setMenuOpen(false)}
                  className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 rounded-lg transition-colors"
                >
                  <Briefcase className="w-4 h-4" />
                  Mis Emprendimientos
                </Link>
                <Link
                  to="/pedidos-recibidos"
                  onClick={() => setMenuOpen(false)}
                  className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 rounded-lg transition-colors"
                >
                  <Package className="w-4 h-4" />
                  Pedidos Recibidos
                </Link>
                <Link
                  to="/metricas"
                  onClick={() => setMenuOpen(false)}
                  className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 rounded-lg transition-colors"
                >
                  <BarChart3 className="w-4 h-4" />
                  Métricas
                </Link>
              </>
            )}
            {isComprador && (
              <Link
                to="/mis-pedidos"
                onClick={() => setMenuOpen(false)}
                className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 rounded-lg transition-colors"
              >
                <ShoppingCart className="w-4 h-4" />
                Mis Pedidos
              </Link>
            )}
            {isAdmin && (
              <>
                <Link
                  to="/admin/usuarios"
                  onClick={() => setMenuOpen(false)}
                  className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 rounded-lg transition-colors"
                >
                  <Users className="w-4 h-4" />
                  Admin — Usuarios
                </Link>
                <Link
                  to="/admin/categorias"
                  onClick={() => setMenuOpen(false)}
                  className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 rounded-lg transition-colors"
                >
                  <Tag className="w-4 h-4" />
                  Admin — Categorías
                </Link>
                <Link
                  to="/admin/emprendimientos"
                  onClick={() => setMenuOpen(false)}
                  className="flex items-center gap-2 px-4 py-2.5 text-sm text-gray-700 hover:bg-blue-50 hover:text-blue-600 rounded-lg transition-colors"
                >
                  <Building2 className="w-4 h-4" />
                  Admin — Emprendimientos
                </Link>
              </>
            )}
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
