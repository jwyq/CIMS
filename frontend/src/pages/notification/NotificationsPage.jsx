import { Alert, Button, Card, Space, Table, Typography } from "antd";
import { useNotifications } from "../../hooks/useNotifications";
import "../../styles/notification/notification.css";

const { Title } = Typography;

export default function NotificationsPage({ token }) {
  const { data, error, load } = useNotifications(token);
  const columns = [
    { title: "Message ID", dataIndex: "id", key: "id", width: 120 },
    { title: "Recipient", dataIndex: "recipient", key: "recipient", width: 140 },
    { title: "Content", dataIndex: "content", key: "content" }
  ];

  return (
    <Card className="notification-card">
      <Space direction="vertical" size={16} style={{ width: "100%" }}>
        <Title level={3}>Notifications</Title>
        <Button onClick={load}>Refresh</Button>
        {error && <Alert className="form-alert" message={error} type="error" showIcon />}
        <Table rowKey="id" columns={columns} dataSource={data} pagination={{ pageSize: 8 }} />
      </Space>
    </Card>
  );
}
