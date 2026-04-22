import { useNavigate } from 'react-router-dom';
import { Form, Input, Button, Card, message, notification, Typography } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { authApi } from '../../api/auth';
import { useAuth } from '../../hooks/useAuth';

const { Title } = Typography;

export default function LoginPage() {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (values: { email: string; password: string }) => {
    try {
      const res = await authApi.login(values);
      const { accessToken, refreshToken, user, emailFailureNotifications } = res.data.data;
      login(accessToken, refreshToken, user);

      if (emailFailureNotifications && emailFailureNotifications.length > 0) {
        emailFailureNotifications.forEach((msg: string) => {
          notification.warning({ message: '郵件發送失敗通知', description: msg, duration: 8 });
        });
      }

      message.success('登入成功');
      navigate('/');
    } catch (err: unknown) {
      const axiosErr = err as { response?: { data?: { message?: string } } };
      message.error(axiosErr.response?.data?.message || '登入失敗');
    }
  };

  return (
    <div className="login-container">
      <Card style={{ width: 400 }}>
        <Title level={3} style={{ textAlign: 'center', marginBottom: 32 }}>
          重大災害損失預估通報系統
        </Title>
        <Form form={form} onFinish={handleSubmit} layout="vertical" size="large">
          <Form.Item
            name="email"
            label="帳號 (Email)"
            rules={[{ required: true, message: '請輸入帳號' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="請輸入 Email" />
          </Form.Item>
          <Form.Item
            name="password"
            label="密碼"
            rules={[{ required: true, message: '請輸入密碼' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="請輸入密碼" />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block>
              登入
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
