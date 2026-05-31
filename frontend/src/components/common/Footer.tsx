import { Link } from 'react-router-dom';
import { Store } from 'lucide-react';

const Footer: React.FC = () => {
  return (
    <footer className="bg-gray-900 text-white mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {/* Col 1: Brand */}
          <div>
            <div className="flex items-center gap-2 font-bold text-white text-lg mb-3">
              <Store className="w-6 h-6 text-blue-400" />
              <span>Emprende ICESI</span>
            </div>
            <p className="text-gray-400 text-sm leading-relaxed">
              La plataforma de emprendimientos universitarios de Icesi
            </p>
          </div>

          {/* Col 2: Plataforma */}
          <div>
            <h3 className="font-semibold text-white mb-4 text-sm uppercase tracking-wider">
              Plataforma
            </h3>
            <ul className="space-y-2">
              <li>
                <Link
                  to="/marketplace"
                  className="text-gray-400 hover:text-white text-sm transition-colors"
                >
                  Marketplace
                </Link>
              </li>
              <li>
                <Link
                  to="/mis-emprendimientos/nuevo"
                  className="text-gray-400 hover:text-white text-sm transition-colors"
                >
                  Crear emprendimiento
                </Link>
              </li>
            </ul>
          </div>

          {/* Col 3: Información */}
          <div>
            <h3 className="font-semibold text-white mb-4 text-sm uppercase tracking-wider">
              Información
            </h3>
            <ul className="space-y-2">
              <li>
                <a
                  href="https://www.icesi.edu.co"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-gray-400 hover:text-white text-sm transition-colors"
                >
                  Universidad Icesi
                </a>
              </li>
              <li>
                <a
                  href="mailto:emprendeicesi@icesi.edu.co"
                  className="text-gray-400 hover:text-white text-sm transition-colors"
                >
                  Contacto
                </a>
              </li>
            </ul>
          </div>
        </div>

        <div className="border-t border-gray-800 mt-10 pt-6 text-center">
          <p className="text-gray-500 text-sm">
            © {new Date().getFullYear()} Emprende ICESI · Universidad Icesi
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
