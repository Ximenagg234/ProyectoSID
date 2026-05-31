import { Link } from 'react-router-dom';
import type { EmprendimientoResponse } from '../../types/emprendimiento.types';
import { Building2, User, Pencil, Package, Trash2 } from 'lucide-react';

interface Props {
  emprendimiento: EmprendimientoResponse;
  onDelete?: (id: number) => void;
  showActions?: boolean;
}

const EmprendimientoCard: React.FC<Props> = ({ emprendimiento, onDelete, showActions = false }) => {
  const isActivo = emprendimiento.nombreEstado === 'ACTIVO';

  return (
    <Link
      to={`/emprendimientos/${emprendimiento.idEmprendimiento}`}
      className="block group"
    >
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg hover:-translate-y-1 transition-all duration-200 h-full flex flex-col">
        {/* Image / logo area */}
        <div className="relative h-40 bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center">
          {emprendimiento.logoUrl ? (
            <img
              src={emprendimiento.logoUrl}
              alt={emprendimiento.nombre}
              className="w-20 h-20 rounded-xl object-cover shadow-md"
            />
          ) : (
            <Building2 className="w-12 h-12 text-blue-300" />
          )}
          {emprendimiento.destacado === true && (
            <span className="absolute top-3 right-3 bg-yellow-400 text-yellow-900 text-xs font-semibold px-2.5 py-1 rounded-full shadow-sm">
              ✨ Destacado
            </span>
          )}
        </div>

        {/* Body */}
        <div className="p-5 flex flex-col flex-1">
          <span className="inline-block bg-blue-100 text-blue-700 text-xs font-semibold px-3 py-1 rounded-full mb-2 self-start">
            {emprendimiento.nombreCategoria}
          </span>
          <h3 className="font-bold text-gray-900 text-lg leading-tight mb-1">
            {emprendimiento.nombre}
          </h3>
          <p className="text-gray-600 text-sm line-clamp-2 mb-3 flex-1">
            {emprendimiento.descripcion}
          </p>
          <div className="flex items-center gap-1.5 text-xs text-gray-400 mb-4">
            <User className="w-3.5 h-3.5" />
            <span className="truncate">{emprendimiento.nombreUsuario}</span>
          </div>

          {/* Footer row */}
          <div className="flex items-center justify-between flex-wrap gap-2">
            <span
              className={`text-xs font-semibold px-3 py-1 rounded-full ${
                isActivo
                  ? 'bg-green-100 text-green-700'
                  : 'bg-gray-100 text-gray-500'
              }`}
            >
              {emprendimiento.nombreEstado}
            </span>

            {showActions && (
              <div
                className="flex gap-1.5"
                onClick={(e) => e.preventDefault()}
              >
                <Link
                  to={`/mis-emprendimientos/editar/${emprendimiento.idEmprendimiento}`}
                  onClick={(e) => e.stopPropagation()}
                  className="flex items-center gap-1 text-xs bg-amber-50 text-amber-700 px-2.5 py-1.5 rounded-lg hover:bg-amber-100 transition-colors font-semibold"
                >
                  <Pencil className="w-3 h-3" />
                  Editar
                </Link>
                <Link
                  to={`/mis-emprendimientos/${emprendimiento.idEmprendimiento}/productos`}
                  onClick={(e) => e.stopPropagation()}
                  className="flex items-center gap-1 text-xs bg-blue-50 text-blue-700 px-2.5 py-1.5 rounded-lg hover:bg-blue-100 transition-colors font-semibold"
                >
                  <Package className="w-3 h-3" />
                  Productos
                </Link>
                {onDelete && (
                  <button
                    onClick={(e) => {
                      e.preventDefault();
                      e.stopPropagation();
                      onDelete(emprendimiento.idEmprendimiento);
                    }}
                    className="flex items-center gap-1 text-xs bg-red-50 text-red-600 px-2.5 py-1.5 rounded-lg hover:bg-red-100 transition-colors font-semibold"
                  >
                    <Trash2 className="w-3 h-3" />
                    Eliminar
                  </button>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </Link>
  );
};

export default EmprendimientoCard;
