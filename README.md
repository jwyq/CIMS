# CIMS

贷前管理系统基础框架（Spring Boot + React）。

## 项目结构

- `backend`: Spring Boot 后端（登录鉴权、权限、客户、贷款申请、审批、通知基础 API）java 1.8
- `frontend`: React 前端（登录页 + 各模块基础页面与路由）node 24

## 后端分层（4层架构）

- `controller`: 对外 REST API
- `service`: 业务编排与流程
- `repository`: 数据访问（MyBatis-Plus + MySQL）
- `domain`: 核心业务实体

## 当前已提供基础接口

- `POST /api/auth/login`
- `GET/POST /api/customers`
- `GET/POST /api/loans`
- `GET /api/approvals`
- `POST /api/approvals/approve`
- `GET /api/notifications`
- `GET/POST/PUT /api/system/roles`
- `GET /api/system/resources/tree`
- `GET/PUT /api/system/roles/{roleId}/resources`
- `GET /api/system/users`
- `GET/PUT /api/system/users/{userId}/roles`

## 数据库脚本（RBAC + 业务模块）

- 建表脚本：`backend/docs/db/01_schema.sql`
- 初始化脚本：`backend/docs/db/02_seed_rbac.sql`

执行顺序：

```bash
mysql -u root -p < backend/docs/db/01_schema.sql
mysql -u root -p < backend/docs/db/02_seed_rbac.sql
```

## 启动后端

```bash
cd backend
mvn spring-boot:run
```

默认地址：`http://localhost:8080`

默认数据库配置（可用环境变量覆盖）：
- `DB_URL=jdbc:mysql://localhost:3306/cims?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai`
- `DB_USERNAME=root`
- `DB_PASSWORD=123456`

## 启动前端

```bash
cd frontend
npm install
npm run dev
```

默认地址：`http://localhost:5173`

## 内置测试账号

- `admin / admin123`
- `manager / manager123`
- `officer / officer123`
