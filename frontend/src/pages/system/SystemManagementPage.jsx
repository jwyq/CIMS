import { useEffect, useMemo, useState } from "react";
import { Alert, Form, Spin, Tag, Tooltip, message } from "antd";
import SystemConsoleLayout from "../../components/system/SystemConsoleLayout";
import SystemDashboardHero from "../../components/system/SystemDashboardHero";
import SystemRoleResourceModal from "../../components/system/SystemRoleResourceModal";
import SystemRoleEditModal from "../../components/system/SystemRoleEditModal";
import SystemRolesPanel from "../../components/system/SystemRolesPanel";
import SystemUsersPanel from "../../components/system/SystemUsersPanel";
import SystemWelcomePanel from "../../components/system/SystemWelcomePanel";
import { getUiNavigation } from "../../auth/uiNavigation";
import { usePermissions } from "../../hooks/usePermissions";
import { useSystemManagement } from "../../hooks/useSystemManagement";
import "../../styles/system/system.css";

/** 统计可配置权限点：菜单 / 页面 / 按钮（不含 API，接口挂在对应按钮下） */
function countResourceNodesExcludingApi(nodes) {
  if (!nodes?.length) return 0;
  let total = 0;
  const walk = (arr) => {
    arr.forEach((n) => {
      if (n.resourceType !== "API") {
        total += 1;
      }
      if (n.children?.length) walk(n.children);
    });
  };
  walk(nodes);
  return total;
}

