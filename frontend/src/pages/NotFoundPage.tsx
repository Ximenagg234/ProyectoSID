import { Link } from 'react-router-dom';

const NotFoundPage: React.FC = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="text-center">
        <div className="text-8xl mb-6">🔍</div>
        <h1 className="text-7xl font-extrabold text-blue-600 mb-4">404</h1>
        <p className="text-2xl font-bold text-gray-800 mb-2">Página no encontrada</p>
        <p className="text-gray-500 mb-8">La página que buscas no existe o fue movida.</p>
        <Link
          to="/marketplace"
          className="bg-blue-600 hover:bg-blue-700 text-white font-semibold px-6 py-3 rounded-xl transition-colors inline-block"
        >
          ← Volver al inicio
        </Link>
      </div>
    </div>
  );
};

export default NotFoundPage;
