import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './hooks/useAuth';
import ProtectedRoute from './components/ProtectedRoute';
import MainLayout from './components/MainLayout';
import LoginPage from './pages/Login';
import DashboardPage from './pages/Dashboard';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/" element={
            <ProtectedRoute>
              <MainLayout />
            </ProtectedRoute>
          }>
            <Route index element={<DashboardPage />} />
            <Route path="reports" element={<div><h2>災損通報</h2><p>開發中...</p></div>} />
            <Route path="statistics" element={<div><h2>統計報表</h2><p>開發中...</p></div>} />
            <Route path="disasters" element={<div><h2>災害管理</h2><p>開發中...</p></div>} />
            <Route path="accounts" element={<div><h2>帳號管理</h2><p>開發中...</p></div>} />
            <Route path="config" element={<div><h2>系統設定</h2><p>開發中...</p></div>} />
            <Route path="syslogs" element={<div><h2>系統日誌</h2><p>開發中...</p></div>} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App
