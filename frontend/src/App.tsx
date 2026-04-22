import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhTW from 'antd/locale/zh_TW';
import { AuthProvider } from './hooks/useAuth';
import ProtectedRoute from './components/ProtectedRoute';
import MainLayout from './components/MainLayout';
import LoginPage from './pages/Login';
import DashboardPage from './pages/Dashboard';
import DisasterPage from './pages/Disaster';
import ReportPage from './pages/Report';
import AccountPage from './pages/Account';
import StatisticsPage from './pages/Statistics';
import SystemConfigPage from './pages/SystemConfig';
import SyslogPage from './pages/Syslog';
import './App.css';

function App() {
  return (
    <ConfigProvider locale={zhTW}>
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
              <Route path="reports" element={<ReportPage />} />
              <Route path="statistics" element={<StatisticsPage />} />
              <Route path="disasters" element={<DisasterPage />} />
              <Route path="accounts" element={<AccountPage />} />
              <Route path="config" element={<SystemConfigPage />} />
              <Route path="syslogs" element={<SyslogPage />} />
            </Route>
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ConfigProvider>
  );
}

export default App
