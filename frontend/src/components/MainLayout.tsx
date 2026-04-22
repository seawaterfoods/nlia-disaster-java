import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { authApi } from '../api/auth';

const LEVEL_LABELS: Record<number, string> = {
  1: '一般使用者',
  2: '公司管理者',
  3: '委員會',
  4: '系統管理員',
  5: '超級管理員',
};

export default function MainLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      await authApi.logout();
    } catch {
      // Ignore errors during logout
    }
    logout();
    navigate('/login');
  };

  const level = user?.level ?? 0;

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      {/* Sidebar */}
      <nav style={{ width: '220px', background: '#1e3a5f', color: '#fff', padding: '1rem' }}>
        <h3 style={{ marginBottom: '1.5rem', fontSize: '0.9rem' }}>重大災損通報系統</h3>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          <li style={{ marginBottom: '0.5rem' }}><Link to="/" style={linkStyle}>公告首頁</Link></li>
          {level >= 1 && <li style={{ marginBottom: '0.5rem' }}><Link to="/reports" style={linkStyle}>災損通報</Link></li>}
          {level >= 3 && <li style={{ marginBottom: '0.5rem' }}><Link to="/statistics" style={linkStyle}>統計報表</Link></li>}
          {level >= 3 && <li style={{ marginBottom: '0.5rem' }}><Link to="/disasters" style={linkStyle}>災害管理</Link></li>}
          {level >= 2 && <li style={{ marginBottom: '0.5rem' }}><Link to="/accounts" style={linkStyle}>帳號管理</Link></li>}
          {level >= 4 && <li style={{ marginBottom: '0.5rem' }}><Link to="/config" style={linkStyle}>系統設定</Link></li>}
          {level >= 4 && <li style={{ marginBottom: '0.5rem' }}><Link to="/syslogs" style={linkStyle}>系統日誌</Link></li>}
        </ul>
      </nav>

      {/* Main content */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
        {/* Header */}
        <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.75rem 1.5rem', background: '#f8f9fa', borderBottom: '1px solid #dee2e6' }}>
          <span>{user?.companyName}</span>
          <div>
            <span style={{ marginRight: '1rem' }}>{user?.name}（{LEVEL_LABELS[level] || ''}）</span>
            <button onClick={handleLogout} style={{ padding: '0.25rem 0.75rem', cursor: 'pointer' }}>登出</button>
          </div>
        </header>

        {/* Content */}
        <main style={{ flex: 1, padding: '1.5rem' }}>
          <Outlet />
        </main>
      </div>
    </div>
  );
}

const linkStyle: React.CSSProperties = {
  color: '#ccc',
  textDecoration: 'none',
};
