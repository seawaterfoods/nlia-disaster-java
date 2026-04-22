import { useEffect, useState, useCallback } from 'react';
import {
  Table, Button, Drawer, Form, Input, Select, Tag, Space,
  Modal, message, Typography,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, KeyOutlined, SearchOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { accountApi } from '../../api/accounts';
import { useAuth } from '../../hooks/useAuth';
import type { Account, AccountCreateRequest, AccountUpdateRequest, Company } from '../../types';

const { Title } = Typography;

const LEVEL_LABELS: Record<number, string> = {
  1: '一般使用者',
  2: '公司管理者',
  3: '委員會',
  4: '系統管理員',
  5: '超級管理員',
};

const LEVEL_COLORS: Record<number, string> = {
  1: 'default', 2: 'blue', 3: 'green', 4: 'orange', 5: 'red',
};

export default function AccountPage() {
  const { user } = useAuth();
  const level = user?.level ?? 0;
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(20);
  const [searchEmail, setSearchEmail] = useState('');
  const [searchCid, setSearchCid] = useState('');
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [editing, setEditing] = useState<Account | null>(null);
  const [passwordModalOpen, setPasswordModalOpen] = useState(false);
  const [passwordTarget, setPasswordTarget] = useState<Account | null>(null);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [form] = Form.useForm();
  const [pwForm] = Form.useForm();

  const canCreate = level >= 2;
  const canDelete = level >= 3;

  useEffect(() => {
    accountApi.listCompanies('all').then(res => setCompanies(res.data.data || [])).catch(() => {});
  }, []);

  const loadData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await accountApi.search({
        email: searchEmail || undefined,
        cid: searchCid || undefined,
        page,
        size: pageSize,
      });
      const pageData = res.data.data;
      setAccounts(pageData.content || []);
      setTotal(pageData.totalElements);
    } catch {
      message.error('載入帳號資料失敗');
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, searchEmail, searchCid]);

  useEffect(() => { loadData(); }, [loadData]);

  const handleSearch = () => {
    setPage(0);
    loadData();
  };

  const handleCreate = () => {
    setEditing(null);
    form.resetFields();
    if (level <= 2 && user?.cid) {
      form.setFieldsValue({ cid: user.cid });
    }
    setDrawerOpen(true);
  };

  const handleEdit = (record: Account) => {
    setEditing(record);
    form.setFieldsValue(record);
    setDrawerOpen(true);
  };

  const handleDelete = (sn: number) => {
    Modal.confirm({
      title: '確認刪除',
      content: '確定要刪除此帳號？',
      okText: '刪除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await accountApi.delete(sn);
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
      if (editing) {
        const updateData: AccountUpdateRequest = {
          name: values.name,
          title: values.title,
          tel: values.tel,
          mobile: values.mobile,
          email2: values.email2,
          alevel: values.alevel,
          insurance: values.insurance,
          status: values.status,
        };
        await accountApi.update(editing.sn, updateData);
        message.success('更新成功');
      } else {
        const createData: AccountCreateRequest = {
          email: values.email,
          password: values.password,
          name: values.name,
          title: values.title,
          tel: values.tel,
          mobile: values.mobile,
          email2: values.email2,
          alevel: values.alevel,
          insurance: values.insurance,
          cid: values.cid,
        };
        await accountApi.create(createData);
        message.success('新增成功');
      }
      setDrawerOpen(false);
      loadData();
    } catch {
      message.error('儲存失敗');
    }
  };

  const handleChangePassword = (record: Account) => {
    setPasswordTarget(record);
    pwForm.resetFields();
    setPasswordModalOpen(true);
  };

  const handlePasswordSubmit = async () => {
    try {
      const values = await pwForm.validateFields();
      if (!passwordTarget) return;
      await accountApi.changePassword(passwordTarget.sn, values);
      message.success('密碼修改成功');
      setPasswordModalOpen(false);
    } catch {
      message.error('密碼修改失敗');
    }
  };

  const columns: ColumnsType<Account> = [
    { title: 'Email', dataIndex: 'email', ellipsis: true },
    { title: '姓名', dataIndex: 'name', width: 100 },
    { title: '公司代碼', dataIndex: 'cid', width: 100 },
    { title: '公司名稱', dataIndex: 'companyName', width: 150, ellipsis: true },
    {
      title: '等級', dataIndex: 'alevel', width: 100,
      render: (v: number) => <Tag color={LEVEL_COLORS[v]}>{LEVEL_LABELS[v] || v}</Tag>,
    },
    {
      title: '狀態', dataIndex: 'status', width: 70,
      render: (v: string) => <Tag color={v === 'Y' ? 'green' : 'red'}>{v === 'Y' ? '啟用' : '停用'}</Tag>,
    },
    { title: '最後登入', dataIndex: 'lastlogin', width: 160, ellipsis: true },
    {
      title: '操作', width: 200,
      render: (_, record) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => handleEdit(record)}>編輯</Button>
          <Button icon={<KeyOutlined />} size="small" onClick={() => handleChangePassword(record)}>密碼</Button>
          {canDelete && (
            <Button icon={<DeleteOutlined />} size="small" danger onClick={() => handleDelete(record.sn)} />
          )}
        </Space>
      ),
    },
  ];

  return (
    <>
      <Title level={4}>帳號管理</Title>

      <Space style={{ marginBottom: 16 }}>
        <Input
          placeholder="Email"
          value={searchEmail}
          onChange={e => setSearchEmail(e.target.value)}
          onPressEnter={handleSearch}
          style={{ width: 200 }}
        />
        <Input
          placeholder="公司代碼"
          value={searchCid}
          onChange={e => setSearchCid(e.target.value)}
          onPressEnter={handleSearch}
          style={{ width: 150 }}
        />
        <Button icon={<SearchOutlined />} onClick={handleSearch}>搜尋</Button>
        {canCreate && (
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>新增帳號</Button>
        )}
      </Space>

      <Table
        rowKey="sn"
        columns={columns}
        dataSource={accounts}
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

      <Drawer
        title={editing ? '編輯帳號' : '新增帳號'}
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        width={480}
        extra={
          <Space>
            <Button onClick={() => setDrawerOpen(false)}>取消</Button>
            <Button type="primary" onClick={handleSubmit}>儲存</Button>
          </Space>
        }
      >
        <Form form={form} layout="vertical">
          {!editing && (
            <>
              <Form.Item name="email" label="Email" rules={[
                { required: true, message: '請輸入 Email' },
                { type: 'email', message: '請輸入有效的 Email' },
              ]}>
                <Input />
              </Form.Item>
              <Form.Item name="password" label="密碼" rules={[{ required: true, message: '請輸入密碼' }]}>
                <Input.Password />
              </Form.Item>
            </>
          )}
          <Form.Item name="name" label="姓名" rules={[{ required: true, message: '請輸入姓名' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="title" label="職稱"><Input /></Form.Item>
          <Form.Item name="cid" label="公司代碼" rules={[{ required: !editing, message: '請選擇公司' }]}>
            <Select disabled={!!editing || level <= 2} showSearch optionFilterProp="label">
              {companies.map(c => (
                <Select.Option key={c.cid} value={c.cid} label={`${c.cid} - ${c.cname}`}>
                  {c.cid} - {c.cname}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="alevel" label="權限等級" rules={[{ required: true, message: '請選擇等級' }]}>
            <Select>
              {Object.entries(LEVEL_LABELS)
                .filter(([k]) => Number(k) <= level)
                .map(([k, v]) => (
                  <Select.Option key={k} value={Number(k)}>{v}</Select.Option>
                ))}
            </Select>
          </Form.Item>
          <Form.Item name="tel" label="電話"><Input /></Form.Item>
          <Form.Item name="mobile" label="手機"><Input /></Form.Item>
          <Form.Item name="email2" label="備用 Email"><Input /></Form.Item>
          <Form.Item name="insurance" label="保險"><Input /></Form.Item>
          {editing && (
            <Form.Item name="status" label="狀態">
              <Select>
                <Select.Option value="Y">啟用</Select.Option>
                <Select.Option value="N">停用</Select.Option>
              </Select>
            </Form.Item>
          )}
        </Form>
      </Drawer>

      <Modal
        title={`修改密碼 - ${passwordTarget?.email || ''}`}
        open={passwordModalOpen}
        onOk={handlePasswordSubmit}
        onCancel={() => setPasswordModalOpen(false)}
        okText="確認"
        cancelText="取消"
      >
        <Form form={pwForm} layout="vertical">
          <Form.Item name="oldPassword" label="目前密碼" rules={[{ required: true, message: '請輸入目前密碼' }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="newPassword" label="新密碼" rules={[{ required: true, message: '請輸入新密碼' }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="確認新密碼"
            rules={[
              { required: true, message: '請確認新密碼' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) return Promise.resolve();
                  return Promise.reject(new Error('兩次密碼不一致'));
                },
              }),
            ]}
          >
            <Input.Password />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
