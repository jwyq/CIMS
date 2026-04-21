import { Card, Col, Row, Statistic, Typography } from "antd";

const { Title, Paragraph } = Typography;

export default function SystemDashboardHero({ roleCount, userCount, resourceCount }) {
  return (
    <Card className="system-hero-card">
      <Title level={4} className="system-hero-title">系统管理控制台</Title>
      <Paragraph type="secondary" className="system-hero-desc">
        统一维护角色、资源、用户授权关系。资源树按「菜单 → 页面 → 按钮 → 接口」分层，接口绑定在对应按钮下。
      </Paragraph>
      <Row gutter={14}>
        <Col span={8}>
          <div className="system-kpi-card">
            <Statistic title="角色数量" value={roleCount} />
          </div>
        </Col>
        <Col span={8}>
          <div className="system-kpi-card">
            <Statistic title="用户数量" value={userCount} />
          </div>
        </Col>
        <Col span={8}>
          <div className="system-kpi-card">
            <Statistic title="权限资源" value={resourceCount} suffix="项" />
            <div style={{ marginTop: 4, fontSize: 12, color: "rgba(0,0,0,0.45)" }}>
              菜单 / 页面 / 按钮（不含接口）
            </div>
          </div>
        </Col>
      </Row>
    </Card>
  );
}
