import { useEffect, useState, useRef } from 'react';
import { User, Mail, BookOpen, GraduationCap, Camera, ShieldCheck } from 'lucide-react';
import * as usuarioApi from '../../api/usuarioApi';
import * as imagenApi from '../../api/imagenApi';
import type { UsuarioResponse, UsuarioRequest } from '../../types/usuario.types';
import { useAuth } from '../../hooks/useAuth';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';

const ROL_LABEL: Record<string, { label: string; color: string }> = {
  ROLE_ADMIN:        { label: 'Administrador', color: 'var(--danger)' },
  ROLE_EMPRENDEDOR:  { label: 'Emprendedor',   color: 'var(--primary)' },
  ROLE_COMPRADOR:    { label: 'Comprador',      color: 'var(--secondary)' },
};

const ProfilePage: React.FC = () => {
  const { idUsuario, rol, setFotoPerfil } = useAuth();
  const [usuario, setUsuario] = useState<UsuarioResponse | null>(null);
  const [loading, setLoading] = useState(!!idUsuario);
  const [error, setError] = useState<string | null>(null);
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [saveOk, setSaveOk] = useState(false);
  const [form, setForm] = useState<Partial<UsuarioRequest>>({});
  const [uploadingPhoto, setUploadingPhoto] = useState(false);
  const [photoPreview, setPhotoPreview] = useState<string | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (!idUsuario) return;
    usuarioApi
      .getById(idUsuario)
      .then((u) => {
        setUsuario(u);
        setFotoPerfil(u.fotoPerfil ?? null);
        setForm({
          nombreCompleto: u.nombreCompleto,
          programaAcademico: u.programaAcademico,
          semestreAcademico: u.semestreAcademico,
          fotoPerfil: u.fotoPerfil ?? '',
          correoInstitucional: u.correoInstitucional,
          clave: '',
        });
      })
      .catch(() => setError('No se pudo cargar tu perfil'))
      .finally(() => setLoading(false));
  }, [idUsuario, setFotoPerfil]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: name === 'semestreAcademico' ? Number(value) : value,
    }));
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!idUsuario || !form.correoInstitucional) return;
    setSaving(true);
    try {
      const updated = await usuarioApi.update(idUsuario, form as UsuarioRequest);
      setUsuario(updated);
      setEditing(false);
      setSaveOk(true);
      setTimeout(() => setSaveOk(false), 3000);
    } catch {
      setError('No se pudieron guardar los cambios');
    } finally {
      setSaving(false);
    }
  };

  const rolInfo = ROL_LABEL[rol ?? ''] ?? { label: rol?.replace('ROLE_', '') ?? '—', color: 'var(--text-muted)' };

  const handlePhotoFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file || !idUsuario) return;
    // Show preview immediately
    const reader = new FileReader();
    reader.onload = (ev) => setPhotoPreview(ev.target?.result as string);
    reader.readAsDataURL(file);
    // Upload
    setUploadingPhoto(true);
    try {
      const updated = await imagenApi.uploadFotoPerfil(idUsuario, file);
      setUsuario(updated);
      setPhotoPreview(null);
      setFotoPerfil(updated.fotoPerfil ?? null);
      setSaveOk(true);
      setTimeout(() => setSaveOk(false), 3000);
    } catch {
      setError('No se pudo subir la foto');
      setPhotoPreview(null);
    } finally {
      setUploadingPhoto(false);
    }
  };

  return (
    <DashboardLayout
      title="Mi Perfil"
      subtitle="Información de tu cuenta"
      action={
        !editing ? (
          <button className="btn btn-primary btn-sm" onClick={() => setEditing(true)}>
            ✏️ Editar perfil
          </button>
        ) : undefined
      }
    >
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {saveOk && (
        <div
          style={{
            background: 'var(--secondary-bg)',
            border: '1px solid #BBF7D0',
            color: '#15803D',
            padding: '12px 18px',
            borderRadius: 'var(--radius-md)',
            marginBottom: 20,
            fontSize: 13.5,
            fontWeight: 600,
          }}
        >
          ✅ Perfil actualizado correctamente
        </div>
      )}

      {!loading && !error && usuario && (
        <div style={{ display: 'grid', gridTemplateColumns: '280px 1fr', gap: 24, alignItems: 'start' }}>
          {/* Avatar card */}
          <div
            className="card"
            style={{ textAlign: 'center', padding: 32 }}
          >
            {/* Hidden file input */}
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              style={{ display: 'none' }}
              onChange={handlePhotoFileChange}
            />

            <div
              style={{ position: 'relative', display: 'inline-block', marginBottom: 20, cursor: 'pointer' }}
              onClick={() => fileInputRef.current?.click()}
              title="Cambiar foto de perfil"
            >
              {(photoPreview ?? usuario.fotoPerfil) ? (
                <img
                  src={photoPreview ?? usuario.fotoPerfil!}
                  alt={usuario.nombreCompleto}
                  style={{
                    width: 96,
                    height: 96,
                    borderRadius: '50%',
                    objectFit: 'cover',
                    border: '3px solid var(--border)',
                    opacity: uploadingPhoto ? 0.5 : 1,
                  }}
                />
              ) : (
                <div
                  style={{
                    width: 96,
                    height: 96,
                    borderRadius: '50%',
                    background: 'var(--primary)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    margin: '0 auto',
                  }}
                >
                  <User size={40} color="white" />
                </div>
              )}
              {/* Always show camera overlay on hover */}
              <div
                style={{
                  position: 'absolute',
                  bottom: 0,
                  right: 0,
                  width: 28,
                  height: 28,
                  background: uploadingPhoto ? '#9CA3AF' : 'var(--primary)',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  border: '2px solid white',
                }}
              >
                {uploadingPhoto ? (
                  <div style={{ width: 10, height: 10, border: '2px solid white', borderTopColor: 'transparent', borderRadius: '50%', animation: 'spin 0.6s linear infinite' }} />
                ) : (
                  <Camera size={13} color="white" />
                )}
              </div>
            </div>

            <h2 style={{ fontWeight: 800, fontSize: 17, marginBottom: 4 }}>
              {usuario.nombreCompleto}
            </h2>
            <p style={{ fontSize: 13, color: 'var(--text-muted)', marginBottom: 16 }}>
              {usuario.correoInstitucional}
            </p>

            <span
              style={{
                display: 'inline-flex',
                alignItems: 'center',
                gap: 6,
                background: `${rolInfo.color}15`,
                color: rolInfo.color,
                fontSize: 12,
                fontWeight: 700,
                padding: '5px 14px',
                borderRadius: 50,
                textTransform: 'uppercase',
                letterSpacing: '0.5px',
              }}
            >
              <ShieldCheck size={12} />
              {rolInfo.label}
            </span>

            <div
              style={{
                marginTop: 24,
                paddingTop: 20,
                borderTop: '1px solid var(--border)',
                display: 'flex',
                flexDirection: 'column',
                gap: 12,
                textAlign: 'left',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <GraduationCap size={15} color="var(--primary)" />
                <div>
                  <div style={{ fontSize: 10.5, fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                    Programa
                  </div>
                  <div style={{ fontSize: 13, fontWeight: 600 }}>{usuario.programaAcademico}</div>
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <BookOpen size={15} color="var(--primary)" />
                <div>
                  <div style={{ fontSize: 10.5, fontWeight: 600, color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>
                    Semestre
                  </div>
                  <div style={{ fontSize: 13, fontWeight: 600 }}>{usuario.semestreAcademico}°</div>
                </div>
              </div>
            </div>
          </div>

          {/* Edit form or info card */}
          <div className="card">
            {!editing ? (
              <>
                <h3 style={{ fontWeight: 700, fontSize: 15, marginBottom: 24 }}>
                  Datos personales
                </h3>
                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: '1fr 1fr',
                    gap: 20,
                  }}
                >
                  {[
                    { label: 'Nombre completo', value: usuario.nombreCompleto, icon: <User size={14} /> },
                    { label: 'Correo institucional', value: usuario.correoInstitucional, icon: <Mail size={14} /> },
                    { label: 'Programa académico', value: usuario.programaAcademico, icon: <GraduationCap size={14} /> },
                    { label: 'Semestre', value: `${usuario.semestreAcademico}°`, icon: <BookOpen size={14} /> },
                  ].map(({ label, value, icon }) => (
                    <div
                      key={label}
                      style={{
                        background: '#F9FAFB',
                        borderRadius: 'var(--radius-md)',
                        padding: '14px 16px',
                      }}
                    >
                      <div
                        style={{
                          display: 'flex',
                          alignItems: 'center',
                          gap: 6,
                          fontSize: 11,
                          fontWeight: 700,
                          textTransform: 'uppercase',
                          letterSpacing: '0.6px',
                          color: 'var(--text-muted)',
                          marginBottom: 6,
                        }}
                      >
                        {icon} {label}
                      </div>
                      <div style={{ fontWeight: 600, fontSize: 14 }}>{value}</div>
                    </div>
                  ))}
                </div>

                {usuario.fotoPerfil && (
                  <div style={{ marginTop: 20 }}>
                    <div style={{ fontSize: 11, fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.6px', color: 'var(--text-muted)', marginBottom: 8 }}>
                      URL foto de perfil
                    </div>
                    <div
                      style={{
                        fontSize: 13,
                        color: 'var(--primary)',
                        background: 'var(--primary-bg)',
                        padding: '8px 14px',
                        borderRadius: 'var(--radius-md)',
                        wordBreak: 'break-all',
                      }}
                    >
                      {usuario.fotoPerfil}
                    </div>
                  </div>
                )}
              </>
            ) : (
              <form onSubmit={handleSave}>
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    marginBottom: 24,
                  }}
                >
                  <h3 style={{ fontWeight: 700, fontSize: 15 }}>Editar datos</h3>
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={() => setEditing(false)}
                  >
                    Cancelar
                  </button>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
                  <div>
                    <label className="form-label">Nombre completo</label>
                    <input
                      className="form-input"
                      name="nombreCompleto"
                      value={form.nombreCompleto ?? ''}
                      onChange={handleChange}
                      required
                    />
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                    <div>
                      <label className="form-label">Programa académico</label>
                      <input
                        className="form-input"
                        name="programaAcademico"
                        value={form.programaAcademico ?? ''}
                        onChange={handleChange}
                        required
                      />
                    </div>
                    <div>
                      <label className="form-label">Semestre</label>
                      <input
                        className="form-input"
                        type="number"
                        name="semestreAcademico"
                        min={1}
                        max={10}
                        value={form.semestreAcademico ?? 1}
                        onChange={handleChange}
                        required
                      />
                    </div>
                  </div>
                  <div>
                    <label className="form-label">
                      <Camera size={12} style={{ display: 'inline', marginRight: 4 }} />
                      Foto de perfil
                    </label>
                    <p style={{ fontSize: 12.5, color: 'var(--text-muted)', marginBottom: 8 }}>
                      Haz clic en tu foto (en el panel izquierdo) para cambiarla desde tu dispositivo.
                    </p>
                  </div>
                  <div>
                    <label className="form-label">Nueva contraseña (dejar vacío para no cambiar)</label>
                    <input
                      className="form-input"
                      type="password"
                      name="clave"
                      value={form.clave ?? ''}
                      onChange={handleChange}
                      placeholder="••••••••"
                      minLength={4}
                    />
                  </div>

                  <button
                    type="submit"
                    className="btn btn-primary"
                    style={{ alignSelf: 'flex-start', opacity: saving ? 0.7 : 1 }}
                    disabled={saving}
                  >
                    {saving ? 'Guardando...' : '✓ Guardar cambios'}
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}

      {!loading && !error && !usuario && !idUsuario && (
        <div
          style={{
            textAlign: 'center',
            padding: '64px 0',
            color: 'var(--text-muted)',
            background: 'white',
            borderRadius: 'var(--radius-xl)',
            border: '1px solid var(--border)',
          }}
        >
          <User size={48} style={{ opacity: 0.2, marginBottom: 16, display: 'block', margin: '0 auto 16px' }} />
          <p style={{ fontWeight: 600 }}>
            Reinicia sesión para ver tu perfil
          </p>
          <p style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4 }}>
            Tu sesión no tiene información de usuario. Cierra sesión e inicia de nuevo.
          </p>
        </div>
      )}
    </DashboardLayout>
  );
};

export default ProfilePage;
