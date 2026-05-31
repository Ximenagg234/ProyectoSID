import { useEffect, useState } from 'react';
import * as usuarioApi from '../../api/usuarioApi';
import * as rolApi from '../../api/rolApi';
import type { UsuarioResponse } from '../../types/usuario.types';
import type { RolResponse } from '../../api/rolApi';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { Plus, Trash2, User, Shield } from 'lucide-react';

const ROLE_COLORS: Record<string, { bg: string; color: string; border: string }> = {
  ADMIN:        { bg: '#FEF3C7', color: '#92400E', border: '#FDE68A' },
  EMPRENDEDOR:  { bg: '#EDE9FE', color: '#5B21B6', border: '#C4B5FD' },
  COMPRADOR:    { bg: '#D1FAE5', color: '#065F46', border: '#6EE7B7' },
};

const rolColor = (nombre: string) => {
  const key = nombre.replace('ROLE_', '');
  return ROLE_COLORS[key] ?? { bg: '#F3F4F6', color: '#374151', border: '#E5E7EB' };
};

const RolBadge: React.FC<{
  nombre: string;
  onRemove?: () => void;
}> = ({ nombre, onRemove }) => {
  const display = nombre.replace('ROLE_', '');
  const { bg, color, border } = rolColor(nombre);
  return (
    <span
      style={{
        display: 'inline-flex', alignItems: 'center', gap: 4,
        padding: '3px 8px',
        background: bg, color, border: `1px solid ${border}`,
        borderRadius: 999, fontSize: 12, fontWeight: 600,
        whiteSpace: 'nowrap',
      }}
    >
      {display}
      {onRemove && (
        <button
          onClick={(e) => { e.stopPropagation(); onRemove(); }}
          title={`Quitar rol ${display}`}
          style={{
            background: 'none', border: 'none', cursor: 'pointer',
            color, padding: 0, lineHeight: 1, fontSize: 14, fontWeight: 700,
            opacity: 0.7,
          }}
        >
          ×
        </button>
      )}
    </span>
  );
};

