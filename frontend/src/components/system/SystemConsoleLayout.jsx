import { Card, Menu } from "antd";

export default function SystemConsoleLayout({
  activeKey,
  onMenuChange,
  menuItems,
  menuTitle,
  menuSubtitle,
  topSection,
  children
}) {
  return (
    <div className="system-layout">
      <div className="system-menu-shell">
        <Card className="system-menu-card">
          <div className="system-menu-header">
            <div className="system-menu-title">{menuTitle}</div>
            <div className="system-menu-subtitle">{menuSubtitle}</div>
          </div>
          <Menu
            mode="inline"
            selectedKeys={[activeKey]}
            onClick={({ key }) => onMenuChange(key)}
            items={menuItems}
          />
        </Card>
      </div>
      
      <div className="system-content-area">
        <div className="system-content-fixed">
          {topSection}
          {children}
        </div>
      </div>
    </div>
  );
}
