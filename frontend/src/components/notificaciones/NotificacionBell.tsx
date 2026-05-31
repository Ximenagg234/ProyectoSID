import { useState, useRef, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { Bell } from 'lucide-react';
import type { RootState, AppDispatch } from '../../store';
import { marcarTodasLeidas } from '../../store/notificacionesSlice';
import { useWebSocket } from '../../hooks/useWebSocket';

const NotificacionBell: React.FC = () => {
  const dispatch = useDispatch<AppDispatch>();
  const { lista, noLeidas } = useSelector((s: RootState) => s.notificaciones);
  const [open, setOpen] = useState(false);
  const btnRef = useRef<HTMLButtonElement>(null);
  const [pos, setPos] = useState({ top: 0, left: 0 });
  useWebSocket();

  // Calculate dropdown position relative to viewport when opening
  const handleToggle = () => {
    if (!open && btnRef.current) {
      const rect = btnRef.current.getBoundingClientRect();
      setPos({
        top: rect.bottom + 8,
        left: 260 + 16, // right of sidebar + padding
      });
    }
    setOpen((o) => !o);
  };

  // Close on outside click
  useEffect(() => {
    if (!open) return;
    const handler = (e: MouseEvent) => {
      if (btnRef.current && !btnRef.current.contains(e.target as Node)) {
        const dropdown = document.getElementById('notif-dropdown');
        if (dropdown && !dropdown.contains(e.target as Node)) {
          setOpen(false);
        }
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [open]);

  return (
    <>
      <button
        ref={btnRef}
        onClick={handleToggle}
        style={{
          position: 'relative',
          background: 'rgba(255,255,255,.08)',
          border: 'none',
          borderRadius: '50%',
          width: 32,
          height: 32,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          cursor: 'pointer',
          flexShrink: 0,
        }}
        aria-label="Notificaciones"
      >
        <Bell size={15} color="#CBD5E1" />
        {noLeidas > 0 && (
          <span
            style={{
              position: 'absolute',
              top: -3,
              right: -3,
              background: 'var(--danger)',
              color: 'white',
              fontSize: 9,
              fontWeight: 800,
              borderRadius: '50%',
              width: 16,
              height: 16,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              border: '1.5px solid var(--sidebar-bg)',
            }}
          >
            {noLeidas > 9 ? '9+' : noLeidas}
          </span>
        )}
      </button>

      {open && (
        <div
          id="notif-dropdown"
          style={{
            position: 'fixed',
            top: pos.top,
            left: pos.left,
            width: 320,
            background: 'white',
            borderRadius: 'var(--radius-lg)',
            boxShadow: 'var(--shadow-lg)',
            border: '1px solid var(--border)',
            zIndex: 9999,
            overflow: 'hidden',
          }}
        >
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '14px 16px',
              borderBottom: '1px solid var(--border)',
            }}
          >
            <span style={{ fontWeight: 700, fontSize: 14, color: 'var(--text)' }}>
              Notificaciones
            </span>
            <button
              onClick={() => dispatch(marcarTodasLeidas())}
              style={{
                background: 'none',
                border: 'none',
                cursor: 'pointer',
                fontSize: 12,
                color: 'var(--primary)',
                fontWeight: 600,
              }}
            >
              Marcar todas como leídas
            </button>
          </div>
          <div style={{ maxHeight: 280, overflowY: 'auto' }}>
            {lista.length === 0 ? (
              <p
                style={{
                  textAlign: 'center',
                  color: 'var(--text-muted)',
                  padding: '28px 16px',
                  fontSize: 13,
                }}
              >
                Sin notificaciones
              </p>
            ) : (
              lista.map((n) => (
                <div
                  key={n.id}
                  style={{
                    padding: '12px 16px',
                    borderBottom: '1px solid var(--border)',
                    background: n.leida ? 'transparent' : 'var(--primary-bg)',
                  }}
                >
                  <p style={{ fontSize: 13, color: n.leida ? 'var(--text-muted)' : 'var(--text)', fontWeight: n.leida ? 400 : 600 }}>
                    {n.mensaje}
                  </p>
                  <p style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 3 }}>
                    {new Date(n.timestamp).toLocaleTimeString()}
                  </p>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </>
  );
};

export default NotificacionBell;
