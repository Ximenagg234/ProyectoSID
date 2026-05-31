import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Store, ShoppingCart, Briefcase, UserCircle2, CheckCircle2 } from 'lucide-react';
import * as authApi from '../../api/authApi';
import type { UsuarioRequest } from '../../types/usuario.types';

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [rolSeleccionado, setRolSeleccionado] = useState<2 | 3>(3); // 2=EMPRENDEDOR, 3=COMPRADOR
  const [form, setForm] = useState<UsuarioRequest>({
    nombreCompleto: '',
    correoInstitucional: '',
    programaAcademico: '',
    semestreAcademico: 1,
    fotoPerfil: '',
    clave: '',
  });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: name === 'semestreAcademico' ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (
      !form.correoInstitucional.endsWith('@u.icesi.edu.co') &&
      !form.correoInstitucional.endsWith('@icesi.edu.co')
    ) {
      setError('El correo debe ser institucional (@u.icesi.edu.co o @icesi.edu.co)');
      return;
    }

    setLoading(true);
    try {
      await authApi.register(form, rolSeleccionado);
      navigate('/login?registered=1');
    } catch (err: unknown) {
      const status = (err as { response?: { status?: number } })?.response?.status;
      if (!status) {
        setError('No se pudo conectar con el servidor. ¿Está el backend corriendo?');
      } else {
        setError('No se pudo crear la cuenta. El correo ya puede estar en uso.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      {/* Left panel */}
      <div
        style={{
          width: '40%',
          background: '#0F172A',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          padding: '48px 52px',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: 14, marginBottom: 48 }}>
          <div
            style={{
              width: 48,
              height: 48,
              background: 'var(--primary)',
              borderRadius: 14,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <Store size={26} color="white" />
          </div>
          <div>
            <div style={{ color: 'white', fontWeight: 800, fontSize: 22, lineHeight: 1.2 }}>
              Emprende
            </div>
            <div style={{ color: '#818CF8', fontWeight: 600, fontSize: 14 }}>ICESI</div>
          </div>
        </div>

        <h1
          style={{ color: 'white', fontSize: 26, fontWeight: 800, marginBottom: 12, lineHeight: 1.3 }}
        >
          Únete a la comunidad emprendedora
        </h1>
        <p style={{ color: '#94A3B8', fontSize: 14, marginBottom: 40, lineHeight: 1.6 }}>
          Crea tu cuenta y forma parte del ecosistema de emprendimientos universitarios ICESI.
        </p>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          {[
            { icon: '🎓', title: 'Comunidad universitaria', desc: 'Solo correos institucionales ICESI' },
            { icon: '🛒', title: 'Compra y vende', desc: 'Marketplace exclusivo para la comunidad' },
            { icon: '📊', title: 'Panel de métricas', desc: 'Rastrea el rendimiento de tu negocio' },
          ].map(({ icon, title, desc }) => (
            <div
              key={title}
              style={{
                display: 'flex',
                alignItems: 'flex-start',
                gap: 14,
                background: 'rgba(255,255,255,.04)',
                borderRadius: 12,
                padding: '14px 16px',
              }}
            >
              <span style={{ fontSize: 20 }}>{icon}</span>
              <div>
                <div style={{ color: 'white', fontSize: 13, fontWeight: 700 }}>{title}</div>
                <div style={{ color: '#94A3B8', fontSize: 12, marginTop: 2 }}>{desc}</div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Right panel */}
      <div
        style={{
          flex: 1,
          background: 'white',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '40px 52px',
          overflowY: 'auto',
        }}
      >
        <div style={{ width: '100%', maxWidth: 460 }}>
          <div style={{ marginBottom: 28 }}>
            <span
              style={{
                background: 'var(--primary-bg)',
                color: 'var(--primary)',
                fontSize: 11,
                fontWeight: 700,
                padding: '4px 12px',
                borderRadius: 50,
                textTransform: 'uppercase',
                letterSpacing: '0.5px',
              }}
            >
              Registro
            </span>
            <h2
              style={{
                fontSize: 24,
                fontWeight: 800,
                color: 'var(--text)',
                marginTop: 16,
                marginBottom: 4,
              }}
            >
              Crear cuenta
            </h2>
            <p style={{ fontSize: 13.5, color: 'var(--text-muted)' }}>
              Completa el formulario para comenzar
            </p>
          </div>

          {/* Role selector */}
          <div style={{ marginBottom: 24 }}>
            <label className="form-label" style={{ marginBottom: 10 }}>
              ¿Cómo quieres participar?
            </label>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
              <button
                type="button"
                onClick={() => setRolSeleccionado(3)}
                style={{
                  padding: '14px 16px',
                  borderRadius: 'var(--radius-md)',
                  border: `2px solid ${rolSeleccionado === 3 ? 'var(--primary)' : 'var(--border)'}`,
                  background: rolSeleccionado === 3 ? 'var(--primary-bg)' : 'white',
                  cursor: 'pointer',
                  textAlign: 'left',
                  transition: 'all 0.15s',
                  position: 'relative',
                }}
              >
                {rolSeleccionado === 3 && (
                  <CheckCircle2
                    size={16}
                    color="var(--primary)"
                    style={{ position: 'absolute', top: 10, right: 10 }}
                  />
                )}
                <ShoppingCart
                  size={22}
                  color={rolSeleccionado === 3 ? 'var(--primary)' : 'var(--text-muted)'}
                  style={{ marginBottom: 8 }}
                />
                <div
                  style={{
                    fontWeight: 700,
                    fontSize: 13,
                    color: rolSeleccionado === 3 ? 'var(--primary)' : 'var(--text)',
                  }}
                >
                  Comprador
                </div>
                <div style={{ fontSize: 11.5, color: 'var(--text-muted)', marginTop: 2 }}>
                  Explora y compra
                </div>
              </button>

              <button
                type="button"
                onClick={() => setRolSeleccionado(2)}
                style={{
                  padding: '14px 16px',
                  borderRadius: 'var(--radius-md)',
                  border: `2px solid ${rolSeleccionado === 2 ? 'var(--primary)' : 'var(--border)'}`,
                  background: rolSeleccionado === 2 ? 'var(--primary-bg)' : 'white',
                  cursor: 'pointer',
                  textAlign: 'left',
                  transition: 'all 0.15s',
                  position: 'relative',
                }}
              >
                {rolSeleccionado === 2 && (
                  <CheckCircle2
                    size={16}
                    color="var(--primary)"
                    style={{ position: 'absolute', top: 10, right: 10 }}
                  />
                )}
                <Briefcase
                  size={22}
                  color={rolSeleccionado === 2 ? 'var(--primary)' : 'var(--text-muted)'}
                  style={{ marginBottom: 8 }}
                />
                <div
                  style={{
                    fontWeight: 700,
                    fontSize: 13,
                    color: rolSeleccionado === 2 ? 'var(--primary)' : 'var(--text)',
                  }}
                >
                  Emprendedor
                </div>
                <div style={{ fontSize: 11.5, color: 'var(--text-muted)', marginTop: 2 }}>
                  Crea y vende
                </div>
              </button>
            </div>
          </div>

          {error && (
            <div
              style={{
                background: 'var(--danger-bg)',
                border: '1px solid #FECACA',
                color: 'var(--danger)',
                padding: '12px 16px',
                borderRadius: 'var(--radius-md)',
                marginBottom: 20,
                fontSize: 13,
              }}
            >
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
            <div>
              <label className="form-label">Nombre completo *</label>
              <input
                className="form-input"
                name="nombreCompleto"
                value={form.nombreCompleto}
                onChange={handleChange}
                required
                placeholder="Tu nombre completo"
              />
            </div>
            <div>
              <label className="form-label">Correo institucional *</label>
              <input
                className="form-input"
                type="email"
                name="correoInstitucional"
                value={form.correoInstitucional}
                onChange={handleChange}
                required
                placeholder="usuario@u.icesi.edu.co"
              />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 14 }}>
              <div>
                <label className="form-label">Programa académico *</label>
                <input
                  className="form-input"
                  name="programaAcademico"
                  value={form.programaAcademico}
                  onChange={handleChange}
                  required
                  placeholder="Ej: Ingeniería de Sistemas"
                />
              </div>
              <div>
                <label className="form-label">Semestre *</label>
                <input
                  className="form-input"
                  type="number"
                  name="semestreAcademico"
                  min={1}
                  max={10}
                  value={form.semestreAcademico}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>
            <div>
              <label className="form-label">
                <UserCircle2 size={13} style={{ display: 'inline', marginRight: 4 }} />
                URL foto de perfil (opcional)
              </label>
              <input
                className="form-input"
                name="fotoPerfil"
                value={form.fotoPerfil}
                onChange={handleChange}
                placeholder="https://..."
              />
            </div>
            <div>
              <label className="form-label">Contraseña *</label>
              <input
                className="form-input"
                type="password"
                name="clave"
                value={form.clave}
                onChange={handleChange}
                required
                minLength={4}
                placeholder="Mínimo 4 caracteres"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn btn-primary"
              style={{
                width: '100%',
                justifyContent: 'center',
                padding: '13px 18px',
                fontSize: 14,
                marginTop: 6,
                opacity: loading ? 0.7 : 1,
              }}
            >
              {loading ? 'Creando cuenta...' : 'Crear cuenta'}
            </button>
          </form>

          <p
            style={{
              textAlign: 'center',
              fontSize: 13,
              color: 'var(--text-muted)',
              marginTop: 24,
            }}
          >
            ¿Ya tienes cuenta?{' '}
            <Link
              to="/login"
              style={{ color: 'var(--primary)', fontWeight: 600, textDecoration: 'none' }}
            >
              Inicia sesión
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
