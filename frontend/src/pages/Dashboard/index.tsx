import { useAuth } from '../../hooks/useAuth';

export default function DashboardPage() {
  const { user } = useAuth();

  return (
    <div>
      <h2>公告首頁</h2>
      <p>歡迎，{user?.name}（{user?.companyName}）</p>
      <p>目前功能開發中...</p>
    </div>
  );
}
