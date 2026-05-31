import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Store, ShoppingBag, Briefcase, Package } from 'lucide-react';
import * as authApi from '../../api/authApi';
import { useAuth } from '../../hooks/useAuth';

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const { setCredentials } = useAuth();
  const [form, setForm] = useState({ correoInstitucional: '', clave: '' });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await authApi.login(form);
      const correo = res.correoInstitucional;
      setCredentials({ token: res.token, correo, roles: res.roles ?? [], idUsuario: res.idUsuario });

      navigate('/home');
    } catch (err: unknown) {
      const status = (err as { response?: { status?: number } })?.response?.status;
      if (status === 401) {
        setError('Credenciales incorrectas. Verifica tu correo y contraseña.');
      } else if (!status) {
        setError('No se pudo conectar con el servidor. ¿Está el backend corriendo?');
      } else {
        setError('Error al iniciar sesión. Intenta de nuevo.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      {/* Left panel */}
      <div style={{ width: '45%', background: '#0F172A', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '48px 56px' }}>
        {/* Logo */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 14, marginBottom: 48 }}>
          <div style={{ width: 48, height: 48, background: 'var(--primary)', borderRadius: 14, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Store size={26} color="white" />
          </div>
          <div>
            <div style={{ color: 'white', fontWeight: 800, fontSize: 22, lineHeight: 1.2 }}>Emprende</div>
            <div style={{ color: '#818CF8', fontWeight: 600, fontSize: 14 }}>ICESI</div>
          </div>
        </div>

        <h1 style={{ color: 'white', fontSize: 30, fontWeight: 800, marginBottom: 12, lineHeight: 1.3 }}>
          La plataforma de emprendimientos universitarios
        </h1>
        <p style={{ color: '#94A3B8', fontSize: 14, marginBottom: 48, lineHeight: 1.6 }}>
          Conectamos estudiantes emprendedores con compradores en la comunidad ICESI.
        </p>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          {[
            { icon: <ShoppingBag size={18} color="#818CF8" />, text: 'Marketplace estudiantil' },
            { icon: <Briefcase size={18} color="#818CF8" />, text: 'Gestión de emprendimientos' },
            { icon: <Package size={18} color="#818CF8" />, text: 'Seguimiento de pedidos' },
          ].map(({ icon, text }) => (
            <div key={text} style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
              <div style={{ width: 38, height: 38, background: 'rgba(99,102,241,.15)', borderRadius: 10, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                {icon}
              </div>
              <span style={{ color: '#CBD5E1', fontSize: 14, fontWeight: 500 }}>{text}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Right panel */}
      <div style={{ flex: 1, background: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '48px 56px' }}>
        <div style={{ width: '100%', maxWidth: 400 }}>
          <div style={{ marginBottom: 32 }}>
            <span style={{ background: 'var(--primary-bg)', color: 'var(--primary)', fontSize: 11, fontWeight: 700, padding: '4px 12px', borderRadius: 50, textTransform: 'uppercase', letterSpacing: '0.5px' }}>
              Portal Estudiantil
            </span>
            <h2 style={{ fontSize: 26, fontWeight: 800, color: 'var(--text)', marginTop: 16, marginBottom: 4 }}>Bienvenido de nuevo</h2>
            <p style={{ fontSize: 13.5, color: 'var(--text-muted)' }}>Ingresa tus credenciales para continuar</p>
          </div>

          {error && (
            <div style={{ background: 'var(--danger-bg)', border: '1px solid #FECACA', color: 'var(--danger)', padding: '12px 16px', borderRadius: 'var(--radius-md)', marginBottom: 20, fontSize: 13 }}>
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
            <div>
              <label className="form-label">Correo institucional</label>
              <input
                className="form-input"
                type="email"
                name="correoInstitucional"
                value={form.correoInstitucional}
                onChange={handleChange}
                required
                placeholder="usuario@icesi.edu.co"
              />
            </div>
            <div>
              <label className="form-label">Contraseña</label>
              <input
                className="form-input"
                type="password"
                name="clave"
                value={form.clave}
                onChange={handleChange}
                required
                placeholder="••••••••"
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="btn btn-primary"
              style={{ width: '100%', justifyContent: 'center', padding: '13px 18px', fontSize: 14, marginTop: 4, opacity: loading ? 0.7 : 1 }}
            >
              {loading ? 'Iniciando sesión...' : 'Iniciar sesión'}
            </button>
          </form>

          <p style={{ textAlign: 'center', fontSize: 13, color: 'var(--text-muted)', marginTop: 24 }}>
            ¿No tienes cuenta?{' '}
            <Link to="/registro" style={{ color: 'var(--primary)', fontWeight: 600, textDecoration: 'none' }}>
              Regístrate aquí
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
