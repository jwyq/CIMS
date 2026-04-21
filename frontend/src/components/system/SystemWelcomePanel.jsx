import { Card } from "antd";

export default function SystemWelcomePanel() {
  return (
    <Card className="system-welcome-card system-fill-card">
      <div className="system-welcome-content">
        <h2>欢迎使用系统管理</h2>
        <p>请选择左侧功能进行角色、资源和用户授权管理</p>
        <div className="system-welcome-illustration" />
      </div>
    </Card>
  );
}
