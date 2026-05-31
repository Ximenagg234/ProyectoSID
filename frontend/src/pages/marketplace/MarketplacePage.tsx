import { useEffect, useState, useMemo } from 'react';
import * as emprendimientoApi from '../../api/emprendimientoApi';
import * as productoApi from '../../api/productoApi';
import type { EmprendimientoResponse } from '../../types/emprendimiento.types';
import type { ProductoResponse } from '../../types/producto.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';
import ProductoCard from '../../components/productos/ProductoCard';
import { Search, SlidersHorizontal, Package } from 'lucide-react';

const MarketplacePage: React.FC = () => {
  const [productos, setProductos] = useState<ProductoResponse[]>([]);
  const [emprendimientos, setEmprendimientos] = useState<EmprendimientoResponse[]>([]);
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busqueda, setBusqueda] = useState('');

  useEffect(() => {
    const cargar = async () => {
      try {
        const [prods, emps] = await Promise.all([
          productoApi.getAll(),
          emprendimientoApi.getAll(),
        ]);
        // Solo productos ACTIVOS
        setProductos(prods.filter((p) => p.nombreEstado !== 'INACTIVO'));
        setEmprendimientos(emps);
      } catch {
        setError('Error al cargar el marketplace');
      } finally {
        setLoading(false);
      }
    };
    cargar();
  }, []);

  // Mapa idEmprendimiento → emprendimiento (para categoría, logo, vendedor)
  const empMap = useMemo(
    () => new Map(emprendimientos.map((e) => [e.idEmprendimiento, e])),
    [emprendimientos]
  );

  // Categorías disponibles derivadas de los emprendimientos que tienen productos
  const categoriasDisponibles = useMemo(() => {
    const ids = new Set(productos.map((p) => p.idEmprendimiento));
    const cats = new Set(
      emprendimientos
        .filter((e) => ids.has(e.idEmprendimiento))
        .map((e) => e.nombreCategoria)
        .filter(Boolean)
    );
    return Array.from(cats).sort();
  }, [productos, emprendimientos]);

  // Filtrado: categoría + búsqueda por nombre de producto o emprendimiento
  const filtrados = useMemo(() => {
    return productos.filter((p) => {
      const emp = empMap.get(p.idEmprendimiento);
      const cat = emp?.nombreCategoria ?? '';
      const matchCategoria = categoriaSeleccionada ? cat === categoriaSeleccionada : true;
      const termino = busqueda.toLowerCase();
      const matchBusqueda = termino
        ? p.nombre.toLowerCase().includes(termino) ||
          p.nombreEmprendimiento.toLowerCase().includes(termino) ||
          (p.descripcion ?? '').toLowerCase().includes(termino)
        : true;
      return matchCategoria && matchBusqueda;
    });
  }, [productos, empMap, categoriaSeleccionada, busqueda]);

  return (
    <DashboardLayout title="Marketplace" subtitle="Descubre productos de tus compañeros emprendedores">
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      {!loading && !error && (
        <>
          {/* Search bar */}
          <div style={{ marginBottom: 24, position: 'relative', maxWidth: 520 }}>
            <Search
              style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)', pointerEvents: 'none' }}
              size={16}
            />
            <input
              type="text"
              placeholder="Buscar productos, emprendimientos..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
              className="form-input"
              style={{ paddingLeft: 42 }}
            />
          </div>

          <div style={{ display: 'flex', gap: 24, alignItems: 'flex-start' }}>
            {/* Sidebar de filtros */}
            <aside style={{ width: 200, flexShrink: 0 }}>
              <div className="card" style={{ padding: 16, position: 'sticky', top: 24 }}>
                <div
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 6,
                    fontSize: 10.5,
                    fontWeight: 700,
                    textTransform: 'uppercase',
                    letterSpacing: '0.9px',
                    color: 'var(--text-muted)',
                    marginBottom: 12,
                  }}
                >
                  <SlidersHorizontal size={11} />
                  Categoría
                </div>

                <button
                  onClick={() => setCategoriaSeleccionada(null)}
                  style={{
                    width: '100%', textAlign: 'left', padding: '8px 12px',
                    borderRadius: 'var(--radius-sm)', fontSize: 13, border: 'none', cursor: 'pointer',
                    marginBottom: 4,
                    background: categoriaSeleccionada === null ? 'var(--primary-bg)' : 'transparent',
                    color: categoriaSeleccionada === null ? 'var(--primary)' : 'var(--text-muted)',
                    fontWeight: categoriaSeleccionada === null ? 700 : 500,
                  }}
                >
                  Todos
                </button>

                {categoriasDisponibles.map((cat) => (
                  <button
                    key={cat}
                    onClick={() => setCategoriaSeleccionada(cat)}
                    style={{
                      width: '100%', textAlign: 'left', padding: '8px 12px',
                      borderRadius: 'var(--radius-sm)', fontSize: 13, border: 'none', cursor: 'pointer',
                      marginBottom: 4,
                      background: categoriaSeleccionada === cat ? 'var(--primary-bg)' : 'transparent',
                      color: categoriaSeleccionada === cat ? 'var(--primary)' : 'var(--text-muted)',
                      fontWeight: categoriaSeleccionada === cat ? 700 : 500,
                    }}
                  >
                    {cat}
                  </button>
                ))}

                {categoriasDisponibles.length === 0 && (
                  <p style={{ fontSize: 12, color: 'var(--text-muted)', paddingLeft: 12 }}>
                    Sin categorías
                  </p>
                )}
              </div>
            </aside>

            {/* Grid de productos */}
            <div style={{ flex: 1, minWidth: 0 }}>
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  marginBottom: 20,
                }}
              >
                <p style={{ fontSize: 13, color: 'var(--text-muted)' }}>
                  <strong style={{ color: 'var(--text)' }}>{filtrados.length}</strong>{' '}
                  producto{filtrados.length !== 1 ? 's' : ''} encontrado{filtrados.length !== 1 ? 's' : ''}
                  {categoriaSeleccionada && (
                    <span>
                      {' '}en <span style={{ color: 'var(--primary)', fontWeight: 600 }}>{categoriaSeleccionada}</span>
                      <button
                        onClick={() => setCategoriaSeleccionada(null)}
                        style={{ marginLeft: 8, fontSize: 11, color: 'var(--danger)', background: 'none', border: 'none', cursor: 'pointer', fontWeight: 600 }}
                      >
                        ✕ quitar filtro
                      </button>
                    </span>
                  )}
                </p>
              </div>

              {filtrados.length === 0 ? (
                <div
                  style={{
                    textAlign: 'center', padding: '80px 0',
                    color: 'var(--text-muted)',
                    background: 'white',
                    borderRadius: 'var(--radius-xl)',
                    border: '1px solid var(--border)',
                  }}
                >
                  <Package size={44} style={{ opacity: 0.2, display: 'block', margin: '0 auto 16px' }} />
                  <p style={{ fontWeight: 700, fontSize: 15, marginBottom: 6 }}>
                    No se encontraron productos
                  </p>
                  <p style={{ fontSize: 13 }}>
                    Intenta con otra búsqueda o categoría
                  </p>
                </div>
              ) : (
                <div
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))',
                    gap: 20,
                  }}
                >
                  {filtrados.map((p) => (
                    <ProductoCard
                      key={p.idProducto}
                      producto={p}
                      emprendimiento={empMap.get(p.idEmprendimiento)}
                    />
                  ))}
                </div>
              )}
            </div>
          </div>
        </>
      )}
    </DashboardLayout>
  );
};

export default MarketplacePage;
