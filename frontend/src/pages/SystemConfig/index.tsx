import { useEffect, useState, useCallback } from 'react';
import {
  Tabs, Table, Button, Modal, Form, Input, InputNumber,
  Space, message, Typography, Popconfirm,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SaveOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { configApi } from '../../api/config';
import type { SysConfig, NdType, NdReason, AddrType } from '../../types';

const { Title } = Typography;

function SysConfigTab() {
  const [data, setData] = useState<SysConfig[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingKey, setEditingKey] = useState('');
  const [editValue, setEditValue] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await configApi.listConfigs();
      setData(res.data.data || []);
    } catch {
      message.error('載入設定失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleSave = async (id: string) => {
    try {
      await configApi.updateConfig(id, editValue);
      message.success('更新成功');
      setEditingKey('');
      load();
    } catch {
      message.error('更新失敗');
    }
  };

  const columns: ColumnsType<SysConfig> = [
    { title: '代碼', dataIndex: 'id', width: 150 },
    { title: '名稱', dataIndex: 'title', width: 200 },
    {
      title: '內容', dataIndex: 'content',
      render: (text: string, record) => {
        if (editingKey === record.id) {
          return (
            <Input.TextArea
              value={editValue}
              onChange={e => setEditValue(e.target.value)}
              autoSize={{ minRows: 1, maxRows: 4 }}
            />
          );
        }
        return text;
      },
    },
    {
      title: '操作', width: 120,
      render: (_, record) => {
        if (editingKey === record.id) {
          return (
            <Space>
              <Button icon={<SaveOutlined />} size="small" type="primary" onClick={() => handleSave(record.id)}>儲存</Button>
              <Button size="small" onClick={() => setEditingKey('')}>取消</Button>
            </Space>
          );
        }
        return (
          <Button icon={<EditOutlined />} size="small" onClick={() => { setEditingKey(record.id); setEditValue(record.content); }}>
            編輯
          </Button>
        );
      },
    },
  ];

  return <Table rowKey="id" columns={columns} dataSource={data} loading={loading} pagination={false} size="small" />;
}

function NdTypeTab() {
  const [data, setData] = useState<NdType[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<NdType | null>(null);
  const [form] = Form.useForm();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await configApi.listProducts();
      setData(res.data.data || []);
    } catch {
      message.error('載入產品類型失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editing) {
        await configApi.updateProduct(editing.sn, values);
        message.success('更新成功');
      } else {
        await configApi.createProduct(values);
        message.success('新增成功');
      }
      setModalOpen(false);
      load();
    } catch {
      message.error('儲存失敗');
    }
  };

  const handleDelete = async (sn: number) => {
    try {
      await configApi.deleteProduct(sn);
      message.success('刪除成功');
      load();
    } catch {
      message.error('刪除失敗');
    }
  };

  const columns: ColumnsType<NdType> = [
    { title: '序號', dataIndex: 'sn', width: 70 },
    { title: '險種分類', dataIndex: 'hname', width: 150 },
    { title: '業務類別', dataIndex: 'bname', width: 150 },
    { title: '產品名稱', dataIndex: 'pname' },
    { title: '排序', dataIndex: 'hsort', width: 70 },
    {
      title: '操作', width: 130,
      render: (_, record) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => { setEditing(record); form.setFieldsValue(record); setModalOpen(true); }}>編輯</Button>
          <Popconfirm title="確定刪除？" onConfirm={() => handleDelete(record.sn)}>
            <Button icon={<DeleteOutlined />} size="small" danger />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      <Button type="primary" icon={<PlusOutlined />} onClick={() => { setEditing(null); form.resetFields(); setModalOpen(true); }} style={{ marginBottom: 16 }}>
        新增產品
      </Button>
      <Table rowKey="sn" columns={columns} dataSource={data} loading={loading} pagination={{ pageSize: 15 }} size="small" />
      <Modal title={editing ? '編輯產品' : '新增產品'} open={modalOpen} onOk={handleSubmit} onCancel={() => setModalOpen(false)} okText="儲存" cancelText="取消">
        <Form form={form} layout="vertical">
          <Form.Item name="hname" label="險種分類" rules={[{ required: true, message: '必填' }]}><Input /></Form.Item>
          <Form.Item name="bname" label="業務類別" rules={[{ required: true, message: '必填' }]}><Input /></Form.Item>
          <Form.Item name="pname" label="產品名稱" rules={[{ required: true, message: '必填' }]}><Input /></Form.Item>
          <Form.Item name="hsort" label="排序"><InputNumber min={0} /></Form.Item>
        </Form>
      </Modal>
    </>
  );
}

