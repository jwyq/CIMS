import {
  EditOutlined,
  SearchOutlined,
  SolutionOutlined,
  TeamOutlined,
  SettingOutlined,
  BellOutlined,
  AppstoreOutlined,
  FileDoneOutlined
} from "@ant-design/icons";
import {
  Badge,
  Button,
  Card,
  Checkbox,
  Col,
  DatePicker,
  Empty,
  Input,
  List,
  Row,
  Select,
  Space,
  Tabs,
  Tag
} from "antd";
import { useNavigate } from "react-router-dom";
import { usePermissions } from "../hooks/usePermissions";
import { getUiNavigation } from "../auth/uiNavigation";

const { RangePicker } = DatePicker;
const MESSAGE_ITEMS = [];

const QUICK_SYSTEMS = [
  { key: "customer", title: "客户管理", icon: <TeamOutlined />, path: "/customers", openNewWindow: true },
  { key: "approval", title: "授信审批", icon: <SolutionOutlined />, path: "/credit-approval" },
  { key: "system", title: "系统管理", icon: <SettingOutlined />, path: "/system-management", openNewWindow: true },
  { key: "notice", title: "消息中心", icon: <BellOutlined />, path: "/home" },
  { key: "task", title: "任务办理", icon: <FileDoneOutlined />, path: "/credit-approval" },
  { key: "more", title: "更多应用", icon: <AppstoreOutlined />, path: "/home" }
];

export default function HomePage({ user }) {
  const navigate = useNavigate();
  const { canAccessRoute } = usePermissions(user);
  const allowedQuickSystems = QUICK_SYSTEMS.filter((item) => {
    const routeConfig = getUiNavigation(user).routes[item.path];
    return routeConfig && routeConfig.nav.quickAccess && canAccessRoute(item.path);
  });

  const gotoSystem = (item) => {
    if (item.openNewWindow) {
      const url = item.path.startsWith("http") ? item.path : `${window.location.origin}${item.path}`;
      window.open(url, "_blank", "noopener,noreferrer");
      return;
    }
    navigate(item.path);
  };

  return (
    <Row gutter={18} align="stretch" className="home-board">
      <Col span={8} className="home-board-col">
        <Card title="常用系统" className="home-left-panel dashboard-panel" bodyStyle={{ padding: 12 }}>
          <div className="quick-grid">
            {allowedQuickSystems.map((item) => (
              <button
                key={item.key}
                className="quick-item"
                onClick={() => gotoSystem(item)}
              >
                <span className="quick-item-icon">{item.icon}</span>
                <span className="quick-item-title">{item.title}</span>
              </button>
            ))}
          </div>
        </Card>
      </Col>
      <Col span={16} className="home-board-col">
        <Card className="message-panel dashboard-panel" bodyStyle={{ paddingTop: 10 }}>
          <Tabs className="message-tabs" defaultActiveKey="message" items={[
            { key: "todo", label: <span>我的待办 <Badge count={99} /></span> },
            { key: "task", label: <span>我的任务 <Badge count={99} /></span> },
            { key: "message", label: "我的消息" }
          ]}
          />
          <Space wrap className="home-toolbar compact-toolbar">
            <Button icon={<EditOutlined />}>编辑</Button>
            <Select defaultValue="all" style={{ width: 130 }} options={[{ label: "消息类型", value: "all" }, { label: "审批", value: "approval" }, { label: "通知", value: "notice" }]} />
            <Select defaultValue="unread" style={{ width: 100 }} options={[{ label: "未读", value: "unread" }, { label: "全部", value: "all" }]} />
            <RangePicker />
            <Input placeholder="消息内容模糊查询" prefix={<SearchOutlined />} style={{ width: 220 }} />
            <Button type="primary">查询</Button>
            <Button type="link">查看全部</Button>
          </Space>
          <div className="message-body">
            <div className="message-actions">
              <Checkbox>全选</Checkbox>
              <Button type="link">标记为已读</Button>
            </div>
            {MESSAGE_ITEMS.length > 0 ? (
              <List itemLayout="horizontal" dataSource={MESSAGE_ITEMS} renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta title={<Space><Tag color={item.status === "未读" ? "blue" : "default"}>{item.type}</Tag><span>{item.title}</span></Space>} description={item.time} />
                  <Tag color={item.status === "未读" ? "processing" : "default"}>{item.status}</Tag>
                </List.Item>
              )}
              />
            ) : <div className="message-empty-wrap"><Empty description="暂无数据" /></div>}
            <div className="message-footer">共0条记录，当前第1/0页</div>
          </div>
        </Card>
      </Col>
    </Row>
  );
}
