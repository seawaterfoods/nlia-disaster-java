import { useEffect, useState, useCallback } from 'react';
import { Table, Button, Input, Select, Space, Tag, Typography, message } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { syslogApi } from '../../api/syslogs';
import type { Syslog } from '../../types';

const { Title } = Typography;
const { Option } = Select;

const ACTION_COLORS: Record<string, string> = {
  LOGIN: 'blue', LOGOUT: 'default', CREATE: 'green',
  UPDATE: 'orange', DELETE: 'red', QUERY: 'cyan',
};

const STATUS_COLORS: Record<string, string> = {
  SUCCESS: 'green', FAIL: 'red', ERROR: 'volcano',
};

export default function SyslogPage() {
  const [data, setData] = useState<Syslog[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [filterAction, setFilterAction] = useState<string>('');
  const [filterCid, setFilterCid] = useState('');

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await syslogApi.search({
        action: filterAction || undefined,
        cid: filterCid || undefined,
        page,
        size: pageSize,
      });
      const pageData = res.data.data;
      setData(pageData.content || []);
      setTotal(pageData.totalElements);
    } catch {
      message.error('載入日誌失敗');
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, filterAction, filterCid]);

  useEffect(() => { loadData(); }, [loadData]);

  const handleSearch = () => {
    setPage(0);
    loadData();
  };

  const columns: ColumnsType<Syslog> = [
    { title: '序號', dataIndex: 'sn', width: 70 },
    {
      title: '時間', dataIndex: 'adate', width: 170,
      render: (v: number) => v ? dayjs(v).format('YYYY-MM-DD HH:mm:ss') : '-',
    },
    { title: '帳號', dataIndex: 'loginid', width: 180, ellipsis: true },
    { title: '公司', dataIndex: 'company', width: 150, ellipsis: true },
    {
      title: '操作', dataIndex: 'action', width: 100,
      render: (v: string) => <Tag color={ACTION_COLORS[v] || 'default'}>{v}</Tag>,
    },
    {
      title: '狀態', dataIndex: 'mstatus', width: 90,
      render: (v: string) => <Tag color={STATUS_COLORS[v] || 'default'}>{v}</Tag>,
    },
    { title: 'IP', dataIndex: 'fromip', width: 140 },
    { title: '詳情', dataIndex: 'detail', ellipsis: true },
  ];

  return (
    <>
      <Title level={4}>系統日誌</Title>

      <Space style={{ marginBottom: 16 }}>
        <Select
          style={{ width: 150 }}
          placeholder="操作類型"
          allowClear
          value={filterAction || undefined}
          onChange={(v) => setFilterAction(v || '')}
        >
          <Option value="LOGIN">登入</Option>
          <Option value="LOGOUT">登出</Option>
          <Option value="CREATE">新增</Option>
          <Option value="UPDATE">修改</Option>
          <Option value="DELETE">刪除</Option>
          <Option value="QUERY">查詢</Option>
        </Select>
        <Input
          placeholder="公司代碼"
          value={filterCid}
          onChange={e => setFilterCid(e.target.value)}
          onPressEnter={handleSearch}
          style={{ width: 150 }}
        />
        <Button icon={<SearchOutlined />} onClick={handleSearch}>搜尋</Button>
      </Space>

      <Table
        rowKey="sn"
        columns={columns}
        dataSource={data}
        loading={loading}
        pagination={{
          current: page + 1,
          pageSize,
          total,
          showSizeChanger: true,
          showTotal: (t) => `共 ${t} 筆`,
          onChange: (p, ps) => { setPage(p - 1); setPageSize(ps); },
        }}
        scroll={{ x: 1000 }}
      />
    </>
  );
}
