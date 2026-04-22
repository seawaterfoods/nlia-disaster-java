import { useEffect, useState, useCallback } from 'react';
import {
  Table, Button, Modal, Form, Input, InputNumber, DatePicker, Tag, Space,
  Select, Card, message, Typography, Descriptions,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { disasterApi } from '../../api/disasters';
import { reportApi } from '../../api/reports';
import { configApi } from '../../api/config';
import { useAuth } from '../../hooks/useAuth';
import type { Disaster, ReportMain, ReportDetail, ReportDetailRequest, NdType, AddrType } from '../../types';

const { Title, Text } = Typography;
const { TextArea } = Input;

const STATUS_COLOR: Record<string, string> = { Y: 'green', N: 'red', X: 'default' };
const STATUS_LABEL: Record<string, string> = { Y: '有損失', N: '無損失', X: '未填報' };

const formatCurrency = (v: number | undefined) =>
  v != null ? v.toLocaleString('zh-TW', { minimumFractionDigits: 0 }) : '-';

export default function ReportPage() {
  const { user } = useAuth();
  const level = user?.level ?? 0;

  const [disasters, setDisasters] = useState<Disaster[]>([]);
  const [selectedDisaster, setSelectedDisaster] = useState<number | undefined>();
  const [mains, setMains] = useState<ReportMain[]>([]);
  const [selectedMain, setSelectedMain] = useState<ReportMain | null>(null);
  const [details, setDetails] = useState<ReportDetail[]>([]);
  const [loading, setLoading] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [editingDetail, setEditingDetail] = useState<ReportDetail | null>(null);
  const [products, setProducts] = useState<NdType[]>([]);
  const [cities, setCities] = useState<string[]>([]);
  const [areas, setAreas] = useState<AddrType[]>([]);
  const [form] = Form.useForm();

  useEffect(() => {
    Promise.all([
      disasterApi.list('all'),
      configApi.listProducts(),
      configApi.listCities(),
    ]).then(([dRes, pRes, cRes]) => {
      setDisasters(dRes.data.data || []);
      setProducts(pRes.data.data || []);
      setCities(cRes.data.data || []);
    }).catch(() => message.error('載入基礎資料失敗'));
  }, []);

  const loadMains = useCallback(async (ndsn: number) => {
    setLoading(true);
    try {
      if (level <= 2) {
        await reportApi.ensureMain(ndsn);
      }
      const res = await reportApi.listMains(ndsn);
      setMains(res.data.data || []);
    } catch {
      message.error('載入通報資料失敗');
    } finally {
      setLoading(false);
    }
  }, [level]);

  const loadDetails = useCallback(async (ndsn: number, cid: string) => {
    setDetailLoading(true);
    try {
      const res = await reportApi.listDetails(ndsn, cid);
      setDetails(res.data.data || []);
    } catch {
      message.error('載入明細失敗');
    } finally {
      setDetailLoading(false);
    }
  }, []);

  const handleDisasterChange = (ndsn: number) => {
    setSelectedDisaster(ndsn);
    setSelectedMain(null);
    setDetails([]);
    loadMains(ndsn);
  };

  const handleMainClick = (record: ReportMain) => {
    setSelectedMain(record);
    loadDetails(record.ndsn, record.cid);
  };

  const handleStatusUpdate = async (record: ReportMain, field: string, status: string) => {
    try {
      await reportApi.updateMainStatus(record.ndsn, record.cid, field, status);
      message.success('更新成功');
      if (selectedDisaster) loadMains(selectedDisaster);
    } catch {
      message.error('更新失敗');
    }
  };

  const handleCityChange = async (city: string) => {
    form.setFieldsValue({ area: undefined, zip: undefined });
    try {
      const res = await configApi.listAreas(city);
      setAreas(res.data.data || []);
    } catch {
      setAreas([]);
    }
  };

  const handleAreaChange = (areaName: string) => {
    const found = areas.find(a => a.aname === areaName);
    if (found) {
      form.setFieldsValue({ zip: found.asn });
    }
  };

  const handleCreateDetail = () => {
    if (!selectedMain) return;
    setEditingDetail(null);
    form.resetFields();
    form.setFieldsValue({ ndsn: selectedMain.ndsn, cid: selectedMain.cid });
    setDetailModalOpen(true);
  };

  const handleEditDetail = (record: ReportDetail) => {
    setEditingDetail(record);
    form.setFieldsValue({
      ...record,
      ndDate: record.ndDate ? dayjs(record.ndDate) : null,
    });
    if (record.city) {
      configApi.listAreas(record.city).then(res => setAreas(res.data.data || []));
    }
    setDetailModalOpen(true);
  };

  const handleDeleteDetail = (sn: number) => {
    Modal.confirm({
      title: '確認刪除',
      content: '確定要刪除此通報明細？',
      okText: '刪除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await reportApi.deleteDetail(sn);
          message.success('刪除成功');
          if (selectedMain) loadDetails(selectedMain.ndsn, selectedMain.cid);
        } catch {
          message.error('刪除失敗');
        }
      },
    });
  };

  const handleDetailSubmit = async () => {
    try {
      const values = await form.validateFields();
      const data: ReportDetailRequest = {
        ...values,
        ndDate: values.ndDate?.format('YYYY-MM-DD'),
      };
      if (editingDetail) {
        await reportApi.updateDetail(editingDetail.sn, data);
        message.success('更新成功');
      } else {
        await reportApi.createDetail(data);
        message.success('新增成功');
      }
      setDetailModalOpen(false);
      if (selectedMain) loadDetails(selectedMain.ndsn, selectedMain.cid);
    } catch {
      message.error('儲存失敗');
    }
  };

  const statusFields = ['nd1', 'nd2', 'nd3', 'nd4', 'nd5'] as const;

  const mainColumns: ColumnsType<ReportMain> = [
    { title: '公司代碼', dataIndex: 'cid', width: 100 },
    { title: '公司名稱', dataIndex: 'companyName', ellipsis: true },
    {
      title: '總狀態', dataIndex: 'nd', width: 80,
      render: (v: string) => <Tag color={STATUS_COLOR[v]}>{STATUS_LABEL[v] || v}</Tag>,
    },
    ...statusFields.map((f, i) => ({
      title: `通報${i + 1}`,
      dataIndex: f,
      width: 90,
      render: (v: string, record: ReportMain) => (
        level >= 3 ? (
          <Select
            size="small"
            value={v}
            style={{ width: 75 }}
            onChange={(val) => handleStatusUpdate(record, f, val)}
          >
            <Select.Option value="X">未填</Select.Option>
            <Select.Option value="Y">有</Select.Option>
            <Select.Option value="N">無</Select.Option>
          </Select>
        ) : (
          <Tag color={STATUS_COLOR[v]}>{STATUS_LABEL[v] || v}</Tag>
        )
      ),
    })),
    {
      title: '結案', dataIndex: 'closs', width: 70,
      render: (v: string) => <Tag color={v === 'Y' ? 'green' : 'default'}>{v === 'Y' ? '是' : '否'}</Tag>,
    },
    { title: '填報人', dataIndex: 'author', width: 100 },
  ];

  const detailColumns: ColumnsType<ReportDetail> = [
    { title: '保單號', dataIndex: 'bid', width: 100 },
    { title: '縣市', dataIndex: 'city', width: 80 },
    { title: '地區', dataIndex: 'area', width: 80 },
    { title: '險種', dataIndex: 'hname', width: 100 },
    { title: '產品', dataIndex: 'pname', width: 100, ellipsis: true },
    { title: '預估損失', dataIndex: 'preCost', width: 110, render: formatCurrency, align: 'right' },
    { title: '已賠付', dataIndex: 'commited', width: 110, render: formatCurrency, align: 'right' },
    { title: '待處理', dataIndex: 'pending', width: 110, render: formatCurrency, align: 'right' },
    { title: '預付', dataIndex: 'prepay', width: 100, render: formatCurrency, align: 'right' },
    {
      title: '結案', dataIndex: 'close', width: 60,
      render: (v: string) => <Tag color={v === 'Y' ? 'green' : 'default'}>{v === 'Y' ? '是' : '否'}</Tag>,
    },
    {
      title: '操作', width: 130,
      render: (_, record) => (
        <Space>
          <Button icon={<EditOutlined />} size="small" onClick={() => handleEditDetail(record)} />
          <Button icon={<DeleteOutlined />} size="small" danger onClick={() => handleDeleteDetail(record.sn)} />
        </Space>
      ),
    },
  ];

  const categories = [...new Set(products.map(p => p.hname))];

  return (
    <>
      <Title level={4}>災損通報</Title>

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

      {selectedDisaster && (
        <Card title="各公司通報狀態" style={{ marginBottom: 16 }}>
          <Table
            rowKey="sn"
            columns={mainColumns}
            dataSource={mains}
            loading={loading}
            pagination={false}
            size="small"
            onRow={(record) => ({
              onClick: () => handleMainClick(record),
              style: {
                cursor: 'pointer',
                background: selectedMain?.sn === record.sn ? '#e6f7ff' : undefined,
              },
            })}
          />
        </Card>
      )}

      {selectedMain && (
        <Card
          title={
            <Space>
              <span>{selectedMain.companyName} - 通報明細</span>
              <Button type="primary" icon={<PlusOutlined />} size="small" onClick={handleCreateDetail}>
                新增明細
              </Button>
            </Space>
          }
        >
          <Descriptions size="small" bordered column={4} style={{ marginBottom: 16 }}>
            <Descriptions.Item label="公司代碼">{selectedMain.cid}</Descriptions.Item>
            <Descriptions.Item label="公司名稱">{selectedMain.companyName}</Descriptions.Item>
            <Descriptions.Item label="填報人">{selectedMain.author}</Descriptions.Item>
            <Descriptions.Item label="更新時間">{selectedMain.udate || '-'}</Descriptions.Item>
          </Descriptions>
          <Table
            rowKey="sn"
            columns={detailColumns}
            dataSource={details}
            loading={detailLoading}
            pagination={false}
            size="small"
            scroll={{ x: 1100 }}
          />
        </Card>
      )}

      <Modal
        title={editingDetail ? '編輯通報明細' : '新增通報明細'}
        open={detailModalOpen}
        onOk={handleDetailSubmit}
        onCancel={() => setDetailModalOpen(false)}
        width={720}
        okText="儲存"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item name="ndsn" hidden><Input /></Form.Item>
          <Form.Item name="cid" hidden><Input /></Form.Item>
          <Space size="large">
            <Form.Item name="bid" label="保單號碼"><Input /></Form.Item>
            <Form.Item name="ndDate" label="災害日期"><DatePicker /></Form.Item>
            <Form.Item name="ndTypeSn" label="災害類型">
              <Select style={{ width: 160 }} allowClear>
                {products.map(p => (
                  <Select.Option key={p.sn} value={p.sn}>{p.pname}</Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Space>
          <Space size="large">
            <Form.Item name="city" label="縣市">
              <Select style={{ width: 140 }} allowClear onChange={handleCityChange} showSearch>
                {cities.map(c => <Select.Option key={c} value={c}>{c}</Select.Option>)}
              </Select>
            </Form.Item>
            <Form.Item name="area" label="地區">
              <Select style={{ width: 140 }} allowClear onChange={handleAreaChange} showSearch>
                {areas.map(a => <Select.Option key={a.asn} value={a.aname}>{a.aname}</Select.Option>)}
              </Select>
            </Form.Item>
            <Form.Item name="zip" label="郵遞區號"><Input style={{ width: 100 }} /></Form.Item>
          </Space>
          <Space size="large">
            <Form.Item name="hname" label="險種">
              <Select style={{ width: 160 }} allowClear>
                {categories.map(c => <Select.Option key={c} value={c}>{c}</Select.Option>)}
              </Select>
            </Form.Item>
            <Form.Item name="bname" label="業務類別"><Input /></Form.Item>
            <Form.Item name="pname" label="產品名稱"><Input /></Form.Item>
          </Space>
          <Space size="large">
            <Form.Item name="preCost" label="預估損失"><InputNumber style={{ width: 150 }} min={0} /></Form.Item>
            <Form.Item name="preInum" label="預估件數"><InputNumber style={{ width: 100 }} min={0} /></Form.Item>
            <Form.Item name="preDnum" label="預估死亡"><InputNumber style={{ width: 100 }} min={0} /></Form.Item>
          </Space>
          <Space size="large">
            <Form.Item name="commited" label="已賠付"><InputNumber style={{ width: 150 }} min={0} /></Form.Item>
            <Form.Item name="pending" label="待處理"><InputNumber style={{ width: 150 }} min={0} /></Form.Item>
            <Form.Item name="prepay" label="預付"><InputNumber style={{ width: 150 }} min={0} /></Form.Item>
          </Space>
          <Space size="large">
            <Form.Item name="close" label="結案狀態">
              <Select style={{ width: 100 }}>
                <Select.Option value="Y">是</Select.Option>
                <Select.Option value="N">否</Select.Option>
              </Select>
            </Form.Item>
          </Space>
          <Form.Item name="memo" label="備註">
            <TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
