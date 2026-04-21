import { useCallback, useEffect, useMemo, useState } from "react";
import { applyApiError, createAuthedApi } from "../api/client";

export function useSystemManagement(token) {
  const api = useMemo(() => createAuthedApi(token), [token]);
  const [roles, setRoles] = useState([]);
  const [users, setUsers] = useState([]);
  const [resourceTree, setResourceTree] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const post = useCallback(async (path, body) => {
    return api(path, {
      method: "POST",
      ...(body === undefined ? {} : { body: JSON.stringify(body) })
    });
  }, [api]);

  const loadAll = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [rolesData, usersData, resourcesData] = await Promise.all([
        post("/system/queryRoles"),
        post("/system/queryUsers"),
        post("/system/queryResourceTree")
      ]);
      setRoles(rolesData || []);
      setUsers(usersData || []);
      setResourceTree(resourcesData || []);
    } catch (e) {
      applyApiError(setError, e, "加载系统管理数据失败");
    } finally {
      setLoading(false);
    }
  }, [post]);

  /** 仅刷新资源树（打开资源授权前调用，避免整页重载） */
  const refreshResourceTree = useCallback(async () => {
    setError("");
    try {
      const resourcesData = await post("/system/queryResourceTree");
      setResourceTree(resourcesData || []);
    } catch (e) {
      applyApiError(setError, e, "刷新资源树失败");
    }
  }, [post]);

  const createRole = async (payload) => {
    try {
      await post("/system/createRole", payload);
      await loadAll();
      return true;
    } catch (e) {
      applyApiError(setError, e, "新增角色失败");
      return false;
    }
  };

  const updateRole = async (roleId, payload) => {
    try {
      await post("/system/updateRole", { roleId, ...payload });
      await loadAll();
      return true;
    } catch (e) {
      applyApiError(setError, e, "更新角色失败");
      return false;
    }
  };

  const deleteRole = async (roleId) => {
    try {
      await post("/system/deleteRole", { roleId });
      await loadAll();
      return true;
    } catch (e) {
      applyApiError(setError, e, "删除角色失败");
      return false;
    }
  };

  const loadRoleResourceIds = async (roleId) => {
    try {
      return await post("/system/queryRoleResources", { roleId });
    } catch (e) {
      applyApiError(setError, e, "加载角色资源失败");
      return [];
    }
  };

  const grantRoleResources = async (roleId, resourceIds) => {
    try {
      await post("/system/grantRoleResources", { roleId, resourceIds });
      return true;
    } catch (e) {
      applyApiError(setError, e, "角色资源授权失败");
      return false;
    }
  };

  const loadUserRoleIds = async (userId) => {
    try {
      return await post("/system/queryUserRoles", { userId });
    } catch (e) {
      applyApiError(setError, e, "加载用户角色失败");
      return [];
    }
  };

  const grantUserRoles = async (userId, roleIds) => {
    try {
      await post("/system/grantUserRoles", { userId, roleIds });
      await loadAll();
      return true;
    } catch (e) {
      applyApiError(setError, e, "用户角色授权失败");
      return false;
    }
  };

  const updateUser = async (userId, payload) => {
    try {
      await post("/system/updateUser", { userId, ...payload });
      await loadAll();
      return true;
    } catch (e) {
      applyApiError(setError, e, "修改用户失败");
      return false;
    }
  };

  useEffect(() => {
    loadAll();
  }, [loadAll]);

  return {
    roles,
    users,
    resourceTree,
    error,
    loading,
    loadAll,
    refreshResourceTree,
    createRole,
    updateRole,
    deleteRole,
    loadRoleResourceIds,
    grantRoleResources,
    loadUserRoleIds,
    grantUserRoles,
    updateUser
  };
}
