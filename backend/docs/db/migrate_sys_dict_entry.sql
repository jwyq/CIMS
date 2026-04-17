-- 已有库升级：sys_dict_entry 字典表（code ↔ 中文）
USE cims;

SET NAMES utf8mb4;

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
