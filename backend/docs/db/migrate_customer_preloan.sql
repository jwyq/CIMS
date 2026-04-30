-- 已有库升级：客户贷前字段 + 基本信息修改历史表
-- MySQL 不支持通用的 ADD COLUMN IF NOT EXISTS，本脚本用存储过程按列名检测，可重复执行。
USE cims;

SET NAMES utf8mb4;

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_migrate_cst_customer_preloan$$

CREATE PROCEDURE sp_migrate_cst_customer_preloan()
BEGIN
  DECLARE dbname VARCHAR(64);
  SET dbname = DATABASE();

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'id_type'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN id_type VARCHAR(32) NULL AFTER name;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'id_valid_until'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN id_valid_until DATE NULL AFTER id_no;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'email'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN email VARCHAR(128) NULL AFTER mobile;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'marital_status'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN marital_status VARCHAR(32) NULL AFTER email;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'education_level'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN education_level VARCHAR(64) NULL AFTER marital_status;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'occupation'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN occupation VARCHAR(64) NULL AFTER education_level;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'employer_name'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN employer_name VARCHAR(128) NULL AFTER occupation;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'annual_income'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN annual_income DECIMAL(18,2) NULL AFTER employer_name;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'contact_address'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN contact_address VARCHAR(255) NULL AFTER annual_income;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'risk_level'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN risk_level VARCHAR(32) NULL AFTER contact_address;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'remark'
  ) THEN
    ALTER TABLE cst_customer ADD COLUMN remark VARCHAR(500) NULL AFTER risk_level;
  END IF;
END$$

DELIMITER ;

CALL sp_migrate_cst_customer_preloan();
DROP PROCEDURE IF EXISTS sp_migrate_cst_customer_preloan;

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
