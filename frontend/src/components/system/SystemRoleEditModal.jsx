import { Form, Input, Modal, Select, Switch } from "antd";

export default function SystemRoleEditModal({ form, editingRole, onCancel, onSubmit }) {
  return (
    <Modal
      title="编辑角色"
      open={!!editingRole}
      onCancel={onCancel}
      onOk={onSubmit}
      okText="保存"
    >
      <Form form={form} layout="vertical">
        <Form.Item label="角色名称" name="roleName" rules={[{ required: true, message: "请输入角色名称" }]}>
          <Input />
        </Form.Item>
        <Form.Item label="数据范围" name="scopeType" rules={[{ required: true }]}>
          <Select
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
          <Input.TextArea rows={3} />
        </Form.Item>
        <Form.Item label="启用状态" name="enabled" valuePropName="checked">
          <Switch checkedChildren="启用" unCheckedChildren="禁用" />
        </Form.Item>
      </Form>
    </Modal>
  );
}
