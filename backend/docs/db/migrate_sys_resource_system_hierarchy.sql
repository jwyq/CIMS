-- 已有库增量：系统管理资源拆为「控制台首页 / 角色管理 / 用户管理」三个 PAGE，按钮与 API 挂到对应节点下。
-- 可重复执行（依赖 id 稳定，与 02_seed_rbac.sql 一致）。
USE cims;

SET NAMES utf8mb4;

INSERT INTO sys_resource (id, parent_id, resource_type, resource_code, resource_name, route_path, sort_no, visible, status)
VALUES
  (144, 130, 'PAGE', 'page:system:roles', '角色管理', '/system-management', 2, 1, 1),
  (145, 130, 'PAGE', 'page:system:users', '用户管理', '/system-management', 3, 1, 1)
ON DUPLICATE KEY UPDATE resource_name = VALUES(resource_name), route_path = VALUES(route_path), parent_id = VALUES(parent_id), status = VALUES(status);

UPDATE sys_resource SET parent_id = 130, resource_name = '控制台首页', route_path = '/system-management', sort_no = 1 WHERE id = 143;

UPDATE sys_resource SET parent_id = 143 WHERE id = 220;
UPDATE sys_resource SET parent_id = 144 WHERE id IN (221, 222);
UPDATE sys_resource SET parent_id = 145 WHERE id = 223;

UPDATE sys_resource SET parent_id = 221 WHERE id IN (345, 346);
UPDATE sys_resource SET parent_id = 222 WHERE id = 347;
UPDATE sys_resource SET parent_id = 223 WHERE id = 348;

INSERT INTO sys_role_resource (role_id, resource_id)
VALUES (2, 144), (2, 145), (4, 144), (4, 145)
ON DUPLICATE KEY UPDATE resource_id = VALUES(resource_id);
