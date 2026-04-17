-- 兼容说明：应用统一使用物理列名 customer_no（Java 属性 customerNo）。
-- 若你曾将列改名为 cims_customer_no，请执行下面语句改回 customer_no，否则 MyBatis 会报 Unknown column 'customer_no'。
-- 若当前库仍是 customer_no，则无需执行本文件。

USE cims;
SET NAMES utf8mb4;

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_migrate_cst_customer_no_column$$

CREATE PROCEDURE sp_migrate_cst_customer_no_column()
BEGIN
  DECLARE dbname VARCHAR(64);
  SET dbname = DATABASE();

  IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'cims_customer_no'
  ) AND NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = dbname AND TABLE_NAME = 'cst_customer' AND COLUMN_NAME = 'customer_no'
  ) THEN
    ALTER TABLE cst_customer CHANGE COLUMN cims_customer_no customer_no VARCHAR(64) NOT NULL COMMENT 'CIMS系统内客户编号';
  END IF;
END$$

DELIMITER ;

CALL sp_migrate_cst_customer_no_column();
DROP PROCEDURE IF EXISTS sp_migrate_cst_customer_no_column;
