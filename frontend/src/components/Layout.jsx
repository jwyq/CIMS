import {
  BankOutlined,
  ExclamationCircleOutlined,
  FormOutlined,
  HomeOutlined,
  LogoutOutlined,
  SafetyCertificateOutlined,
  SearchOutlined,
  SettingOutlined,
  UserOutlined
} from "@ant-design/icons";
import { Button, Layout as AntLayout, Space, Tabs, Tooltip, Typography } from "antd";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import { usePermissions } from "../hooks/usePermissions";
import { getUiNavigation } from "../auth/uiNavigation";

const { Header, Content } = AntLayout;
const { Text } = Typography;

/** 新窗口直达客户/系统管理时，顶栏只显示当前模块页签，不出现「首页」 */
const TAB_SORT_ORDER = [
  "/home",
  "/customers",
  "/customers/corp-register",
  "/customers/corp-query",
  "/credit-approval",
  "/system-management",
  "/no-permission"
];

function sortTabKeys(keys) {
  return [...keys].sort((a, b) => {
    const ia = TAB_SORT_ORDER.indexOf(a);
    const ib = TAB_SORT_ORDER.indexOf(b);
    if (ia === -1 && ib === -1) return String(a).localeCompare(String(b));
    if (ia === -1) return 1;
    if (ib === -1) return -1;
    return ia - ib;
  });
}

function initialOpenTabsForPath(pathname) {
  if (pathname === "/customers/corp-register") {
    return sortTabKeys(["/customers", "/customers/corp-register"]);
  }
  if (pathname === "/customers/corp-query") {
    return sortTabKeys(["/customers", "/customers/corp-query"]);
  }
  if (pathname.startsWith("/customers")) {
    return ["/customers"];
  }
  if (pathname === "/system-management") {
    return [pathname];
  }
  return ["/home"];
}

