import { NavLink, useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import {
  Store, Home, ShoppingBag, Briefcase, Package, BarChart3,
  ShoppingCart, Users, Tag, Building2, LogOut, User
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import NotificacionBell from '../notificaciones/NotificacionBell';
import type { RootState } from '../../store';

const Sidebar: React.FC = () => {
  const { correo, rol, isAdmin, isEmprendedor, logout, fotoPerfil } = useAuth();
  const navigate = useNavigate();
  const cartCount = useSelector((state: RootState) =>
    state.cart.items.reduce((acc, item) => acc + item.cantidad, 0)
  );

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <aside className="app-sidebar">
      {/* Logo */}
      <div className="sidebar-logo">
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{ width: 36, height: 36, background: 'var(--primary)', borderRadius: 10, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Store size={20} color="white" />
          </div>
          <div>
            <div style={{ color: 'white', fontWeight: 800, fontSize: 15, lineHeight: 1.2 }}>Emprende</div>
            <div style={{ color: '#818CF8', fontWeight: 600, fontSize: 12 }}>ICESI</div>
          </div>
        </div>
      </div>

      {/* User info */}
      <div style={{ padding: '16px 20px', borderBottom: '1px solid rgba(255,255,255,.08)', display: 'flex', alignItems: 'center', gap: 10 }}>
        {fotoPerfil ? (
          <img
            src={fotoPerfil}
            alt="Foto de perfil"
            style={{ width: 36, height: 36, borderRadius: '50%', objectFit: 'cover', flexShrink: 0, border: '2px solid rgba(255,255,255,0.2)' }}
          />
        ) : (
          <div style={{ width: 36, height: 36, borderRadius: '50%', background: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
            <User size={16} color="white" />
          </div>
        )}
        <div style={{ overflow: 'hidden' }}>
          <div style={{ color: 'white', fontSize: 12.5, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{correo}</div>
          <div style={{ fontSize: 11, color: '#818CF8', fontWeight: 500 }}>{rol?.replace('ROLE_', '')}</div>
        </div>
        {isEmprendedor && <div style={{ marginLeft: 'auto' }}><NotificacionBell /></div>}
      </div>

      {/* Navigation */}
      <nav style={{ flex: 1, paddingBottom: 16 }}>
        <div className="sidebar-section-title">Principal</div>
        <NavLink to="/home" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
          <Home size={16} /><span>Inicio</span>
        </NavLink>

        <div className="sidebar-section-title">Marketplace</div>
        <NavLink to="/marketplace" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
          <ShoppingBag size={16} /><span>Marketplace</span>
        </NavLink>
        <NavLink to="/carrito" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
          <ShoppingCart size={16} />
          <span>Mi Carrito</span>
          {cartCount > 0 && (
            <span style={{ marginLeft: 'auto', background: 'var(--primary)', color: 'white', fontSize: 10, fontWeight: 700, padding: '2px 7px', borderRadius: 50, minWidth: 20, textAlign: 'center' }}>
              {cartCount}
            </span>
          )}
        </NavLink>
        <NavLink to="/mis-pedidos" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
          <Package size={16} /><span>Mis Compras</span>
        </NavLink>

        {(isEmprendedor || isAdmin) && (
          <>
            <div className="sidebar-section-title">Mi Negocio</div>
            <NavLink to="/mis-emprendimientos" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
              <Briefcase size={16} /><span>Mis Emprendimientos</span>
            </NavLink>
            <NavLink to="/pedidos-recibidos" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
              <Package size={16} /><span>Pedidos Recibidos</span>
            </NavLink>
            <NavLink to="/metricas" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
              <BarChart3 size={16} /><span>Métricas</span>
            </NavLink>
          </>
        )}

        {isAdmin && (
          <>
            <div className="sidebar-section-title">Administración</div>
            <NavLink to="/admin/usuarios" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
              <Users size={16} /><span>Usuarios</span>
            </NavLink>
            <NavLink to="/admin/categorias" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
              <Tag size={16} /><span>Categorías</span>
            </NavLink>
            <NavLink to="/admin/emprendimientos" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
              <Building2 size={16} /><span>Emprendimientos</span>
            </NavLink>
          </>
        )}

        <div className="sidebar-section-title">Cuenta</div>
        <NavLink to="/perfil" className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}>
          <User size={16} /><span>Mi Perfil</span>
        </NavLink>
        <button className="sidebar-link" onClick={handleLogout} style={{ color: '#F87171' }}>
          <LogOut size={16} /><span>Cerrar sesión</span>
        </button>
      </nav>
    </aside>
  );
};

export default Sidebar;
