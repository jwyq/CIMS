import { useState } from "react";
import { Alert, Button, Card, Form, InputNumber, Space, Table, Typography } from "antd";
import { useLoans } from "../../hooks/useLoans";
import "../../styles/approval/approval.css";

const { Title } = Typography;

export default function LoansPage({ token }) {
  const { data, error, load, create } = useLoans(token);
  const [form, setForm] = useState({ customerId: "1", amount: "10000", termMonths: "12" });

  const handleCreate = async () => {
    await create({
      customerId: Number(form.customerId),
      amount: Number(form.amount),
      termMonths: Number(form.termMonths)
    });
  };

  const columns = [
    { title: "Loan ID", dataIndex: "id", key: "id", width: 100 },
    { title: "Customer ID", dataIndex: "customerId", key: "customerId", width: 120 },
    { title: "Amount", dataIndex: "amount", key: "amount" },
    { title: "Term (Months)", dataIndex: "termMonths", key: "termMonths", width: 140 },
    { title: "Status", dataIndex: "status", key: "status", width: 120 }
  ];

  return (
    <Space direction="vertical" size={16} style={{ width: "100%" }}>
      <Card>
        <Title level={3}>Loan Applications</Title>
        <Form layout="vertical">
          <div className="approval-form-grid">
            <Form.Item label="Customer ID">
              <InputNumber
                min={1}
                style={{ width: "100%" }}
                value={Number(form.customerId)}
                onChange={(value) => setForm({ ...form, customerId: String(value || 1) })}
              />
            </Form.Item>
            <Form.Item label="Amount">
              <InputNumber
                min={1}
                style={{ width: "100%" }}
                value={Number(form.amount)}
                onChange={(value) => setForm({ ...form, amount: String(value || 1) })}
              />
            </Form.Item>
            <Form.Item label="Term Months">
              <InputNumber
                min={1}
                style={{ width: "100%" }}
                value={Number(form.termMonths)}
                onChange={(value) => setForm({ ...form, termMonths: String(value || 1) })}
              />
            </Form.Item>
          </div>
          <Space>
            <Button type="primary" onClick={handleCreate}>Create Loan</Button>
            <Button onClick={load}>Refresh</Button>
          </Space>
        </Form>
        {error && <Alert className="form-alert" message={error} type="error" showIcon />}
      </Card>
      <Card>
        <Table rowKey="id" columns={columns} dataSource={data} pagination={{ pageSize: 6 }} />
      </Card>
    </Space>
  );
}