const AdminUsuariosPage: React.FC = () => {
  const [usuarios, setUsuarios] = useState<UsuarioResponse[]>([]);
  const [todosRoles, setTodosRoles] = useState<RolResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [confirmId, setConfirmId] = useState<number | null>(null);

  // Role-add dropdown state: which user row is open
  const [addRolOpen, setAddRolOpen] = useState<number | null>(null);
  const [roleWorking, setRoleWorking] = useState(false);

  useEffect(() => {
    Promise.all([usuarioApi.getAll(), rolApi.getRoles()])
      .then(([users, roles]) => {
        setUsuarios(users);
        setTodosRoles(roles);
      })
      .catch(() => setError('Error al cargar usuarios'))
      .finally(() => setLoading(false));
  }, []);

  const handleDelete = async (id: number) => {
    try {
      await usuarioApi.remove(id);
      setUsuarios((prev) => prev.filter((u) => u.idUsuario !== id));
    } catch {
      setError('No se pudo eliminar el usuario');
    } finally {
      setConfirmId(null);
    }
  };

  const handleAsignarRol = async (idUsuario: number, rol: RolResponse) => {
    setRoleWorking(true);
    try {
      await rolApi.asignarRol(idUsuario, rol.idRol);
      // Refresh user from server to get updated roles
      const updated = await usuarioApi.getById(idUsuario);
      setUsuarios((prev) => prev.map((u) => (u.idUsuario === idUsuario ? updated : u)));
    } catch {
      setError('No se pudo asignar el rol');
    } finally {
      setRoleWorking(false);
      setAddRolOpen(null);
    }
  };

  const handleQuitarRol = async (idUsuario: number, rolNombre: string) => {
    const rol = todosRoles.find((r) => r.nombre === rolNombre || `ROLE_${r.nombre}` === rolNombre);
    if (!rol) return;
    setRoleWorking(true);
    try {
      await rolApi.quitarRol(idUsuario, rol.idRol);
      const updated = await usuarioApi.getById(idUsuario);
      setUsuarios((prev) => prev.map((u) => (u.idUsuario === idUsuario ? updated : u)));
    } catch {
      setError('No se pudo quitar el rol');
    } finally {
      setRoleWorking(false);
    }
  };

  // Roles that a user doesn't already have
  const rolesDisponibles = (u: UsuarioResponse) =>
    todosRoles.filter(
      (r) => !u.roles?.some((ur) => ur === r.nombre || ur === `ROLE_${r.nombre}`)
    );

  return (
    <DashboardLayout title="Gestión de Usuarios" subtitle="Administra usuarios, roles y permisos">
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {/* Stats strip */}
      {!loading && !error && (
        <div style={{ display: 'flex', gap: 14, marginBottom: 24, flexWrap: 'wrap' }}>
          {[
            { label: 'Total usuarios', value: usuarios.length, icon: <User size={16} /> },
            { label: 'Admins', value: usuarios.filter((u) => u.roles?.some((r) => r.includes('ADMIN'))).length, icon: <Shield size={16} /> },
            { label: 'Emprendedores', value: usuarios.filter((u) => u.roles?.some((r) => r.includes('EMPRENDEDOR'))).length, icon: <Shield size={16} /> },
            { label: 'Compradores', value: usuarios.filter((u) => u.roles?.some((r) => r.includes('COMPRADOR'))).length, icon: <Shield size={16} /> },
          ].map((s) => (
            <div
              key={s.label}
              className="card"
              style={{ padding: '12px 20px', display: 'flex', alignItems: 'center', gap: 12, minWidth: 140 }}
            >
              <div style={{ color: 'var(--primary)' }}>{s.icon}</div>
              <div>
                <div style={{ fontSize: 22, fontWeight: 800 }}>{s.value}</div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{s.label}</div>
              </div>
            </div>
          ))}
        </div>
      )}

      <div className="card" style={{ padding: 0, overflow: 'visible' }}>
        <table className="table-custom">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nombre</th>
              <th>Correo</th>
              <th>Programa</th>
              <th>Roles</th>
              <th style={{ textAlign: 'right' }}>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {usuarios.map((u) => {
              const disponibles = rolesDisponibles(u);
              return (
                <tr key={u.idUsuario}>
                  <td style={{ color: 'var(--text-muted)', fontSize: 12 }}>{u.idUsuario}</td>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      {u.fotoPerfil ? (
                        <img
                          src={u.fotoPerfil}
                          alt={u.nombreCompleto}
                          style={{ width: 30, height: 30, borderRadius: '50%', objectFit: 'cover', border: '1px solid var(--border)', flexShrink: 0 }}
                        />
                      ) : (
                        <div
                          style={{
                            width: 30, height: 30, borderRadius: '50%',
                            background: 'var(--primary-bg)',
                            display: 'flex', alignItems: 'center', justifyContent: 'center',
                            flexShrink: 0,
                          }}
                        >
                          <User size={14} color="var(--primary)" />
                        </div>
                      )}
                      <span style={{ fontWeight: 600 }}>{u.nombreCompleto}</span>
                    </div>
                  </td>
                  <td style={{ color: 'var(--text-muted)', fontSize: 13 }}>{u.correoInstitucional}</td>
                  <td style={{ color: 'var(--text-muted)', fontSize: 13 }}>{u.programaAcademico}</td>
                  <td>
                    <div style={{ display: 'flex', gap: 4, flexWrap: 'wrap', alignItems: 'center' }}>
                      {u.roles?.map((r) => (
                        <RolBadge
                          key={r}
                          nombre={r}
                          onRemove={roleWorking ? undefined : () => handleQuitarRol(u.idUsuario, r)}
                        />
                      ))}

                      {/* Add role dropdown */}
                      {disponibles.length > 0 && (
                        <div style={{ position: 'relative' }}>
                          <button
                            onClick={() => setAddRolOpen(addRolOpen === u.idUsuario ? null : u.idUsuario)}
                            disabled={roleWorking}
                            title="Agregar rol"
                            style={{
                              width: 22, height: 22,
                              borderRadius: '50%',
                              border: '1.5px dashed var(--border)',
                              background: 'none',
                              cursor: 'pointer',
                              display: 'flex', alignItems: 'center', justifyContent: 'center',
                              color: 'var(--text-muted)',
                              padding: 0,
                            }}
                          >
                            <Plus size={13} />
                          </button>

                          {addRolOpen === u.idUsuario && (
                            <div
                              style={{
                                position: 'absolute', top: 28, left: 0, zIndex: 100,
                                background: 'white',
                                border: '1px solid var(--border)',
                                borderRadius: 'var(--radius-md)',
                                boxShadow: '0 8px 24px rgba(0,0,0,0.12)',
                                minWidth: 140,
                                overflow: 'hidden',
                              }}
                            >
                              {disponibles.map((rol) => (
                                <button
                                  key={rol.idRol}
                                  onClick={() => handleAsignarRol(u.idUsuario, rol)}
                                  style={{
                                    display: 'block', width: '100%', textAlign: 'left',
                                    padding: '9px 14px',
                                    background: 'none', border: 'none', cursor: 'pointer',
                                    fontSize: 13, fontWeight: 600,
                                    color: rolColor(`ROLE_${rol.nombre}`).color,
                                  }}
                                  onMouseEnter={(e) => {
                                    (e.currentTarget as HTMLButtonElement).style.background = '#F9FAFB';
                                  }}
                                  onMouseLeave={(e) => {
                                    (e.currentTarget as HTMLButtonElement).style.background = 'none';
                                  }}
                                >
                                  + {rol.nombre}
                                </button>
                              ))}
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <button
                      onClick={() => setConfirmId(u.idUsuario)}
                      className="btn btn-sm"
                      style={{
                        background: 'none', border: '1px solid #FECACA', color: '#DC2626',
                        borderRadius: 'var(--radius-md)', padding: '4px 10px',
                        display: 'inline-flex', alignItems: 'center', gap: 5, fontSize: 12,
                        cursor: 'pointer',
                      }}
                    >
                      <Trash2 size={13} />
                      Eliminar
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
        {!loading && usuarios.length === 0 && (
          <p style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-muted)', fontSize: 13 }}>
            No hay usuarios
          </p>
        )}
      </div>

      {/* Close dropdown when clicking outside */}
      {addRolOpen !== null && (
        <div
          style={{ position: 'fixed', inset: 0, zIndex: 99 }}
          onClick={() => setAddRolOpen(null)}
        />
      )}

      {confirmId !== null && (
        <ConfirmDialog
          mensaje="¿Eliminar este usuario? Esta acción no se puede deshacer."
          onConfirm={() => handleDelete(confirmId)}
          onCancel={() => setConfirmId(null)}
        />
      )}
    </DashboardLayout>
  );
};

export default AdminUsuariosPage;
