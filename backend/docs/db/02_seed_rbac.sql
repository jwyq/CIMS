-- Seed data for RBAC and module resources
USE cims;

SET NAMES utf8mb4;

-- 客户表字段与历史表见 01_schema.sql；已有库升级请执行 migrate_customer_preloan.sql、migrate_customer_cims_no_column.sql、migrate_sys_dict_entry.sql（可重复执行）

-- =========
-- Base org/dept
-- =========
INSERT INTO sys_org (id, org_code, org_name, parent_id, status)
VALUES (1, 'HO', '总行', NULL, 1)
ON DUPLICATE KEY UPDATE org_name = VALUES(org_name), status = VALUES(status);

-- 字典：存 code，展示 label_zh（证件类型、客户状态等）
INSERT INTO sys_dict_entry (dict_type, code, label_zh, sort_no, status) VALUES
  ('ID_TYPE', 'UNIFIED_SOCIAL_CREDIT', '统一社会信用代码', 1, 1),
  ('ID_TYPE', 'BUSINESS_LICENSE', '营业执照', 2, 1),
  ('ID_TYPE', 'ORG_CODE', '组织机构代码', 3, 1),
  ('ID_TYPE', 'ID_CARD', '身份证', 10, 1),
  ('ID_TYPE', 'PASSPORT', '护照', 11, 1),
  ('ID_TYPE', 'HK_MO_PERMIT', '港澳居民来往内地通行证', 12, 1),
  ('ID_TYPE', 'TW_PERMIT', '台湾居民来往大陆通行证', 13, 1),
  ('CUSTOMER_STATUS', 'ACTIVE', '正常', 1, 1),
  ('CUSTOMER_STATUS', 'INACTIVE', '停用', 2, 1),
  ('CUSTOMER_STATUS', 'FROZEN', '冻结', 3, 1),
  ('COUNTRY_REGION', 'CN', '中国', 1, 1),
  ('COUNTRY_REGION', 'HK', '中国香港', 2, 1),
  ('COUNTRY_REGION', 'MO', '中国澳门', 3, 1),
  ('COUNTRY_REGION', 'SG', '新加坡', 4, 1),
  ('COUNTRY_REGION', 'US', '美国', 5, 1),
  ('ID_COUNTRY_REGION', 'CN', '中国', 1, 1),
  ('ID_COUNTRY_REGION', 'HK', '中国香港', 2, 1),
  ('ID_COUNTRY_REGION', 'MO', '中国澳门', 3, 1),
  ('ID_COUNTRY_REGION', 'SG', '新加坡', 4, 1),
  ('ID_COUNTRY_REGION', 'US', '美国', 5, 1)
ON DUPLICATE KEY UPDATE label_zh = VALUES(label_zh), sort_no = VALUES(sort_no), status = VALUES(status);

INSERT INTO sys_dept (id, dept_code, dept_name, org_id, parent_id, status)
VALUES
  (1, 'RISK', 'Risk Dept', 1, NULL, 1),
  (2, 'BIZ', 'Business Dept', 1, NULL, 1),
  (3, 'OPS', 'Ops Dept', 1, NULL, 1)
ON DUPLICATE KEY UPDATE dept_name = VALUES(dept_name), status = VALUES(status);

-- =====
-- Roles
-- =====
INSERT INTO sys_role (id, role_code, role_name, description, scope_type, status)
VALUES
  (1, 'ADMIN', '系统管理员', '全局管理权限', 'ALL', 1),
  (2, 'MANAGER', '业务经理', '客户维护、审批和系统管理', 'DEPT', 1),
  (3, 'OFFICER', '客户经理', '客户维护与授信申请', 'DEPT', 1),
  (4, 'OPS_MANAGER', '运营经理', '通知与系统运营配置', 'ORG', 1)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), scope_type = VALUES(scope_type), status = VALUES(status);

INSERT INTO sys_user (id, username, password_hash, display_name, org_id, dept_id, status)
VALUES
  (1, 'admin', 'admin123', '系统管理员', 1, 1, 1),
  (2, 'manager', 'manager123', '业务经理', 1, 1, 1),
  (3, 'officer', 'officer123', '客户经理', 1, 2, 1)
