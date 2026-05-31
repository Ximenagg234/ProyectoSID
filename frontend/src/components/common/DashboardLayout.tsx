import Sidebar from './Sidebar';

interface DashboardLayoutProps {
  children: React.ReactNode;
  title?: string;
  subtitle?: string;
  action?: React.ReactNode;
}

const DashboardLayout: React.FC<DashboardLayoutProps> = ({ children, title, subtitle, action }) => {
  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      <Sidebar />
      <div className="app-main">
        {(title || action) && (
          <div style={{ padding: '0 32px', paddingTop: 32, paddingBottom: 0 }}>
            <div className="page-header">
              <div>
                {title && <h1 className="page-title">{title}</h1>}
                {subtitle && <p className="page-subtitle">{subtitle}</p>}
              </div>
              {action && <div>{action}</div>}
            </div>
          </div>
        )}
        <div className="app-content" style={{ paddingTop: title ? 24 : 32 }}>
          {children}
        </div>
      </div>
    </div>
  );
};

export default DashboardLayout;
