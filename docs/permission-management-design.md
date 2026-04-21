# CIMS 权限管理设计文档

## 1. 目标与范围

本文档定义 CIMS 当前权限管理体系，覆盖：

- 数据库权限模型（RBAC 表结构与资源编码）
- 后端认证鉴权方案（JWT + 注解式权限校验）
- 前端权限控制方案（仅消费登录返回的 `resourceCodes`；路由与控件所需资源码由前端静态配置描述，与库表资源编码对齐）
- 权限码规范与迁移要求
- 联调与验收标准

目标是让权限能力具备一致性、可维护性和可扩展性，避免前后端判断漂移。

---

## 2. 权限模型总览

采用 RBAC（User-Role-Resource）模型：

- 用户（User）通过用户角色关系绑定多个角色
- 角色（Role）通过角色资源关系绑定多个资源权限
- 资源（Resource）按类型区分控制颗粒度

资源类型：

- `MENU`：系统导航可见性
- `PAGE`：页面访问权限
- `BUTTON`：页面内操作权限
- `API`：后端接口执行权限（最终安全边界）

---

## 3. 数据库表设计（权限相关）

以下为权限与组织相关核心表（建表参考 `backend/docs/db/01_schema.sql`）。

### 3.1 用户与角色

- `sys_user`
  - 关键字段：`id`, `username`, `password_hash`, `display_name`, `status`, `org_id`, `dept_id`
  - 说明：用户基础信息与登录主体

- `sys_role`
  - 关键字段：`id`, `role_code`, `role_name`, `scope_type`, `status`
  - 说明：角色定义，`scope_type` 支持数据范围扩展（ALL/ORG/DEPT/SELF/CUSTOM）

- `sys_user_role`
  - 关键字段：`user_id`, `role_id`（唯一约束）
  - 说明：用户-角色多对多关系

### 3.2 资源与授权

- `sys_resource`
  - 关键字段：`id`, `parent_id`, `resource_type`, `resource_code`, `resource_name`, `route_path`, `http_method`, `api_path`, `status`
  - 说明：统一资源中心，承载 MENU/PAGE/BUTTON/API

- `sys_role_resource`
  - 关键字段：`role_id`, `resource_id`（唯一约束）
  - 说明：角色-资源多对多关系

### 3.3 数据权限（预留）

- `sys_role_data_scope`
- `sys_role_data_scope_item`

当前主要完成功能权限控制，数据权限用于后续按机构/部门/本人扩展。

### 3.4 建议索引与约束

- `sys_user.username` 唯一
- `sys_role.role_code` 唯一
- `sys_resource.resource_code` 唯一
- `sys_user_role(user_id, role_id)` 唯一
- `sys_role_resource(role_id, resource_id)` 唯一

---

## 4. 权限码规范

统一编码格式：

- `{type}:{domain}:{action}`

示例：

- `menu:system`
- `page:system:home`
- `btn:system:role:manage`
- `api:system:role:create`

规范要求：

- 同一业务能力需有一致语义编码
- `BUTTON` 面向前端能力控制
- `API` 面向后端执行控制
- 禁止同义多码长期并存

---

## 5. 后端方案（认证 + 鉴权）

### 5.1 认证流程

1. 登录接口：`POST /api/auth/login`
2. 校验用户名密码（支持 BCrypt，兼容历史明文）
3. 查询用户角色与角色资源（仅用于在服务端解析出该用户当前的 **资源码列表**）
4. 生成 JWT（仅包含用户名、资源码、过期时间；**不嵌入角色**，也不使用 `ROLE_*` 参与鉴权）
5. 返回统一响应 `ApiResponse<LoginResponse>`

登录返回字段包含：

- `userId`
- `username`
- `displayName`
- `token`
- `resourceCodes`：用户通过角色关联得到的**全部资源码**（菜单/页面/按钮/API 类型在库中区分；JWT 与登录体只传码列表，不下发「规则」对象）

### 5.2 JWT 解析

- `JwtAuthenticationFilter` 解析 token
- 仅将 **资源码** 注入 Spring Security `authorities`（与 `@RequirePage` / `@RequireApi` 及 `hasAuthority("api:*")` 对齐）

### 5.3 鉴权策略（仅按钮绑定 API 参与权限校验）

采用“通用认证 + 写接口细粒度校验”：

- `SecurityConfig`：对“按钮绑定的 API”使用 `hasAuthority("api:*")`；其它接口仅要求 `authenticated()`
- 细粒度权限由注解 + AOP 执行：
  - `@RequireApi("api:xxx")`：写操作权限
  - 查询类接口不再要求 `@RequirePage`

核心组件：

- `backend/src/main/java/com/cims/backend/security/RequirePage.java`
- `backend/src/main/java/com/cims/backend/security/RequireApi.java`
- `backend/src/main/java/com/cims/backend/security/PermissionGuardAspect.java`

### 5.4 System 模块权限实践

控制器：`SystemManagementController`

