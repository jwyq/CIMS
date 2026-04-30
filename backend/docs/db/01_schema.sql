-- CIMS RBAC + business schema (MySQL 8.x)
-- charset/collation
CREATE DATABASE IF NOT EXISTS cims
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE cims;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- Organization / Department
-- =========================
CREATE TABLE IF NOT EXISTS sys_org (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  org_code VARCHAR(64) NOT NULL UNIQUE,
  org_name VARCHAR(128) NOT NULL,
  parent_id BIGINT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_dept (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dept_code VARCHAR(64) NOT NULL UNIQUE,
  dept_name VARCHAR(128) NOT NULL,
  org_id BIGINT NOT NULL,
  parent_id BIGINT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_sys_dept_org FOREIGN KEY (org_id) REFERENCES sys_org(id)
) ENGINE=InnoDB;

-- =========
-- User/Role
-- =========
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  display_name VARCHAR(64) NOT NULL,
  phone VARCHAR(32) NULL,
  email VARCHAR(128) NULL,
  org_id BIGINT NULL,
  dept_id BIGINT NULL,
  status TINYINT NOT NULL DEFAULT 1,
  last_login_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_sys_user_org FOREIGN KEY (org_id) REFERENCES sys_org(id),
  CONSTRAINT fk_sys_user_dept FOREIGN KEY (dept_id) REFERENCES sys_dept(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_code VARCHAR(64) NOT NULL UNIQUE,
  role_name VARCHAR(64) NOT NULL,
  description VARCHAR(255) NULL,
  scope_type VARCHAR(32) NOT NULL DEFAULT 'SELF', -- ALL/ORG/DEPT/SELF/CUSTOM
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_role (user_id, role_id),
  CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB;

-- ===========================
-- Resource (MENU/PAGE/BUTTON/API)
-- ===========================
CREATE TABLE IF NOT EXISTS sys_resource (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NULL,
  resource_type VARCHAR(32) NOT NULL, -- MENU/PAGE/BUTTON/API
  resource_code VARCHAR(128) NOT NULL UNIQUE,
  resource_name VARCHAR(128) NOT NULL,
  route_path VARCHAR(255) NULL,
  component VARCHAR(255) NULL,
  http_method VARCHAR(16) NULL,
  api_path VARCHAR(255) NULL,
  icon VARCHAR(64) NULL,
  sort_no INT NOT NULL DEFAULT 0,
  visible TINYINT NOT NULL DEFAULT 1,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_resource_parent (parent_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_role_resource (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  resource_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_role_resource (role_id, resource_id),
  CONSTRAINT fk_role_res_role FOREIGN KEY (role_id) REFERENCES sys_role(id),
  CONSTRAINT fk_role_res_res FOREIGN KEY (resource_id) REFERENCES sys_resource(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_button_api_binding (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  button_resource_id BIGINT NOT NULL,
  api_resource_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_button_api (button_resource_id, api_resource_id),
  CONSTRAINT fk_button_api_button FOREIGN KEY (button_resource_id) REFERENCES sys_resource(id),
  CONSTRAINT fk_button_api_api FOREIGN KEY (api_resource_id) REFERENCES sys_resource(id)
) ENGINE=InnoDB;

-- ==========
-- Data scope
-- ==========
CREATE TABLE IF NOT EXISTS sys_role_data_scope (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL UNIQUE,
  scope_type VARCHAR(32) NOT NULL, -- ALL/ORG/DEPT/SELF/CUSTOM
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_role_scope_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_role_data_scope_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_id BIGINT NOT NULL,
  scope_item_type VARCHAR(16) NOT NULL, -- ORG/DEPT/USER
  scope_item_id BIGINT NOT NULL,
  UNIQUE KEY uk_scope_item (role_id, scope_item_type, scope_item_id),
  CONSTRAINT fk_scope_item_role FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB;

-- ==========
-- Code → 中文 映射（存 code，展示 label_zh；机构类用 sys_org）
-- ==========
CREATE TABLE IF NOT EXISTS sys_dict_entry (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dict_type VARCHAR(64) NOT NULL COMMENT '字典类型，如 ID_TYPE、CUSTOMER_STATUS',
  code VARCHAR(64) NOT NULL,
  label_zh VARCHAR(128) NOT NULL,
  sort_no INT NOT NULL DEFAULT 0,
  status TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_dict_type_code (dict_type, code),
  INDEX idx_dict_type_status (dict_type, status)
) ENGINE=InnoDB;

-- ============
-- Auth auditing
-- ============
CREATE TABLE IF NOT EXISTS sys_auth_audit_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  operator_user_id BIGINT NOT NULL,
  operation_type VARCHAR(64) NOT NULL,
  target_type VARCHAR(32) NOT NULL, -- ROLE/USER/RESOURCE
  target_id BIGINT NOT NULL,
  before_json JSON NULL,
  after_json JSON NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_auth_audit_created (created_at),
  CONSTRAINT fk_auth_audit_operator FOREIGN KEY (operator_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB;

-- ==============
-- Business tables
-- ==============
CREATE TABLE IF NOT EXISTS cst_customer (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_no VARCHAR(64) NOT NULL COMMENT 'CIMS系统内客户编号',
  name VARCHAR(64) NOT NULL COMMENT '客户名称',
  id_type VARCHAR(32) NULL COMMENT '证件类型编码',
  id_no VARCHAR(64) NOT NULL COMMENT '证件号码',
  id_valid_until DATE NULL,
  mobile VARCHAR(32) NOT NULL,
  email VARCHAR(128) NULL,
  marital_status VARCHAR(32) NULL,
  education_level VARCHAR(64) NULL,
  occupation VARCHAR(64) NULL,
  employer_name VARCHAR(128) NULL,
  annual_income DECIMAL(18,2) NULL,
  contact_address VARCHAR(255) NULL,
  risk_level VARCHAR(32) NULL,
  remark VARCHAR(500) NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
  manager_user_id BIGINT NULL COMMENT '主办客户经理 sys_user.id',
  org_id BIGINT NULL COMMENT '管理机构 sys_org.id',
  dept_id BIGINT NULL,
  created_by BIGINT NULL COMMENT '注册/创建操作人 sys_user.id',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_customer_no (customer_no),
  INDEX idx_customer_name (name),
  INDEX idx_customer_mobile (mobile),
  INDEX idx_customer_id_doc (id_type, id_no),
  CONSTRAINT fk_customer_manager FOREIGN KEY (manager_user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_customer_org FOREIGN KEY (org_id) REFERENCES sys_org(id),
  CONSTRAINT fk_customer_dept FOREIGN KEY (dept_id) REFERENCES sys_dept(id)
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS cst_customer_basic_info_history (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  change_type VARCHAR(64) NOT NULL,
  before_snapshot TEXT NULL,
  after_snapshot TEXT NULL,
  changed_by BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_customer_history_customer (customer_id),
  CONSTRAINT fk_customer_history_customer FOREIGN KEY (customer_id) REFERENCES cst_customer(id),
  CONSTRAINT fk_customer_history_user FOREIGN KEY (changed_by) REFERENCES sys_user(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS loan_application (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  app_no VARCHAR(64) NOT NULL UNIQUE,
  customer_id BIGINT NOT NULL,
  amount DECIMAL(18,2) NOT NULL,
  term_months INT NOT NULL,
  product_code VARCHAR(64) NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
  current_node VARCHAR(64) NULL,
  applicant_user_id BIGINT NOT NULL,
  org_id BIGINT NULL,
  dept_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_loan_status (status),
  INDEX idx_loan_customer (customer_id),
  CONSTRAINT fk_loan_customer FOREIGN KEY (customer_id) REFERENCES cst_customer(id),
  CONSTRAINT fk_loan_applicant FOREIGN KEY (applicant_user_id) REFERENCES sys_user(id),
  CONSTRAINT fk_loan_org FOREIGN KEY (org_id) REFERENCES sys_org(id),
  CONSTRAINT fk_loan_dept FOREIGN KEY (dept_id) REFERENCES sys_dept(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS approval_process (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  biz_type VARCHAR(32) NOT NULL, -- LOAN
  biz_id BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_process_biz (biz_type, biz_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS approval_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  process_id BIGINT NOT NULL,
  node_code VARCHAR(64) NOT NULL,
  assignee_user_id BIGINT NULL,
  assignee_role_code VARCHAR(64) NULL,
  action VARCHAR(32) NOT NULL DEFAULT 'PENDING', -- PENDING/APPROVED/REJECTED/RETURNED
  comment VARCHAR(500) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  handled_at DATETIME NULL,
  INDEX idx_task_process (process_id),
  INDEX idx_task_assignee_user (assignee_user_id),
  CONSTRAINT fk_task_process FOREIGN KEY (process_id) REFERENCES approval_process(id),
  CONSTRAINT fk_task_assignee_user FOREIGN KEY (assignee_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS approval_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  process_id BIGINT NOT NULL,
  task_id BIGINT NOT NULL,
  operator_user_id BIGINT NOT NULL,
  result VARCHAR(32) NOT NULL,
  comment VARCHAR(500) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_record_process (process_id),
  CONSTRAINT fk_record_process FOREIGN KEY (process_id) REFERENCES approval_process(id),
  CONSTRAINT fk_record_task FOREIGN KEY (task_id) REFERENCES approval_task(id),
  CONSTRAINT fk_record_operator FOREIGN KEY (operator_user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_notification (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(128) NOT NULL,
  content TEXT NOT NULL,
  type VARCHAR(32) NOT NULL, -- SYSTEM/APPROVAL/ALERT
  biz_type VARCHAR(32) NULL,
  biz_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS sys_notification_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  notification_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  read_flag TINYINT NOT NULL DEFAULT 0,
  read_at DATETIME NULL,
  UNIQUE KEY uk_notice_user (notification_id, user_id),
  CONSTRAINT fk_notice_user_notice FOREIGN KEY (notification_id) REFERENCES sys_notification(id),
  CONSTRAINT fk_notice_user_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