ON DUPLICATE KEY UPDATE display_name = VALUES(display_name), status = VALUES(status);

INSERT INTO sys_user_role (user_id, role_id)
VALUES
  (1, 1),
  (2, 2),
  (3, 3)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

INSERT INTO sys_resource (
  id, parent_id, resource_type, resource_code, resource_name, route_path, icon, sort_no, visible, status
) VALUES
  (100, NULL, 'MENU', 'menu:home', '首页', '/home', 'HomeOutlined', 1, 1, 1),
  (110, NULL, 'MENU', 'menu:customer', '客户管理', '/customers', 'BankOutlined', 2, 1, 1),
  (120, NULL, 'MENU', 'menu:approval', '授信审批', '/credit-approval', 'SafetyCertificateOutlined', 3, 1, 1),
  (130, NULL, 'MENU', 'menu:system', '系统管理', '/system-management', 'SettingOutlined', 4, 1, 1)
ON DUPLICATE KEY UPDATE resource_name = VALUES(resource_name), route_path = VALUES(route_path), status = VALUES(status);

INSERT INTO sys_resource (
  id, parent_id, resource_type, resource_code, resource_name, route_path, sort_no, visible, status
) VALUES
  (140, 100, 'PAGE', 'page:home:index', '首页页面', '/home', 1, 1, 1),
  (141, 110, 'PAGE', 'page:customer:list', '客户管理页面', '/customers', 1, 1, 1),
  (142, 120, 'PAGE', 'page:approval:credit', '授信审批页面', '/credit-approval', 1, 1, 1),
  (143, 130, 'PAGE', 'page:system:home', '控制台首页', '/system-management', 1, 1, 1),
  (144, 130, 'PAGE', 'page:system:roles', '角色管理', '/system-management', 2, 1, 1),
  (145, 130, 'PAGE', 'page:system:users', '用户管理', '/system-management', 3, 1, 1)
ON DUPLICATE KEY UPDATE resource_name = VALUES(resource_name), route_path = VALUES(route_path), parent_id = VALUES(parent_id), status = VALUES(status);

INSERT INTO sys_resource (
  id, parent_id, resource_type, resource_code, resource_name, sort_no, visible, status
) VALUES
  (200, 110, 'BUTTON', 'btn:customer:create', '新增客户', 1, 1, 1),
  (201, 110, 'BUTTON', 'btn:customer:view', '查看客户', 2, 1, 1),
  (202, 110, 'BUTTON', 'btn:customer:update', '修改客户', 3, 1, 1),
  (210, 120, 'BUTTON', 'btn:loan:create', '创建贷款申请', 1, 1, 1),
  (211, 120, 'BUTTON', 'btn:approval:approve', '审批通过', 2, 1, 1),
  (220, 143, 'BUTTON', 'btn:system:dashboard:view', '控制台首页', 1, 1, 1),
  (221, 144, 'BUTTON', 'btn:system:role:manage', '角色管理', 1, 1, 1),
  (222, 144, 'BUTTON', 'btn:system:resource:grant', '资源授权', 2, 1, 1),
  (223, 145, 'BUTTON', 'btn:system:userRole:grant', '用户管理', 1, 1, 1)
ON DUPLICATE KEY UPDATE resource_code = VALUES(resource_code), resource_name = VALUES(resource_name), parent_id = VALUES(parent_id), status = VALUES(status);

DELETE FROM sys_button_api_binding WHERE api_resource_id IN (310, 320, 330, 331, 340, 341, 342, 343, 344);
DELETE FROM sys_role_resource WHERE resource_id IN (310, 320, 330, 331, 340, 341, 342, 343, 344);
DELETE FROM sys_resource WHERE id IN (310, 320, 330, 331, 340, 341, 342, 343, 344);