- 查询接口：仅要求已登录（`authenticated()`）
- 写接口：使用 `@RequireApi("api:system:*")`（来源于按钮绑定的 API 码）

实现结果：

- 查询类接口不再受 `PAGE` 资源码限制
- 无写接口权限仍不能执行创建/更新/授权操作

### 5.5 统一返回与错误语义

- 成功：`{ code: 0, message: "success", data: ... }`
- 失败：统一错误码 + 统一 message
- 401/403/400/500 通过全局异常与安全入口统一输出

---

## 6. 前端方案（只消费资源码列表）

### 6.1 原则

- **后端不向下发「权限规则」对象**（不返回 path→条件 这类策略快照）；**只返回该用户当前拥有的 `resourceCodes`**（由角色-资源在服务端聚合）。
- **角色的控制能力**体现在：给角色勾选哪些资源（MENU/PAGE/BUTTON/API）→ 用户登录后 `resourceCodes` 即包含这些码 → 前端用其与本地**界面映射**求交，后端用 JWT 内资源码做接口鉴权。

### 6.2 路由注册

- `frontend/src/routes/AppRoutes.jsx`：路由入口；**某路径需要哪些资源码**由 `frontend/src/auth/navigationConfig.js` 描述（与 `sys_resource.resource_code` 命名一致，便于对照 seed）。
- `frontend/src/auth/uiNavigation.js`：提供 `getUiNavigation()`（与 `resourceCodes` 无耦合，仅导出上述静态结构）。
- `frontend/src/App.jsx`：应用壳；不再请求导航规则接口。

### 6.3 UI 判定与 `resourceCodes`

- `frontend/src/auth/canAccess.js`：用 `user.resourceCodes`（登录返回）与规则中的 `anyResourceCodes` / `allResourceCodes` 比较。
- `frontend/src/hooks/usePermissions.js`：`canAccessRoute` / `canAccessCreditPanel` / `canAccessSystemPanel` 基于 `navigationConfig` + `user.resourceCodes`。

### 6.4 路由守卫与导航控制

- `frontend/src/components/ProtectedRoute.jsx`
  - 路由进入统一权限判断，不通过跳无权限页

- `frontend/src/components/Layout.jsx`
  - 页签显示与打开统一走 `canAccessRoute`
  - 用户信息展示使用登录返回：`username/userId`

### 6.5 页面级权限控制

- `SystemManagementPage` 使用 `navigationConfig.systemPanels` 与 `user.resourceCodes` 控制子模块可见性
- 授权树支持菜单节点批量勾选子资源（父子可控）

---

## 7. 按钮与接口关系

原则：

- 前端显示与交互控制看 `BUTTON`
- 后端执行控制看 `API`（当前仅校验按钮绑定的 API）

当前系统管理按钮建议：

- `btn:system:dashboard:view`
- `btn:system:role:manage`
- `btn:system:resource:grant`
- `btn:system:userRole:grant`

后端仅对“按钮绑定 API”进行权限校验，其它查询类接口仅要求已登录。

---

## 8. 种子数据与迁移

种子文件：`backend/docs/db/02_seed_rbac.sql`

作用：

- 初始化角色、资源、角色资源关系
- 初始化当前参与权限校验的 API 资源码（以按钮绑定 API 为主）

每次权限码变更后，必须：

1. 执行 seed SQL 同步资源码
2. 若新增/调整路由或子模块与资源码的对应关系，同步更新 `frontend/src/auth/navigationConfig.js`（仅界面映射，非业务规则下发）
3. 重新登录获取新 `token` 与 `resourceCodes`
4. 验证 `navigationConfig` 中引用的资源码已在库中存在且角色可配置

---

## 9. 验收标准

### 9.1 一致性

- 路由、页签、菜单、按钮四层可见性一致
- 前端可见即接口可执行（在对应 API 权限内）
- 前端不可见的操作，后端仍可拦截（403）

### 9.2 场景验证

- 仅有 `PAGE`：可进页面、可查数据、不可写
- 无 `PAGE`：不可进入目标页面
- 有 `BUTTON` 但无对应绑定 `API`：前端可做限制，后端必须拒绝执行
- 不在按钮绑定范围内的查询接口：只验证登录态，不做资源码拦截

### 9.3 技术验证

- 前端构建通过：`npm run build`
- 后端测试通过：`mvn test`
- 登录返回包含完整用户展示字段：`username/userId`
- 登录返回仅包含资源能力：`resourceCodes`（不返回导航规则 DTO）

---

## 10. 后续演进建议

1. 将按钮到 API 的映射关系配置化（数据库维护）
2. 可选：由后端按 `sys_resource` 生成「路径→所需资源码」元数据接口，仍以资源为主，而非下发自定义规则表达式
3. 推广注解式鉴权到 customer/loan/approval/notification 模块
4. 落地数据权限（ORG/DEPT/SELF）并与查询层联动
5. 增加权限变更审计日志（谁在何时改了什么）

