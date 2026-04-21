import { useState } from "react";
import { Alert, Button, Card, Form, InputNumber, Space, Table, Typography } from "antd";
import { useApprovals } from "../../hooks/useApprovals";
import "../../styles/approval/approval.css";

const { Title } = Typography;

export default function ApprovalsPage({ token }) {
  const { data, error, load, approve } = useApprovals(token);
  const [loanApplicationId, setLoanApplicationId] = useState("");

  const handleApprove = async () => {
    await approve(Number(loanApplicationId));
    setLoanApplicationId("");
  };

  const columns = [
    { title: "Task ID", dataIndex: "id", key: "id", width: 100 },
    { title: "Loan Application ID", dataIndex: "loanApplicationId", key: "loanApplicationId", width: 170 },
    { title: "Assignee", dataIndex: "assignee", key: "assignee" },
    { title: "Status", dataIndex: "status", key: "status", width: 120 }
  ];

  return (
    <Space direction="vertical" size={16} style={{ width: "100%" }}>
      <Card>
        <Title level={3}>Approval Process</Title>
        <Form layout="vertical">
          <Form.Item label="Loan Application ID">
            <InputNumber
              min={1}
              style={{ width: 280 }}
              value={loanApplicationId ? Number(loanApplicationId) : null}
              onChange={(value) => setLoanApplicationId(String(value || ""))}
            />
          </Form.Item>
          <Space>
            <Button type="primary" onClick={handleApprove}>Approve</Button>
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
