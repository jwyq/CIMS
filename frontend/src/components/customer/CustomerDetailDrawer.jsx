import { Button, Card, Col, Descriptions, Drawer, Form, Input, Row, Select, Table } from "antd";
import { CUSTOMER_STATUS_OPTIONS, getCustomerIdTypeLabel } from "../../pages/customer/customerConstants";

export default function CustomerDetailDrawer({
  open,
  onClose,
  selected,
  history,
  editForm,
  onSave
}) {
  return (
    <Drawer
      title="客户详情 · 修改 · 变更历史"
      width={880}
      open={open}
      onClose={onClose}
      destroyOnClose
      className="customer-detail-drawer"
    >
      {selected ? (
        <>
          <Descriptions bordered size="small" column={2} className="customer-detail-desc">
            <Descriptions.Item label="CIMS客户编号">{selected.customerNo}</Descriptions.Item>
            <Descriptions.Item label="客户名称">{selected.name}</Descriptions.Item>
            <Descriptions.Item label="管理机构">{selected.mgmtOrgName || "—"}</Descriptions.Item>
            <Descriptions.Item label="主办客户经理">{selected.managerDisplayName || "—"}</Descriptions.Item>
            <Descriptions.Item label="证件类型">
              {selected.idTypeLabelZh || getCustomerIdTypeLabel(selected.idType)}
            </Descriptions.Item>
            <Descriptions.Item label="证件号码">{selected.idNo}</Descriptions.Item>
            <Descriptions.Item label="手机号">{selected.mobile}</Descriptions.Item>
            <Descriptions.Item label="状态">{selected.status}</Descriptions.Item>
            <Descriptions.Item label="风险等级">{selected.riskLevel || "—"}</Descriptions.Item>
          </Descriptions>
          <Row gutter={[16, 16]}>
            <Col xs={24} lg={12}>
              <Card size="small" title="基本信息修改" className="customer-detail-card">
                <Form form={editForm} layout="vertical" requiredMark={false}>
                  <Form.Item label="姓名" name="name" rules={[{ required: true, message: "请输入姓名" }]}>
                    <Input />
                  </Form.Item>
                  <Form.Item label="手机号" name="mobile">
                    <Input />
                  </Form.Item>
                  <Form.Item label="邮箱" name="email">
                    <Input />
                  </Form.Item>
                  <Form.Item label="地址" name="contactAddress">
                    <Input />
                  </Form.Item>
                  <Form.Item label="状态" name="status">
                    <Select options={CUSTOMER_STATUS_OPTIONS.map((v) => ({ label: v, value: v }))} />
                  </Form.Item>
                  <Form.Item>
                    <Button type="primary" onClick={onSave}>
                      保存修改
                    </Button>
                  </Form.Item>
                </Form>
              </Card>
            </Col>
            <Col xs={24} lg={12}>
              <Card size="small" title="基本信息变更历史" className="customer-detail-card">
                <Table
                  rowKey="id"
                  size="small"
                  pagination={false}
                  dataSource={history}
                  scroll={{ y: 360 }}
                  columns={[
                    { title: "类型", dataIndex: "changeType", key: "changeType", width: 120 },
                    { title: "变更前", dataIndex: "beforeSnapshot", key: "beforeSnapshot", ellipsis: true },
                    { title: "变更后", dataIndex: "afterSnapshot", key: "afterSnapshot", ellipsis: true }
                  ]}
                />
              </Card>
            </Col>
          </Row>
        </>
      ) : null}
    </Drawer>
  );
}
