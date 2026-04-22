import { useEffect, useState, useCallback } from 'react';
import { Select, Tabs, Table, Typography, Space, message, Spin } from 'antd';
import { disasterApi } from '../../api/disasters';
import { statisticsApi } from '../../api/statistics';
import type { Disaster, StatisticsRow } from '../../types';

const { Title, Text } = Typography;

type TabKey = 'company' | 'area' | 'product' | 'areaProduct';

const TAB_LABELS: Record<TabKey, string> = {
  company: '依公司',
  area: '依地區',
  product: '依險種',
  areaProduct: '依地區+險種',
};

export default function StatisticsPage() {
  const [disasters, setDisasters] = useState<Disaster[]>([]);
  const [selectedDisaster, setSelectedDisaster] = useState<number | undefined>();
  const [activeTab, setActiveTab] = useState<TabKey>('company');
  const [data, setData] = useState<StatisticsRow[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    disasterApi.list('all')
      .then(res => setDisasters(res.data.data || []))
      .catch(() => message.error('載入災害列表失敗'));
  }, []);

  const loadStats = useCallback(async (ndsn: number, tab: TabKey) => {
    setLoading(true);
    try {
      const apiMap = {
        company: statisticsApi.byCompany,
        area: statisticsApi.byArea,
        product: statisticsApi.byProduct,
        areaProduct: statisticsApi.byAreaProduct,
      };
      const res = await apiMap[tab](ndsn);
      setData(res.data.data || []);
    } catch {
      message.error('載入統計資料失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  const handleDisasterChange = (ndsn: number) => {
    setSelectedDisaster(ndsn);
    loadStats(ndsn, activeTab);
  };

  const handleTabChange = (tab: string) => {
    const key = tab as TabKey;
    setActiveTab(key);
    if (selectedDisaster) loadStats(selectedDisaster, key);
  };

  const buildColumns = (rows: StatisticsRow[]) => {
    if (rows.length === 0) return [];
    const keys = Object.keys(rows[0]);
    return keys.map(key => ({
      title: key,
      dataIndex: key,
      key,
      render: (v: string | number) => {
        if (typeof v === 'number') {
          return v.toLocaleString('zh-TW');
        }
        return v;
      },
    }));
  };

  const summaryRow = (rows: StatisticsRow[]) => {
    if (rows.length === 0) return null;
    const keys = Object.keys(rows[0]);
    const numericKeys = keys.filter(k => rows.some(r => typeof r[k] === 'number'));
    if (numericKeys.length === 0) return null;
    const sums: Record<string, number> = {};
    numericKeys.forEach(k => {
      sums[k] = rows.reduce((acc, r) => acc + (typeof r[k] === 'number' ? (r[k] as number) : 0), 0);
    });
    return (
      <Table.Summary.Row>
        {keys.map((k, i) => (
          <Table.Summary.Cell key={k} index={i}>
            <Text strong>
              {i === 0 ? '合計' : sums[k] !== undefined ? sums[k].toLocaleString('zh-TW') : ''}
            </Text>
          </Table.Summary.Cell>
        ))}
      </Table.Summary.Row>
    );
  };

  const tabItems = (Object.keys(TAB_LABELS) as TabKey[]).map(key => ({
    key,
    label: TAB_LABELS[key],
    children: (
      <Spin spinning={loading}>
        <Table
          rowKey={(_, index) => String(index)}
          columns={buildColumns(data)}
          dataSource={data}
          pagination={false}
          size="small"
          scroll={{ x: 'max-content' }}
          summary={() => summaryRow(data)}
        />
      </Spin>
    ),
  }));

  return (
    <>
      <Title level={4}>統計報表</Title>

      <Space style={{ marginBottom: 16 }}>
        <Text>選擇災害：</Text>
        <Select
          style={{ width: 400 }}
          placeholder="請選擇災害"
          value={selectedDisaster}
          onChange={handleDisasterChange}
          showSearch
          optionFilterProp="label"
          options={disasters.map(d => ({ label: `${d.title}（${d.ddate || '-'}）`, value: d.sn }))}
        />
      </Space>

      {selectedDisaster ? (
        <Tabs activeKey={activeTab} onChange={handleTabChange} items={tabItems} />
      ) : (
        <Text type="secondary">請先選擇災害以查看統計</Text>
      )}
    </>
  );
}
