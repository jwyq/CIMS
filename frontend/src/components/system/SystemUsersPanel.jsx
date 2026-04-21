import { useEffect, useMemo, useState } from "react";
import { Button, Card, Form, Input, Modal, Radio, Select, Space, Switch, Table, Tag, message } from "antd";
import QueryActionTableLayout from "./QueryActionTableLayout";

function roleNameMap(roles) {
  const m = new Map();
  (roles || []).forEach((r) => m.set(r.id, r.roleName || r.roleCode || String(r.id)));
  return m;
}

export default function SystemUsersPanel({
  users,
  roles,
  roleOptions,
  selectedUserRoleIds,
  onUserChange,
  onUserRoleIdsChange,
  savingUserRoles,
  onSaveUserRoles,
  updateUser
}) {
  const [editForm] = Form.useForm();
  const [idInput, setIdInput] = useState("");
  const [nameInput, setNameInput] = useState("");
  const [appliedId, setAppliedId] = useState("");
  const [appliedName, setAppliedName] = useState("");
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);
  const [authorizeOpen, setAuthorizeOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [savingUserEdit, setSavingUserEdit] = useState(false);

  const names = useMemo(() => roleNameMap(roles), [roles]);

  const filteredUsers = useMemo(() => {
    return (users || []).filter((u) => {
      const idQ = appliedId.trim();
      if (idQ) {
        const n = Number(idQ);
        if (!Number.isNaN(n)) {
          if (u.id !== n) return false;
        } else if (!String(u.id).includes(idQ)) {
          return false;
        }
      }
      const nameQ = appliedName.trim().toLowerCase();
      if (nameQ) {
        const un = (u.username || "").toLowerCase();
        const dn = (u.displayName || "").toLowerCase();
        if (!un.includes(nameQ) && !dn.includes(nameQ)) {
          return false;
        }
      }
      return true;
    });
  }, [users, appliedId, appliedName]);

  useEffect(() => {
    setSelectedRowKeys((keys) => {
      if (!keys.length) return keys;
      const exists = filteredUsers.some((u) => u.id === keys[0]);
      return exists ? keys : [];
    });
  }, [filteredUsers]);

  const runQuery = () => {
    setAppliedId(idInput);
    setAppliedName(nameInput);
  };

  const clearFilters = () => {
    setIdInput("");
    setNameInput("");
    setAppliedId("");
    setAppliedName("");
  };

  const selectedUser = useMemo(() => {
    if (!selectedRowKeys.length) return null;
    return users.find((u) => u.id === selectedRowKeys[0]) || null;
  }, [users, selectedRowKeys]);

  const openAuthorize = async () => {
    if (!selectedRowKeys.length) {
      Modal.warning({
        title: "请先选择用户",
        content: "在列表中点击一行以选中用户，再点击「授权角色」。"
      });
      return;
    }
    await onUserChange(selectedRowKeys[0]);
    setAuthorizeOpen(true);
  };

  const handleModalOk = async () => {
    const ok = await onSaveUserRoles();
    if (ok) {
      setAuthorizeOpen(false);
    }
  };

  const openEdit = () => {
    if (!selectedRowKeys.length) {
      Modal.warning({
        title: "请先选择用户",
        content: "在列表中点击一行以选中用户，再点击「修改用户」。"
      });
      return;
    }
    const u = users.find((x) => x.id === selectedRowKeys[0]);
    if (!u) {
      return;
    }
    editForm.setFieldsValue({
      username: u.username,
      displayName: u.displayName ?? "",
      enabled: u.enabled !== false
    });
    setEditOpen(true);
  };

  const handleEditOk = async () => {
    try {
      const values = await editForm.validateFields();
      const id = selectedRowKeys[0];
      setSavingUserEdit(true);
      const ok = await updateUser(id, {
        displayName: (values.displayName ?? "").trim(),
        enabled: values.enabled
      });
      if (ok) {
        message.success("修改用户成功");
        setEditOpen(false);
      }
    } catch {
      /* validation */
    } finally {
      setSavingUserEdit(false);
    }
  };

  const userColumns = [
    { title: "用户ID", dataIndex: "id", key: "id", width: 100 },
    { title: "用户名", dataIndex: "username", key: "username", width: 160 },
    {
      title: "显示名称",
      dataIndex: "displayName",
      key: "displayName",
      ellipsis: true,
      width: 160,
      render: (v) => (v ? v : <span style={{ color: "#999" }}>—</span>)
    },
    {
      title: "状态",
      dataIndex: "enabled",
      key: "enabled",
      width: 88,
      render: (v) => (
        <Tag color={v !== false ? "green" : "default"}>{v !== false ? "启用" : "禁用"}</Tag>
      )
    },
    {
      title: "已授权角色",
      dataIndex: "roleIds",
      key: "roleIds",
      ellipsis: true,
      render: (roleIds) => (
        <Space wrap size={[4, 4]}>
          {(roleIds || []).map((rid) => (
            <Tag key={rid}>{names.get(rid) || rid}</Tag>
          ))}
          {(!roleIds || !roleIds.length) && <span style={{ color: "#999" }}>—</span>}
        </Space>
      )
    }
  ];

  const filterForm = (
    <Form layout="inline" colon={false} className="qat-filter-form">
      <Form.Item label="用户ID">
        <Input
          allowClear
          placeholder="请输入"
          value={idInput}
          onChange={(e) => setIdInput(e.target.value)}
          onPressEnter={runQuery}
        />
      </Form.Item>
      <Form.Item label="用户名称">
        <Input
          allowClear
          placeholder="请输入用户名"
          value={nameInput}
          onChange={(e) => setNameInput(e.target.value)}
          onPressEnter={runQuery}
        />
      </Form.Item>
      <Form.Item className="qat-filter-actions">
        <Space>
          <Button type="primary" onClick={runQuery}>
            查询
          </Button>
          <Button onClick={clearFilters}>清空</Button>
        </Space>
      </Form.Item>
    </Form>
  );

  const actionBar = (
    <Space wrap size={[8, 8]}>
      <Button onClick={openEdit}>修改用户</Button>
      <Button type="primary" onClick={openAuthorize}>
        授权角色
      </Button>
    </Space>
  );

  return (
    <Card className="lc-roles-card system-fill-card" bordered={false}>
      <QueryActionTableLayout variant="lc" filter={filterForm} actions={actionBar}>
        <Table
          rowKey="id"
          size="small"
          columns={userColumns}
          dataSource={filteredUsers}
          onRow={(record) => ({
            onClick: () => {
              setSelectedRowKeys((prev) => (prev[0] === record.id ? [] : [record.id]));
            }
          })}
          rowSelection={{
            type: "radio",
            selectedRowKeys,
            onChange: (keys) => setSelectedRowKeys(keys),
            renderCell: (checked, record) => (
              <Radio
                checked={checked}
                onClick={(e) => {
                  e.stopPropagation();
                  setSelectedRowKeys((prev) => (prev[0] === record.id ? [] : [record.id]));
                }}
              />
            )
          }}
          pagination={{
            total: filteredUsers.length === 0 ? 1 : filteredUsers.length,
            pageSize: 10,
            showSizeChanger: true,
            pageSizeOptions: [10, 20, 50],
            showTotal: () => `共 ${filteredUsers.length} 条`,
            hideOnSinglePage: false
          }}
          scroll={{ x: "max-content" }}
        />
      </QueryActionTableLayout>

      <Modal
        title={
          selectedUser ? `修改用户 — ${selectedUser.username}（ID ${selectedUser.id}）` : "修改用户"
        }
        open={editOpen}
        onCancel={() => setEditOpen(false)}
        onOk={handleEditOk}
        okText="保存"
        confirmLoading={savingUserEdit}
        width={520}
        destroyOnClose
        afterClose={() => editForm.resetFields()}
      >
        <Form form={editForm} layout="vertical" className="system-user-edit-form">
          <Form.Item label="用户名" name="username">
            <Input disabled />
          </Form.Item>
          <Form.Item
            label="显示名称"
            name="displayName"
            rules={[{ max: 128, message: "最多 128 个字符" }]}
          >
            <Input allowClear placeholder="请输入显示名称" />
          </Form.Item>
          <Form.Item label="状态" name="enabled" valuePropName="checked">
            <Switch checkedChildren="启用" unCheckedChildren="禁用" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={
          selectedUser ? `授权角色 — ${selectedUser.username}（ID ${selectedUser.id}）` : "授权角色"
        }
        open={authorizeOpen}
        onCancel={() => setAuthorizeOpen(false)}
        onOk={handleModalOk}
        okText="保存"
        confirmLoading={savingUserRoles}
        width={560}
        destroyOnClose
      >
        <p style={{ marginBottom: 12, color: "rgba(0,0,0,0.55)" }}>选择该用户拥有的角色，保存后生效。</p>
        <Select
          mode="multiple"
          allowClear
          placeholder="选择角色"
          style={{ width: "100%" }}
          options={roleOptions}
          value={selectedUserRoleIds}
          onChange={onUserRoleIdsChange}
          optionFilterProp="label"
        />
      </Modal>
    </Card>
  );
}