export default function SystemManagementPage({ token, user }) {
  const [messageApi, messageContext] = message.useMessage();
  const {
    roles,
    users,
    resourceTree,
    error,
    loading,
    createRole,
    updateRole,
    deleteRole,
    loadRoleResourceIds,
    grantRoleResources,
    loadUserRoleIds,
    grantUserRoles,
    updateUser,
    refreshResourceTree
  } = useSystemManagement(token);
  const [createForm] = Form.useForm();
  const [editForm] = Form.useForm();
  const [selectedRoleId, setSelectedRoleId] = useState(null);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const [selectedUserRoleIds, setSelectedUserRoleIds] = useState([]);
  const [activePanel, setActivePanel] = useState("dashboard");
  const [checkedResourceIds, setCheckedResourceIds] = useState([]);
  const [editingRole, setEditingRole] = useState(null);
  const [savingRoleResources, setSavingRoleResources] = useState(false);
  const [savingUserRoles, setSavingUserRoles] = useState(false);
  const [resourceGrantOpen, setResourceGrantOpen] = useState(false);
  const { canAccessSystemPanel } = usePermissions(user);

  const roleOptions = roles.map((role) => ({ label: `${role.roleName} (${role.roleCode})`, value: role.id }));
  const allowedPanelKeys = useMemo(
    () => Object.keys(getUiNavigation(user).systemPanels || {}).filter((key) => canAccessSystemPanel(key)),
    [canAccessSystemPanel, user]
  );
  const menuItems = useMemo(
    () =>
      [
        { key: "dashboard", label: "控制台首页" },
        { key: "roles", label: "角色管理" },
        { key: "users", label: "用户管理" }
      ].filter((item) => allowedPanelKeys.includes(item.key)),
    [allowedPanelKeys]
  );

  /** 资源授权仅为「角色管理」内按钮：须同时具备角色管理与资源授权权限 */
  const canAccessResourceGrant =
    allowedPanelKeys.includes("roles") && allowedPanelKeys.includes("permissions");

  useEffect(() => {
    if (activePanel === "permissions") {
      const panelOrder = ["dashboard", "roles", "users"];
      const next = panelOrder.find((k) => allowedPanelKeys.includes(k)) || "dashboard";
      setActivePanel(next);
      return;
    }
    if (!allowedPanelKeys.includes(activePanel)) {
      const panelOrder = ["dashboard", "roles", "users"];
      const next = panelOrder.find((k) => allowedPanelKeys.includes(k)) || "dashboard";
      setActivePanel(next);
    }
  }, [activePanel, allowedPanelKeys]);

  const resourceNodeCount = useMemo(() => countResourceNodesExcludingApi(resourceTree), [resourceTree]);

  const treeData = useMemo(() => {
    const mapNode = (node) => {
      const apiCodes = node.bindingApiCodes || [];
      const apiHint = apiCodes.length ? `绑定 API：${apiCodes.join("、")}` : "";
      const title = (
        <span className="resource-node-title">
          <Tag
            color={
              node.resourceType === "MENU"
                ? "blue"
                : node.resourceType === "PAGE"
                  ? "cyan"
                  : node.resourceType === "BUTTON"
                    ? "purple"
                    : "gold"
            }
          >
            {node.resourceType}
          </Tag>
          <Tooltip title={apiHint || undefined} placement="topLeft">
            <span className="resource-node-name">{node.resourceName}</span>
          </Tooltip>
          <span className="resource-node-code">{node.resourceCode}</span>
        </span>
      );
      return {
        title,
        key: node.id,
        resourceType: node.resourceType,
        resourceCode: node.resourceCode,
        resourceName: node.resourceName,
        bindingApiCodes: apiCodes,
        children: (node.children || []).map(mapNode)
      };
    };
    return (resourceTree || []).map(mapNode);
  }, [resourceTree]);

  const handleCreateRole = async () => {
    try {
      const values = await createForm.validateFields();
      const ok = await createRole(values);
      if (ok) {
        messageApi.success("角色创建成功");
        createForm.resetFields();
      }
      return ok;
    } catch {
      return false;
    }
  };

  const handleDeleteRole = async (roleId) => {
    const ok = await deleteRole(roleId);
    if (ok) {
      messageApi.success("角色已删除");
      if (selectedRoleId === roleId) {
        setSelectedRoleId(null);
        setCheckedResourceIds([]);
        setResourceGrantOpen(false);
      }
    }
    return ok;
  };

  const handleUpdateRole = async () => {
    const values = await editForm.validateFields();
    const ok = await updateRole(editingRole.id, values);
    if (ok) {
      messageApi.success("角色更新成功");
      setEditingRole(null);
    }
  };

  const handleRoleChange = async (roleId) => {
    setSelectedRoleId(roleId);
    const ids = await loadRoleResourceIds(roleId);
    setCheckedResourceIds(ids || []);
  };

  const handleSaveRoleResources = async () => {
    if (!selectedRoleId) {
      messageApi.warning("请先选择角色");
      return false;
    }
    setSavingRoleResources(true);
    const ok = await grantRoleResources(selectedRoleId, checkedResourceIds);
    setSavingRoleResources(false);
    if (ok) {
      messageApi.success("角色资源授权成功");
    }
    return ok;
  };

  const handleSaveUserRoles = async () => {
    if (!selectedUserId) {
      messageApi.warning("请先选择用户");
      return false;
    }
    setSavingUserRoles(true);
    const ok = await grantUserRoles(selectedUserId, selectedUserRoleIds);
    setSavingUserRoles(false);
    if (ok) {
      messageApi.success("用户角色授权成功");
    }
    return ok;
  };

  const handleUserChange = async (userId) => {
    setSelectedUserId(userId);
    const roleIds = await loadUserRoleIds(userId);
    setSelectedUserRoleIds(roleIds || []);
  };

  const handleOpenResourceGrant = async (roleIdFromTable) => {
    if (!canAccessResourceGrant || roleIdFromTable == null) {
      return;
    }
    await refreshResourceTree();
    await handleRoleChange(roleIdFromTable);
    setResourceGrantOpen(true);
  };

  const resourceGrantModalTitle = useMemo(() => {
    if (!selectedRoleId) {
      return "资源授权";
    }
    const r = roles.find((x) => x.id === selectedRoleId);
    return r ? `资源授权 — ${r.roleName}（${r.roleCode}）` : "资源授权";
  }, [selectedRoleId, roles]);

  const saveRoleResourcesFromModal = async () => {
    const ok = await handleSaveRoleResources();
    if (ok) {
      setResourceGrantOpen(false);
    }
    return ok;
  };

  return (
    <div className="system-page">
      {messageContext}
      <Spin spinning={loading}>
        <SystemConsoleLayout
          activeKey={activePanel}
          onMenuChange={setActivePanel}
          menuTitle="系统管理"
          menuItems={menuItems}
          topSection={(
            activePanel === "dashboard" ? (
              <SystemDashboardHero
                roleCount={roles.length}
                userCount={users.length}
                resourceCount={resourceNodeCount}
              />
            ) : null
          )}
        >
          {error && <Alert className="form-alert" message={error} type="error" showIcon />}

          {activePanel === "dashboard" && (
            <SystemWelcomePanel />
          )}

          {activePanel === "roles" && (
            <SystemRolesPanel
              roles={roles}
              createForm={createForm}
              editForm={editForm}
              onCreateRole={handleCreateRole}
              onEditRole={setEditingRole}
              onDeleteRole={handleDeleteRole}
              showResourceGrant={canAccessResourceGrant}
              onOpenResourceGrant={handleOpenResourceGrant}
            />
          )}

          {activePanel === "users" && (
            <SystemUsersPanel
              users={users}
              roles={roles}
              roleOptions={roleOptions}
              selectedUserRoleIds={selectedUserRoleIds}
              onUserChange={handleUserChange}
              onUserRoleIdsChange={setSelectedUserRoleIds}
              savingUserRoles={savingUserRoles}
              onSaveUserRoles={handleSaveUserRoles}
              updateUser={updateUser}
            />
          )}
        </SystemConsoleLayout>
      </Spin>

      <SystemRoleEditModal
        form={editForm}
        editingRole={editingRole}
        onCancel={() => setEditingRole(null)}
        onSubmit={handleUpdateRole}
      />

      <SystemRoleResourceModal
        open={resourceGrantOpen}
        title={resourceGrantModalTitle}
        treeData={treeData}
        checkedResourceIds={checkedResourceIds}
        onCheckedResourceIdsChange={setCheckedResourceIds}
        saving={savingRoleResources}
        onCancel={() => setResourceGrantOpen(false)}
        onSave={saveRoleResourcesFromModal}
      />
    </div>
  );
}
