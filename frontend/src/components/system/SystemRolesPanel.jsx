import { useEffect, useMemo, useState } from "react";
import { Button, Card, Form, Input, Modal, Radio, Select, Space, Table, Tag } from "antd";
import QueryActionTableLayout from "./QueryActionTableLayout";

export default function SystemRolesPanel({
  roles,
  createForm,
  editForm,
  onCreateRole,
  onEditRole,
  onDeleteRole,
  showResourceGrant,
  onOpenResourceGrant
}) {
  const [createOpen, setCreateOpen] = useState(false);
  const [idInput, setIdInput] = useState("");
  const [codeInput, setCodeInput] = useState("");
  const [appliedId, setAppliedId] = useState("");
  const [appliedCode, setAppliedCode] = useState("");
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  useEffect(() => {
    setSelectedRowKeys((keys) => {
      if (!keys.length) return keys;
      const exists = roles.some((r) => r.id === keys[0]);
      return exists ? keys : [];
    });
  }, [roles]);

  const filteredRoles = useMemo(() => {
    return roles.filter((r) => {
      const idQ = appliedId.trim();
      if (idQ) {
        const n = Number(idQ);
        if (!Number.isNaN(n)) {
          if (r.id !== n) return false;
        } else if (!String(r.id).includes(idQ)) {
          return false;
        }
      }
      const codeQ = appliedCode.trim().toLowerCase();
      if (codeQ && !(r.roleCode || "").toLowerCase().includes(codeQ)) {
        return false;
      }
      return true;
    });
  }, [roles, appliedId, appliedCode]);

  const runQuery = () => {
    setAppliedId(idInput);
    setAppliedCode(codeInput);
  };

  const clearFilters = () => {
    setIdInput("");
    setCodeInput("");
    setAppliedId("");
    setAppliedCode("");
  };

  const selectedRole = useMemo(
    () => (selectedRowKeys.length ? roles.find((r) => r.id === selectedRowKeys[0]) : null),
    [roles, selectedRowKeys]
  );

  const openEdit = () => {
    if (!selectedRole) {
      Modal.warning({ title: "请先选择角色", content: "在列表中点击一行以选择要修改的角色。" });
      return;
    }
    onEditRole(selectedRole);
    editForm.setFieldsValue({
      roleName: selectedRole.roleName,
      description: selectedRole.description,
      scopeType: selectedRole.scopeType,
      enabled: selectedRole.enabled
    });
  };

  const confirmDelete = () => {
    if (!selectedRole) {
      Modal.warning({ title: "请先选择角色", content: "在列表中点击一行以选择要删除的角色。" });
      return;
    }
    Modal.confirm({
      title: "删除角色",
      content: `确定删除「${selectedRole.roleName}」（${selectedRole.roleCode}）吗？将同时解除该角色与用户、资源的绑定。`,
      okText: "删除",
      okType: "danger",
      onOk: () => onDeleteRole(selectedRole.id)
    });
  };

  const roleColumns = [
    { title: "角色编号", dataIndex: "id", key: "id", width: 100 },
    { title: "角色编码", dataIndex: "roleCode", key: "roleCode", width: 140 },
    { title: "角色名称", dataIndex: "roleName", key: "roleName", width: 140 },
    { title: "数据范围", dataIndex: "scopeType", key: "scopeType", width: 120, render: (value) => <Tag>{value}</Tag> },
    { title: "描述", dataIndex: "description", key: "description", ellipsis: true },
    {
      title: "状态",
      dataIndex: "enabled",
      key: "enabled",
      width: 100,
      render: (value) => <Tag color={value ? "success" : "default"}>{value ? "启用" : "禁用"}</Tag>
    }
  ];

  const filterForm = (
    <Form layout="inline" colon={false} className="qat-filter-form">
      <Form.Item label="角色编号">
        <Input
          allowClear
          placeholder="请输入"
          value={idInput}
          onChange={(e) => setIdInput(e.target.value)}
          onPressEnter={runQuery}
        />
      </Form.Item>
      <Form.Item label="角色编码">
        <Input
          allowClear
          placeholder="请输入"
          value={codeInput}
          onChange={(e) => setCodeInput(e.target.value)}
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

  const goResourceGrant = () => {
    if (!selectedRowKeys.length) {
      Modal.warning({ title: "请先选择角色", content: "在列表中选中一行后，再点击「资源授权」。" });
      return;
    }
    onOpenResourceGrant?.(selectedRowKeys[0]);
  };

  const actionBar = (
    <Space wrap size={[8, 8]}>
      <Button type="primary" onClick={() => setCreateOpen(true)}>
        增加角色
      </Button>
      <Button onClick={openEdit}>修改角色</Button>
      <Button danger onClick={confirmDelete}>
        删除角色
      </Button>
      {showResourceGrant ? (
        <Button type="primary" ghost onClick={goResourceGrant}>
          资源授权
        </Button>
      ) : null}
    </Space>
  );

  return (
    <Card className="lc-roles-card system-fill-card" bordered={false}>
      <QueryActionTableLayout variant="lc" filter={filterForm} actions={actionBar}>
        <Table
          rowKey="id"
          size="small"
          columns={roleColumns}
          dataSource={filteredRoles}
          onRow={(record) => ({
            onClick: () => {
              setSelectedRowKeys((prev) => (prev[0] === record.id ? [] : [record.id]));
            }
          })}
          rowSelection={{
            type: "radio",
            selectedRowKeys,
            onChange: (keys) => setSelectedRowKeys(keys),
            /* antd 默认：已选行的 Radio 再点不会触发 onChange，无法取消；自定义单元格在 onClick 里切换 */
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
            total: filteredRoles.length === 0 ? 1 : filteredRoles.length,
            pageSize: 10,
            showSizeChanger: true,
            pageSizeOptions: [10, 20, 50],
            showTotal: () => `共 ${filteredRoles.length} 条`,
            hideOnSinglePage: false
          }}
          scroll={{ x: "max-content" }}
        />
      </QueryActionTableLayout>

      <Modal
        title="新增角色"
        open={createOpen}
        onCancel={() => setCreateOpen(false)}
        onOk={async () => {
          const ok = await onCreateRole();
          if (!ok) return Promise.reject();
          setCreateOpen(false);
        }}
        okText="创建"
        width={520}
        destroyOnClose
      >
        <Form form={createForm} layout="vertical" className="system-form">
          <Form.Item label="角色编码" name="roleCode" rules={[{ required: true, message: "请输入角色编码" }]}>
            <Input placeholder="例如：AUDITOR" />
          </Form.Item>
          <Form.Item label="角色名称" name="roleName" rules={[{ required: true, message: "请输入角色名称" }]}>
            <Input placeholder="例如：审计员" />
          </Form.Item>
          <Form.Item label="数据范围" name="scopeType" initialValue="SELF" rules={[{ required: true }]}>
            <Select
              style={{ width: "100%" }}
              options={[
                { label: "全部", value: "ALL" },
                { label: "机构", value: "ORG" },
                { label: "部门", value: "DEPT" },
                { label: "本人", value: "SELF" },
                { label: "自定义", value: "CUSTOM" }
              ]}
            />
          </Form.Item>
          <Form.Item label="角色描述" name="description">
            <Input.TextArea rows={3} placeholder="可选" />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  );
}
