import { useState } from "react";
import { Button, Card, Form, Input, Typography, Alert } from "antd";
import { request } from "../api/client";

const { Title, Text } = Typography;

export default function LoginPage({ onLogin }) {
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("admin123");
  const [error, setError] = useState("");

  const handleSubmit = async () => {
    setError("");
    try {
      const data = await request("/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });
      onLogin(data);
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div className="login-wrap">
      <Card className="login-card">
        <Title level={2} className="login-title">CIMS</Title>
        <Text className="login-subtitle">Loan Origination Control Panel</Text>
        <Form layout="vertical" onFinish={handleSubmit}>
          <Form.Item label="Username">
            <Input value={username} onChange={(e) => setUsername(e.target.value)} />
          </Form.Item>
          <Form.Item label="Password">
            <Input.Password value={password} onChange={(e) => setPassword(e.target.value)} />
          </Form.Item>
          <Button type="primary" htmlType="submit" block>
            Sign in
          </Button>
        </Form>
        {error && <Alert className="form-alert" message={error} type="error" showIcon />}
      </Card>
    </div>
  );
}