function NdReasonTab() {
  const [data, setData] = useState<NdReason[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<NdReason | null>(null);
  const [form] = Form.useForm();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await configApi.listReasons();
      setData(res.data.data || []);
    } catch {
      message.error('載入災害原因失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editing) {
        await configApi.updateReason(editing.sn, values);
        message.success('更新成功');
      } else {
        await configApi.createReason(values);
        message.success('新增成功');
      }
      setModalOpen(false);
      load();
    } catch {
      message.error('儲存失敗');
    }
  };

  const handleDelete = async (sn: number) => {
    try {
      await configApi.deleteReason(sn);
      message.success('刪除成功');
      load();
    } catch {
      message.error('刪除失敗');
    }
  };

  const columns: ColumnsType<NdReason> = [
    { title: '序號', dataIndex: 'sn', width: 70 },
    { title: '代碼', dataIndex: 'id', width: 120 },
    { title: '說明', dataIndex: 'content' },
    {
      title: '操作', width: 130,
      render: (_, record) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => { setEditing(record); form.setFieldsValue(record); setModalOpen(true); }}>編輯</Button>
          <Popconfirm title="確定刪除？" onConfirm={() => handleDelete(record.sn)}>
            <Button icon={<DeleteOutlined />} size="small" danger />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      <Button type="primary" icon={<PlusOutlined />} onClick={() => { setEditing(null); form.resetFields(); setModalOpen(true); }} style={{ marginBottom: 16 }}>
        新增原因
      </Button>
      <Table rowKey="sn" columns={columns} dataSource={data} loading={loading} pagination={false} size="small" />
      <Modal title={editing ? '編輯原因' : '新增原因'} open={modalOpen} onOk={handleSubmit} onCancel={() => setModalOpen(false)} okText="儲存" cancelText="取消">
        <Form form={form} layout="vertical">
          <Form.Item name="id" label="代碼" rules={[{ required: true, message: '必填' }]}><Input /></Form.Item>
          <Form.Item name="content" label="說明" rules={[{ required: true, message: '必填' }]}><Input.TextArea rows={2} /></Form.Item>
        </Form>
      </Modal>
    </>
  );
}

function AddrTypeTab() {
  const [data, setData] = useState<AddrType[]>([]);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const res = await configApi.listAddresses();
      setData(res.data.data || []);
    } catch {
      message.error('載入地址資料失敗');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const columns: ColumnsType<AddrType> = [
    { title: '郵遞區號', dataIndex: 'asn', width: 100 },
    { title: '縣市代碼', dataIndex: 'csn', width: 100 },
    { title: '縣市名稱', dataIndex: 'cname', width: 120 },
    { title: '鄉鎮區', dataIndex: 'aname' },
  ];

  return (
    <Table rowKey="asn" columns={columns} dataSource={data} loading={loading} pagination={{ pageSize: 20, showSizeChanger: true }} size="small" />
  );
}

export default function SystemConfigPage() {
  const tabItems = [
    { key: 'sysconfig', label: '系統設定', children: <SysConfigTab /> },
    { key: 'ndtype', label: '產品類型', children: <NdTypeTab /> },
    { key: 'ndreason', label: '災害原因', children: <NdReasonTab /> },
    { key: 'addrtype', label: '地址資料', children: <AddrTypeTab /> },
  ];

  return (
    <>
      <Title level={4}>系統設定</Title>
      <Tabs items={tabItems} />
    </>
  );
}
