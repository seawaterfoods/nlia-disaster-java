import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Row, Col, Typography, Statistic, Tag, List, Spin, message } from 'antd';
import { ThunderboltOutlined, AlertOutlined, CheckCircleOutlined } from '@ant-design/icons';
import { useAuth } from '../../hooks/useAuth';
import { disasterApi } from '../../api/disasters';
import type { Disaster } from '../../types';

const { Title, Text } = Typography;

export default function DashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [disasters, setDisasters] = useState<Disaster[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDisasters();
  }, []);

  const loadDisasters = async () => {
    try {
      const res = await disasterApi.list('active');
      setDisasters(res.data.data || []);
    } catch {
      message.error('載入災害資料失敗');
    } finally {
      setLoading(false);
    }
  };

  const activeCount = disasters.filter(d => d.showStatus === 'Y').length;
  const closedCount = disasters.filter(d => d.showStatus !== 'Y').length;

  return (
    <Spin spinning={loading}>
      <Title level={4}>公告首頁</Title>
      <Text type="secondary">歡迎，{user?.name}（{user?.companyName}）</Text>

      <Row gutter={16} style={{ marginTop: 24 }}>
        <Col span={8}>
          <Card>
            <Statistic title="進行中災害" value={activeCount} prefix={<ThunderboltOutlined />} valueStyle={{ color: '#cf1322' }} />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic title="已結案災害" value={closedCount} prefix={<CheckCircleOutlined />} valueStyle={{ color: '#3f8600' }} />
          </Card>
        </Col>
        <Col span={8}>
          <Card>
            <Statistic title="災害總數" value={disasters.length} prefix={<AlertOutlined />} />
          </Card>
        </Col>
      </Row>

      <Card title="目前公告災害" style={{ marginTop: 24 }}>
        <List
          dataSource={disasters}
          locale={{ emptyText: '目前無災害公告' }}
          renderItem={(item) => (
            <List.Item
              actions={[
                <Tag
                  key="status"
                  color={item.showStatus === 'Y' ? 'green' : 'default'}
                >
                  {item.showStatus === 'Y' ? '進行中' : '已結束'}
                </Tag>,
              ]}
              style={{ cursor: 'pointer' }}
              onClick={() => navigate('/reports')}
            >
              <List.Item.Meta
                title={item.title}
                description={`災害日期：${item.ddate || '-'} ｜ 通報截止：${item.vdate || '-'}`}
              />
            </List.Item>
          )}
        />
      </Card>
    </Spin>
  );
}
