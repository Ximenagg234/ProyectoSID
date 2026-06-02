import { useEffect, useState, useMemo } from 'react';
import * as emprendimientoApi from '../../api/emprendimientoApi';
import type { EmprendimientoResponse } from '../../types/emprendimiento.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import { Star, TrendingUp, Building2, CheckCircle2, XCircle, Filter, X } from 'lucide-react';

const ESTADO_ACTIVO   = 1;
const ESTADO_INACTIVO = 2;

const AdminEmprendimientosPage: React.FC = () => {
  const [emprendimientos, setEmprendimientos] = useState<EmprendimientoResponse[]>([]);
  const [loading, setLoading]                 = useState(true);
  const [error, setError]                     = useState<string | null>(null);
  const [trabajando, setTrabajando]           = useState<number | null>(null);

  // Filters
  const [filtroEstado,    setFiltroEstado]    = useState<string>('');
  const [filtroCategoria, setFiltroCategoria] = useState<string>('');
  const [filtroSemestre,  setFiltroSemestre]  = useState<string>('');
  const [filtroBusqueda,  setFiltroBusqueda]  = useState<string>('');
  const [ordenarPor,      setOrdenarPor]      = useState<'nombre' | 'categoria' | 'semestre' | 'estado'>('nombre');

  const cargar = () => {
    setLoading(true);
    emprendimientoApi.getAll()
      .then(setEmprendimientos)
      .catch(() => setError('Error al cargar emprendimientos'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { cargar(); }, []);

  // Derived filter options
  const categorias = useMemo(() =>
    [...new Set(emprendimientos.map((e) => e.nombreCategoria))].sort(), [emprendimientos]);
  const semestres = useMemo(() =>
    [...new Set(emprendimientos.map((e) => e.nombreSemestre))].sort(), [emprendimientos]);

  // Filtered & sorted list
  const filtrados = useMemo(() => {
    let list = [...emprendimientos];
    if (filtroEstado)    list = list.filter((e) => e.nombreEstado    === filtroEstado);
    if (filtroCategoria) list = list.filter((e) => e.nombreCategoria === filtroCategoria);
    if (filtroSemestre)  list = list.filter((e) => e.nombreSemestre  === filtroSemestre);
    if (filtroBusqueda)  list = list.filter((e) =>
      e.nombre.toLowerCase().includes(filtroBusqueda.toLowerCase()) ||
      e.nombreUsuario?.toLowerCase().includes(filtroBusqueda.toLowerCase()));
    list.sort((a, b) => {
      if (ordenarPor === 'nombre')    return a.nombre.localeCompare(b.nombre);
      if (ordenarPor === 'categoria') return a.nombreCategoria.localeCompare(b.nombreCategoria);
      if (ordenarPor === 'semestre')  return a.nombreSemestre.localeCompare(b.nombreSemestre);
      if (ordenarPor === 'estado')    return a.nombreEstado.localeCompare(b.nombreEstado);
      return 0;
    });
    return list;
  }, [emprendimientos, filtroEstado, filtroCategoria, filtroSemestre, filtroBusqueda, ordenarPor]);

  // Stats by semestre
  const statsPorSemestre = useMemo(() => {
    const map: Record<string, { activos: number; inactivos: number }> = {};
    emprendimientos.forEach((e) => {
      if (!map[e.nombreSemestre]) map[e.nombreSemestre] = { activos: 0, inactivos: 0 };
      if (e.nombreEstado === 'ACTIVO') map[e.nombreSemestre].activos++;
      else                             map[e.nombreSemestre].inactivos++;
    });
    return Object.entries(map).sort(([a], [b]) => a.localeCompare(b));
  }, [emprendimientos]);

  const hayFiltros = filtroEstado || filtroCategoria || filtroSemestre || filtroBusqueda;

  const limpiarFiltros = () => {
    setFiltroEstado('');
    setFiltroCategoria('');
    setFiltroSemestre('');
    setFiltroBusqueda('');
  };

  const handleCambiarEstado = async (emp: EmprendimientoResponse) => {
    setTrabajando(emp.idEmprendimiento);
    try {
      const nuevoId = emp.nombreEstado === 'ACTIVO' ? ESTADO_INACTIVO : ESTADO_ACTIVO;
      const updated = await emprendimientoApi.cambiarEstado(emp.idEmprendimiento, nuevoId);
      setEmprendimientos((prev) => prev.map((e) => e.idEmprendimiento === emp.idEmprendimiento ? updated : e));
    } catch (e: unknown) {
      const status = (e as { response?: { status?: number; data?: { error?: string } } })?.response?.status;
      const msg    = (e as { response?: { data?: { error?: string } } })?.response?.data?.error;
      setError(`Error ${status ?? ''}: ${msg ?? 'No se pudo cambiar el estado'}`);
    } finally {
      setTrabajando(null);
    }
  };

  const handleToggleDestacado = async (emp: EmprendimientoResponse) => {
    setTrabajando(emp.idEmprendimiento);
    try {
      const updated = await emprendimientoApi.toggleDestacado(emp.idEmprendimiento, !emp.destacado);
      setEmprendimientos((prev) => prev.map((e) => e.idEmprendimiento === emp.idEmprendimiento ? updated : e));
    } catch (e: unknown) {
      const status = (e as { response?: { status?: number; data?: { error?: string } } })?.response?.status;
      const msg    = (e as { response?: { data?: { error?: string } } })?.response?.data?.error;
      setError(`Error ${status ?? ''}: ${msg ?? 'No se pudo actualizar el destacado'}`);
    } finally {
      setTrabajando(null);
    }
  };

  const selectStyle: React.CSSProperties = {
    padding: '7px 10px', border: '1.5px solid var(--border)', borderRadius: 'var(--radius-md)',
    fontSize: 13, color: 'var(--text)', background: 'white', outline: 'none', cursor: 'pointer',
  };

  return (
    <DashboardLayout
      title="Emprendimientos"
      subtitle="Gestión y ranking de emprendimientos por semestre"
    >
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {!loading && !error && (
        <>
          {/* ── Stats por semestre ─────────────────────────────── */}
          <div style={{ display: 'flex', gap: 12, marginBottom: 24, flexWrap: 'wrap' }}>
            <div className="card" style={{ padding: '14px 20px', display: 'flex', alignItems: 'center', gap: 12, minWidth: 160 }}>
              <div style={{ color: 'var(--primary)' }}><Building2 size={20} /></div>
              <div>
                <div style={{ fontSize: 22, fontWeight: 800 }}>{emprendimientos.length}</div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>Total</div>
              </div>
            </div>
            <div className="card" style={{ padding: '14px 20px', display: 'flex', alignItems: 'center', gap: 12, minWidth: 160 }}>
              <div style={{ color: '#22C55E' }}><CheckCircle2 size={20} /></div>
              <div>
                <div style={{ fontSize: 22, fontWeight: 800 }}>{emprendimientos.filter((e) => e.nombreEstado === 'ACTIVO').length}</div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>Activos</div>
              </div>
            </div>
            <div className="card" style={{ padding: '14px 20px', display: 'flex', alignItems: 'center', gap: 12, minWidth: 160 }}>
              <div style={{ color: '#F59E0B' }}><Star size={20} /></div>
              <div>
                <div style={{ fontSize: 22, fontWeight: 800 }}>{emprendimientos.filter((e) => e.destacado).length}</div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>Destacados</div>
              </div>
            </div>
            {statsPorSemestre.map(([sem, stats]) => (
              <div key={sem} className="card" style={{ padding: '14px 20px', minWidth: 180 }}>
                <div style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 6, fontWeight: 600, textTransform: 'uppercase' }}>{sem}</div>
                <div style={{ display: 'flex', gap: 16 }}>
                  <div><span style={{ fontSize: 18, fontWeight: 800, color: '#22C55E' }}>{stats.activos}</span> <span style={{ fontSize: 11, color: 'var(--text-muted)' }}>activos</span></div>
                  <div><span style={{ fontSize: 18, fontWeight: 800, color: '#94A3B8' }}>{stats.inactivos}</span> <span style={{ fontSize: 11, color: 'var(--text-muted)' }}>inactivos</span></div>
                </div>
              </div>
            ))}
          </div>

          {/* ── Filters ────────────────────────────────────────── */}
          <div className="card" style={{ marginBottom: 20, padding: '14px 20px' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: 'var(--text-muted)', fontSize: 13 }}>
                <Filter size={14} /> Filtros:
              </div>

              <input
                type="text"
                placeholder="Buscar nombre o propietario..."
                value={filtroBusqueda}
                onChange={(e) => setFiltroBusqueda(e.target.value)}
                style={{ ...selectStyle, minWidth: 200 }}
              />

              <select value={filtroEstado} onChange={(e) => setFiltroEstado(e.target.value)} style={selectStyle}>
                <option value="">Todos los estados</option>
                <option value="ACTIVO">Activo</option>
                <option value="INACTIVO">Inactivo</option>
              </select>

              <select value={filtroCategoria} onChange={(e) => setFiltroCategoria(e.target.value)} style={selectStyle}>
                <option value="">Todas las categorías</option>
                {categorias.map((c) => <option key={c} value={c}>{c}</option>)}
              </select>

              <select value={filtroSemestre} onChange={(e) => setFiltroSemestre(e.target.value)} style={selectStyle}>
                <option value="">Todos los semestres</option>
                {semestres.map((s) => <option key={s} value={s}>{s}</option>)}
              </select>

              <select value={ordenarPor} onChange={(e) => setOrdenarPor(e.target.value as typeof ordenarPor)} style={selectStyle}>
                <option value="nombre">Ordenar: Nombre</option>
                <option value="categoria">Ordenar: Categoría</option>
                <option value="semestre">Ordenar: Semestre</option>
                <option value="estado">Ordenar: Estado</option>
              </select>

              {hayFiltros && (
                <button onClick={limpiarFiltros} style={{ display: 'flex', alignItems: 'center', gap: 4, padding: '6px 12px', border: '1px solid #FECACA', borderRadius: 'var(--radius-md)', background: 'white', color: 'var(--danger)', fontSize: 12, fontWeight: 600, cursor: 'pointer' }}>
                  <X size={12} /> Limpiar
                </button>
              )}

              <span style={{ marginLeft: 'auto', fontSize: 12, color: 'var(--text-muted)' }}>
                {filtrados.length} de {emprendimientos.length}
              </span>
            </div>
          </div>

          {/* ── Table ──────────────────────────────────────────── */}
          <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
            <table className="table-custom">
              <thead>
                <tr>
                  <th>Emprendimiento</th>
                  <th>Categoría</th>
                  <th>Propietario</th>
                  <th>Semestre</th>
                  <th>Estado</th>
                  <th>Destacado</th>
                  <th style={{ textAlign: 'right' }}>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filtrados.map((e) => (
                  <tr key={e.idEmprendimiento}>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                        {e.logoUrl
                          ? <img src={e.logoUrl} alt={e.nombre} style={{ width: 34, height: 34, borderRadius: 'var(--radius-sm)', objectFit: 'cover', flexShrink: 0 }} />
                          : <div style={{ width: 34, height: 34, borderRadius: 'var(--radius-sm)', background: 'var(--primary-bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}><Building2 size={16} color="var(--primary)" /></div>
                        }
                        <span style={{ fontWeight: 600 }}>{e.nombre}</span>
                      </div>
                    </td>
                    <td><span className="badge badge-primary">{e.nombreCategoria}</span></td>
                    <td style={{ color: 'var(--text-muted)', fontSize: 13 }}>{e.nombreUsuario}</td>
                    <td style={{ color: 'var(--text-muted)', fontSize: 13 }}>{e.nombreSemestre}</td>
                    <td>
                      <span className={`badge badge-${e.nombreEstado}`}>{e.nombreEstado}</span>
                    </td>
                    <td>
                      <button
                        onClick={() => handleToggleDestacado(e)}
                        disabled={trabajando === e.idEmprendimiento}
                        title={e.destacado ? 'Quitar destacado' : 'Marcar como destacado'}
                        style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 4 }}
                      >
                        <Star
                          size={18}
                          fill={e.destacado ? '#F59E0B' : 'none'}
                          color={e.destacado ? '#F59E0B' : '#CBD5E1'}
                        />
                      </button>
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <button
                        onClick={() => handleCambiarEstado(e)}
                        disabled={trabajando === e.idEmprendimiento}
                        className="btn btn-sm"
                        style={{
                          display: 'inline-flex', alignItems: 'center', gap: 5, fontSize: 12,
                          border: '1px solid',
                          borderColor: e.nombreEstado === 'ACTIVO' ? '#FECACA' : '#BBF7D0',
                          color: e.nombreEstado === 'ACTIVO' ? '#DC2626' : '#16A34A',
                          background: 'white', borderRadius: 'var(--radius-md)', padding: '4px 10px', cursor: 'pointer',
                          opacity: trabajando === e.idEmprendimiento ? 0.5 : 1,
                        }}
                      >
                        {e.nombreEstado === 'ACTIVO'
                          ? <><XCircle size={13} /> Desactivar</>
                          : <><CheckCircle2 size={13} /> Activar</>}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {filtrados.length === 0 && (
              <p style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-muted)', fontSize: 13 }}>
                {hayFiltros ? 'Ningún emprendimiento coincide con los filtros' : 'No hay emprendimientos'}
              </p>
            )}
          </div>

          {/* ── Ranking por semestre ───────────────────────────── */}
          {statsPorSemestre.length > 0 && (
            <div style={{ marginTop: 28 }}>
              <h2 style={{ fontWeight: 700, fontSize: 15, marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
                <TrendingUp size={16} color="var(--primary)" />
                Distribución por semestre
              </h2>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: 14 }}>
                {statsPorSemestre.map(([sem, stats]) => {
                  const total = stats.activos + stats.inactivos;
                  const pct   = total > 0 ? Math.round((stats.activos / total) * 100) : 0;
                  return (
                    <div key={sem} className="card" style={{ padding: '16px 20px' }}>
                      <div style={{ fontWeight: 700, fontSize: 13, marginBottom: 10 }}>{sem}</div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12, color: 'var(--text-muted)', marginBottom: 8 }}>
                        <span>{total} emprendimientos</span>
                        <span style={{ color: '#22C55E', fontWeight: 700 }}>{pct}% activos</span>
                      </div>
                      <div style={{ height: 8, background: '#F3F4F6', borderRadius: 99, overflow: 'hidden' }}>
                        <div style={{ height: '100%', width: `${pct}%`, background: '#22C55E', borderRadius: 99, transition: 'width 0.5s' }} />
                      </div>
                      <div style={{ display: 'flex', gap: 16, marginTop: 10 }}>
                        <span style={{ fontSize: 12 }}><strong style={{ color: '#22C55E' }}>{stats.activos}</strong> activos</span>
                        <span style={{ fontSize: 12 }}><strong style={{ color: '#94A3B8' }}>{stats.inactivos}</strong> inactivos</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </>
      )}
    </DashboardLayout>
  );
};

export default AdminEmprendimientosPage;