INSERT INTO sys_resource (
  id, parent_id, resource_type, resource_code, resource_name, http_method, api_path, sort_no, visible, status
) VALUES
  (300, 110, 'API', 'api:customer:list', '客户列表', 'GET', '/api/customers', 1, 0, 1),
  (301, 110, 'API', 'api:customer:create', '客户注册API', 'POST', '/api/customers/register', 2, 0, 1),
  (302, 110, 'API', 'api:customer:update', '客户修改API', 'PUT', '/api/customers/{id}/basic-info', 3, 0, 1),
  (303, 110, 'API', 'api:customer:detail', '客户详情', 'GET', '/api/customers/{id}', 4, 0, 1),
  (304, 110, 'API', 'api:customer:history', '客户基本信息历史', 'GET', '/api/customers/{id}/basic-info-history', 5, 0, 1),
  (311, 120, 'API', 'api:loan:create', '创建贷款API', 'POST', '/api/loans', 2, 0, 1),
  (321, 120, 'API', 'api:approval:approve', '审批动作API', 'POST', '/api/approvals/approve', 4, 0, 1),
  (345, 221, 'API', 'api:system:role:create', '创建角色API', 'POST', '/api/system/createRole', 1, 0, 1),
  (346, 221, 'API', 'api:system:role:update', '更新或删除角色API', 'POST', '/api/system/updateRole', 2, 0, 1),
  (347, 222, 'API', 'api:system:role:grant', '角色资源授权API', 'POST', '/api/system/grantRoleResources', 1, 0, 1),
  (348, 223, 'API', 'api:system:user:grant', '用户角色授权与资料更新API', 'POST', '/api/system/grantUserRoles', 1, 0, 1)
ON DUPLICATE KEY UPDATE resource_name = VALUES(resource_name), api_path = VALUES(api_path), parent_id = VALUES(parent_id), status = VALUES(status);

INSERT INTO sys_button_api_binding (button_resource_id, api_resource_id)
VALUES
  (200, 301),
  (201, 300),
  (201, 303),
  (201, 304),
  (202, 302),
  (210, 311),
  (211, 321),
  (221, 345), (221, 346),
  (222, 347),
  (223, 348)
ON DUPLICATE KEY UPDATE api_resource_id = VALUES(api_resource_id);

INSERT INTO sys_role_resource (role_id, resource_id)
SELECT 1, id FROM sys_resource
ON DUPLICATE KEY UPDATE resource_id = VALUES(resource_id);

INSERT INTO sys_role_resource (role_id, resource_id)
VALUES
  (2, 100), (2, 110), (2, 120), (2, 130),
  (2, 140), (2, 141), (2, 142), (2, 143), (2, 144), (2, 145),
  (2, 200), (2, 201), (2, 202), (2, 210), (2, 211), (2, 220), (2, 221), (2, 222), (2, 223),
  (2, 300), (2, 301), (2, 302), (2, 303), (2, 304), (2, 311), (2, 321),
  (2, 345), (2, 346), (2, 347), (2, 348)
ON DUPLICATE KEY UPDATE resource_id = VALUES(resource_id);

INSERT INTO sys_role_resource (role_id, resource_id)
VALUES
  (3, 100), (3, 110), (3, 120),
  (3, 140), (3, 141), (3, 142),
  (3, 200), (3, 201), (3, 202), (3, 210),
  (3, 300), (3, 301), (3, 302), (3, 303), (3, 304), (3, 311)
ON DUPLICATE KEY UPDATE resource_id = VALUES(resource_id);

INSERT INTO sys_role_resource (role_id, resource_id)
VALUES
  (4, 100), (4, 130),
  (4, 140), (4, 143), (4, 144), (4, 145),
  (4, 220), (4, 221), (4, 222), (4, 223),
  (4, 345), (4, 346), (4, 347), (4, 348)
ON DUPLICATE KEY UPDATE resource_id = VALUES(resource_id);

INSERT INTO sys_role_data_scope (role_id, scope_type)
VALUES
  (1, 'ALL'),
  (2, 'DEPT'),
  (3, 'DEPT'),
  (4, 'ORG')
ON DUPLICATE KEY UPDATE scope_type = VALUES(scope_type);
