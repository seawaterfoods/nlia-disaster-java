import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, Dropdown, Avatar, Space, theme } from 'antd';
import {
  HomeOutlined,
  AlertOutlined,
  BarChartOutlined,
  ThunderboltOutlined,
  TeamOutlined,
  SettingOutlined,
  FileTextOutlined,
  UserOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import { useAuth } from '../hooks/useAuth';
import { authApi } from '../api/auth';

const { Header, Sider, Content } = Layout;

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
  const location = useLocation();
  const [collapsed, setCollapsed] = useState(false);
  const { token: themeToken } = theme.useToken();

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

  const menuItems = [
    { key: '/', icon: <HomeOutlined />, label: '公告首頁' },
    ...(level >= 1 ? [{ key: '/reports', icon: <AlertOutlined />, label: '災損通報' }] : []),
    ...(level >= 3 ? [{ key: '/statistics', icon: <BarChartOutlined />, label: '統計報表' }] : []),
    ...(level >= 3 ? [{ key: '/disasters', icon: <ThunderboltOutlined />, label: '災害管理' }] : []),
    ...(level >= 2 ? [{ key: '/accounts', icon: <TeamOutlined />, label: '帳號管理' }] : []),
    ...(level >= 4 ? [{ key: '/config', icon: <SettingOutlined />, label: '系統設定' }] : []),
    ...(level >= 4 ? [{ key: '/syslogs', icon: <FileTextOutlined />, label: '系統日誌' }] : []),
  ];

  const userMenuItems = [
    {
      key: 'info',
      label: `${user?.name}（${LEVEL_LABELS[level] || ''}）`,
      disabled: true,
    },
    { type: 'divider' as const },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '登出',
      onClick: handleLogout,
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        trigger={null}
        theme="dark"
        width={220}
      >
        <div style={{
          height: 48,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#fff',
          fontWeight: 'bold',
          fontSize: collapsed ? 14 : 15,
          padding: '0 8px',
          whiteSpace: 'nowrap',
          overflow: 'hidden',
        }}>
          {collapsed ? '災損' : '重大災損通報系統'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{
          padding: '0 24px',
          background: themeToken.colorBgContainer,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          borderBottom: `1px solid ${themeToken.colorBorderSecondary}`,
        }}>
          <span
            onClick={() => setCollapsed(!collapsed)}
            style={{ fontSize: 18, cursor: 'pointer' }}
          >
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </span>
          <Space>
            <span>{user?.companyName}</span>
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Space style={{ cursor: 'pointer' }}>
                <Avatar icon={<UserOutlined />} size="small" />
                <span>{user?.name}</span>
              </Space>
            </Dropdown>
          </Space>
        </Header>
        <Content style={{ margin: 16, padding: 24, background: themeToken.colorBgContainer, borderRadius: themeToken.borderRadiusLG }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
