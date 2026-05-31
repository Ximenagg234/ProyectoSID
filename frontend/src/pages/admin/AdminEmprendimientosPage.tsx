import { useEffect, useState } from 'react';
import * as emprendimientoApi from '../../api/emprendimientoApi';
import type { EmprendimientoResponse } from '../../types/emprendimiento.types';
import DashboardLayout from '../../components/common/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import ErrorMessage from '../../components/common/ErrorMessage';

const AdminEmprendimientosPage: React.FC = () => {
  const [emprendimientos, setEmprendimientos] = useState<EmprendimientoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    emprendimientoApi
      .getAll()
      .then(setEmprendimientos)
      .catch(() => setError('Error al cargar emprendimientos'))
      .finally(() => setLoading(false));
  }, []);

  return (
    <DashboardLayout title="Emprendimientos" subtitle="Vista de administrador">
      {loading && <LoadingSpinner />}
      {error && <ErrorMessage mensaje={error} />}

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <table className="table-custom">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Categoría</th>
              <th>Propietario</th>
              <th>Semestre</th>
              <th>Estado</th>
            </tr>
          </thead>
          <tbody>
            {emprendimientos.map((e) => (
              <tr key={e.idEmprendimiento}>
                <td style={{ fontWeight: 600 }}>{e.nombre}</td>
                <td><span className="badge badge-primary">{e.nombreCategoria}</span></td>
                <td style={{ color: 'var(--text-muted)' }}>{e.nombreUsuario}</td>
                <td style={{ color: 'var(--text-muted)' }}>{e.nombreSemestre}</td>
                <td><span className={`badge badge-${e.nombreEstado}`}>{e.nombreEstado}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
        {!loading && emprendimientos.length === 0 && (
          <p style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-muted)', fontSize: 13 }}>No hay emprendimientos</p>
        )}
      </div>
    </DashboardLayout>
  );
};

export default AdminEmprendimientosPage;
