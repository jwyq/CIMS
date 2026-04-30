/**
 * 前端展示用：路由 / 子模块入口与「需具备的资源码」的对应关系。
 * 权限真相以后端为准：登录返回用户通过角色聚合得到的 resourceCodes；
 * 此处不包含业务规则下发，仅与后端 sys_resource 等资源编码保持一致，供 UI 与 resourceCodes 做交集判断。
 */
export const navigationCatalog = {
  routes: {
    "/home": {
      nav: { tabLabel: "首页", quickAccess: false },
      rule: {}
    },
    "/customers": {
      nav: { tabLabel: "客户管理", quickAccess: true },
      rule: {
        anyResourceCodes: ["menu:customer", "page:customer:list"]
      }
    },
    "/customers/corp-register": {
      nav: { tabLabel: "客户注册", quickAccess: false },
      rule: {
        anyResourceCodes: ["menu:customer", "page:customer:list"]
      }
    },
    "/customers/corp-query": {
      nav: { tabLabel: "客户查询", quickAccess: false },
      rule: {
        anyResourceCodes: ["menu:customer", "page:customer:list"]
      }
    },
    "/credit-approval": {
      nav: { tabLabel: "授信审批", quickAccess: true },
      rule: {
        anyResourceCodes: ["menu:approval", "page:approval:credit"]
      }
    },
    "/system-management": {
      nav: { tabLabel: "系统管理", quickAccess: true },
      rule: {
        anyResourceCodes: [
          "menu:system",
          "page:system:home",
          "page:system:roles",
          "page:system:users"
        ]
      }
    },
    "/no-permission": {
      nav: { tabLabel: "无权限", quickAccess: false },
      rule: {}
    }
  },
  creditApprovalPanels: {
    loanApply: {
      anyResourceCodes: ["btn:loan:create"]
    },
    approvalFlow: {
      anyResourceCodes: ["btn:approval:approve"]
    }
  },
  systemPanels: {
    dashboard: { anyResourceCodes: ["btn:system:dashboard:view"] },
    roles: { anyResourceCodes: ["btn:system:role:manage"] },
    /** 与「角色管理」内「资源授权」按钮对应；无角色管理权限时不单独作为菜单或入口 */
    permissions: { anyResourceCodes: ["btn:system:resource:grant"] },
    users: { anyResourceCodes: ["btn:system:userRole:grant"] }
  },
  routeOrder: ["/home", "/customers", "/customers/corp-register", "/customers/corp-query", "/credit-approval", "/system-management", "/no-permission"]
};