export default function Layout({ user, onLogout }) {
  const navigate = useNavigate();
  const location = useLocation();
  const isWideWorkspace =
    location.pathname === "/system-management" || location.pathname.startsWith("/customers");
  const isCustomerWorkspace = location.pathname.startsWith("/customers");
  const { canAccessRoute } = usePermissions(user);
  const userDisplayName = user.username;
  const userCode = user.userId || user.id || "N/A";
  const iconMap = useMemo(() => ({
    "/home": <HomeOutlined />,
    "/customers": <BankOutlined />,
    "/customers/corp-register": <FormOutlined />,
    "/customers/corp-query": <SearchOutlined />,
    "/credit-approval": <SafetyCertificateOutlined />,
    "/system-management": <SettingOutlined />,
    "/no-permission": <ExclamationCircleOutlined />
  }), []);
  const routeTabMap = useMemo(() => {
    const { routes, routeOrder } = getUiNavigation(user);
    return (routeOrder || []).reduce((acc, path) => {
      const routeConfig = routes[path];
      if (routeConfig?.nav) {
        acc[path] = {
          label: routeConfig.nav.tabLabel,
          icon: iconMap[path]
        };
      }
      return acc;
    }, {});
  }, [iconMap, user]);

  /** 与 navigationCatalog.routeOrder 一致：首页入口放最前，避免「全部」与下方「首页」页签语义冲突 */
  const topNavItems = useMemo(
    () => [
      { path: "/home", label: "首页" },
      { path: "/customers", label: "客户管理" },
      { path: "/credit-approval", label: "授信审批" },
      { path: "/system-management", label: "系统管理" }
    ],
    []
  );

  const isTopNavActive = (path) => {
    if (path === "/customers") {
      return location.pathname.startsWith("/customers");
    }
    return location.pathname === path;
  };

  const [openTabs, setOpenTabs] = useState(() => initialOpenTabsForPath(location.pathname));

  const tabKeyForPath = useMemo(() => {
    if (location.pathname === "/customers/corp-register") {
      return "/customers/corp-register";
    }
    if (location.pathname === "/customers/corp-query") {
      return "/customers/corp-query";
    }
    if (location.pathname.startsWith("/customers")) {
      return "/customers";
    }
    return location.pathname;
  }, [location.pathname]);

  useEffect(() => {
    setOpenTabs((prev) => {
      const next = new Set(prev);
      const addIfAllowed = (key) => {
        if (routeTabMap[key] && canAccessRoute(key)) {
          next.add(key);
        }
      };

      if (location.pathname === "/customers/corp-register") {
        addIfAllowed("/customers");
        addIfAllowed("/customers/corp-register");
      } else if (location.pathname === "/customers/corp-query") {
        addIfAllowed("/customers");
        addIfAllowed("/customers/corp-query");
      } else if (routeTabMap[tabKeyForPath] && canAccessRoute(tabKeyForPath)) {
        addIfAllowed(tabKeyForPath);
      }

      return sortTabKeys([...next]);
    });
  }, [location.pathname, tabKeyForPath, routeTabMap, canAccessRoute]);

  const tabItems = openTabs
    .filter((path) => routeTabMap[path] && canAccessRoute(path))
    .map((path) => ({
      key: path,
      closable: path !== "/home",
      label: (
        <span className="header-tab-label">
          {routeTabMap[path].icon}
          <span>{routeTabMap[path].label}</span>
        </span>
      )
    }));

  const handleTabEdit = (targetKey, action) => {
    if (action !== "remove" || targetKey === "/home") {
      return;
    }
    const filtered = openTabs.filter((path) => path !== targetKey);
    setOpenTabs(filtered);

    if (location.pathname === "/customers/corp-register" && targetKey === "/customers/corp-register") {
      navigate("/customers");
      return;
    }
    if (location.pathname === "/customers/corp-query" && targetKey === "/customers/corp-query") {
      navigate("/customers");
      return;
    }
    if (location.pathname.startsWith("/customers") && targetKey === "/customers") {
      const nextPath = filtered[filtered.length - 1] || "/home";
      navigate(nextPath);
      return;
    }
    if (location.pathname === targetKey) {
      const nextPath = filtered[filtered.length - 1] || "/home";
      navigate(nextPath);
    }
  };

  return (
    <AntLayout className="app-shell">
      <Header className="app-header">
        <div className="header-top">
          <div className="header-top-left">
            <Space size="middle" align="center">
              <div className="header-logo-badge">
                <BankOutlined />
              </div>
              <div className="brand-block">
                <Text className="header-title">贷前管理系统</Text>
                <div className="header-subtitle">Credit Intake Management System</div>
              </div>
            </Space>
          </div>
          <nav className="header-center-nav" aria-label="主导航">
            {topNavItems.map((item) => {
              const allowed = canAccessRoute(item.path);
              const active = isTopNavActive(item.path);
              return (
                <button
                  key={item.path}
                  type="button"
                  className={`header-nav-link${active ? " header-nav-link--active" : ""}${!allowed ? " header-nav-link--disabled" : ""}`}
                  disabled={!allowed}
                  onClick={() => {
                    if (!allowed) return;
                    navigate(item.path);
                  }}
                >
                  {item.label}
                </button>
              );
            })}
          </nav>
          <div className="header-top-right">
            <Space size={12}>
              <div className="header-user-inline">
                <UserOutlined className="header-user-icon" />
                <span className="header-user-text">{userDisplayName} / {userCode}</span>
              </div>
              <Tooltip title="注销">
                <Button className="account-btn" icon={<LogoutOutlined />} onClick={onLogout} />
              </Tooltip>
            </Space>
          </div>
        </div>
        <div className="header-tabs-wrap">
          <Tabs
            className="header-tabs"
            type="editable-card"
            hideAdd
            items={tabItems}
            activeKey={routeTabMap[tabKeyForPath] ? tabKeyForPath : "/home"}
            onChange={(key) => navigate(key)}
            onEdit={handleTabEdit}
          />
        </div>
      </Header>
      <Content className={`app-content${isCustomerWorkspace ? " app-content--fill" : ""}`}>
        <div
          className={`app-content-inner ${isWideWorkspace ? "app-content-inner--system" : ""}${
            isCustomerWorkspace ? " app-content-inner--fill" : ""
          }`}
        >
          <Outlet />
        </div>
      </Content>
    </AntLayout>
  );
}
