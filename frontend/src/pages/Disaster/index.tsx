import { useEffect, useState, useCallback } from 'react';
import { Table, Button, Modal, Form, Input, DatePicker, Tag, Space, message, Typography, Select } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { disasterApi } from '../../api/disasters';
import { configApi } from '../../api/config';
import { useAuth } from '../../hooks/useAuth';
import type { Disaster, DisasterRequest, NdReason } from '../../types';

const { Title } = Typography;
const { TextArea } = Input;

export default function DisasterPage() {
  const { user } = useAuth();
  const [disasters, setDisasters] = useState<Disaster[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Disaster | null>(null);
  const [reasons, setReasons] = useState<NdReason[]>([]);
  const [form] = Form.useForm();

  const level = user?.level ?? 0;
  const canEdit = level >= 3;

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const [disRes, reasonRes] = await Promise.all([
        disasterApi.list('all'),
        configApi.listReasons(),
      ]);
      setDisasters(disRes.data.data || []);
      setReasons(reasonRes.data.data || []);
    } catch {
      message.error('載入資料失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadData(); }, [loadData]);

  const handleCreate = () => {
    setEditing(null);
    form.resetFields();
    setModalOpen(true);
  };

  const handleEdit = (record: Disaster) => {
    setEditing(record);
    form.setFieldsValue({
      ...record,
      ddate: record.ddate ? dayjs(record.ddate) : null,
      adate: record.adate ? dayjs(record.adate) : null,
      sdate: record.sdate ? dayjs(record.sdate) : null,
      vdate: record.vdate ? dayjs(record.vdate) : null,
      claimDate: record.claimDate ? dayjs(record.claimDate) : null,
      claimValid: record.claimValid ? dayjs(record.claimValid) : null,
    });
    setModalOpen(true);
  };

  const handleDelete = (sn: number) => {
    Modal.confirm({
      title: '確認刪除',
      content: '確定要刪除此災害公告？此操作無法復原。',
      okText: '刪除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await disasterApi.delete(sn);
          message.success('刪除成功');
          loadData();
        } catch {
          message.error('刪除失敗');
        }
      },
    });
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const data: DisasterRequest = {
        ...values,
        ddate: values.ddate?.format('YYYY-MM-DD'),
        adate: values.adate?.format('YYYY-MM-DD'),
        sdate: values.sdate?.format('YYYY-MM-DD'),
        vdate: values.vdate?.format('YYYY-MM-DD'),
        claimDate: values.claimDate?.format('YYYY-MM-DD'),
        claimValid: values.claimValid?.format('YYYY-MM-DD'),
      };
      if (editing) {
        await disasterApi.update(editing.sn, data);
        message.success('更新成功');
      } else {
        await disasterApi.create(data);
        message.success('新增成功');
      }
      setModalOpen(false);
      loadData();
    } catch {
      message.error('儲存失敗');
    }
  };

  const columns: ColumnsType<Disaster> = [
    { title: '序號', dataIndex: 'sn', width: 70 },
    { title: '標題', dataIndex: 'title', ellipsis: true },
    { title: '災害日期', dataIndex: 'ddate', width: 120 },
    { title: '通報截止', dataIndex: 'vdate', width: 120 },
    {
      title: '顯示狀態', dataIndex: 'showStatus', width: 90,
      render: (v: string) => <Tag color={v === 'Y' ? 'green' : 'default'}>{v === 'Y' ? '顯示' : '隱藏'}</Tag>,
    },
    {
      title: '理賠狀態', dataIndex: 'qstatus', width: 90,
      render: (v: string) => <Tag color={v === 'Y' ? 'blue' : 'default'}>{v === 'Y' ? '啟用' : '停用'}</Tag>,
    },
    ...(canEdit ? [{
      title: '操作', width: 150,
      render: (_: unknown, record: Disaster) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => handleEdit(record)}>編輯</Button>
          {level >= 4 && (
            <Button icon={<DeleteOutlined />} size="small" danger onClick={() => handleDelete(record.sn)}>刪除</Button>
          )}
        </Space>
      ),
    }] : []),
  ];

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>災害管理</Title>
        {canEdit && (
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新增災害</Button>
        )}
      </div>
      <Table
        rowKey="sn"
        columns={columns}
        dataSource={disasters}
        loading={loading}
        pagination={{ pageSize: 10, showSizeChanger: true, showTotal: (t) => `共 ${t} 筆` }}
      />

      <Modal
        title={editing ? '編輯災害' : '新增災害'}
        open={modalOpen}
        onOk={handleSubmit}
        onCancel={() => setModalOpen(false)}
        width={720}
        okText="儲存"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="title" label="標題" rules={[{ required: true, message: '請輸入標題' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="content" label="內容">
            <TextArea rows={3} />
          </Form.Item>
          <Space size="large">
            <Form.Item name="ddate" label="災害日期"><DatePicker /></Form.Item>
            <Form.Item name="adate" label="公告日期"><DatePicker /></Form.Item>
            <Form.Item name="sdate" label="開始日期"><DatePicker /></Form.Item>
            <Form.Item name="vdate" label="通報截止"><DatePicker /></Form.Item>
          </Space>
          <Space size="large">
            <Form.Item name="claimDate" label="理賠日期"><DatePicker /></Form.Item>
            <Form.Item name="claimValid" label="理賠有效"><DatePicker /></Form.Item>
            <Form.Item name="claimAlert" label="理賠提醒(天)"><Input type="number" /></Form.Item>
          </Space>
          <Space size="large">
            <Form.Item name="reason" label="災害原因">
              <Select style={{ width: 200 }} allowClear placeholder="選擇原因">
                {reasons.map(r => (
                  <Select.Option key={r.sn} value={r.id}>{r.content}</Select.Option>
                ))}
              </Select>
            </Form.Item>
            <Form.Item name="showStatus" label="顯示狀態">
              <Select style={{ width: 100 }}>
                <Select.Option value="Y">顯示</Select.Option>
                <Select.Option value="N">隱藏</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="qstatus" label="理賠狀態">
              <Select style={{ width: 100 }}>
                <Select.Option value="Y">啟用</Select.Option>
                <Select.Option value="N">停用</Select.Option>
              </Select>
            </Form.Item>
          </Space>
          <Space size="large">
            <Form.Item name="allowIns" label="允許保險">
              <Select style={{ width: 100 }}>
                <Select.Option value="Y">是</Select.Option>
                <Select.Option value="N">否</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="emailNotice" label="郵件通知">
              <Select style={{ width: 100 }}>
                <Select.Option value="Y">是</Select.Option>
                <Select.Option value="N">否</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item name="df" label="DF">
              <Input style={{ width: 100 }} />
            </Form.Item>
          </Space>
        </Form>
      </Modal>
    </>
  );
}
