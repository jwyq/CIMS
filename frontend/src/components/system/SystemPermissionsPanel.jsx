import { Button, Card, Select, Space, Tree } from "antd";

export default function SystemPermissionsPanel({
  roleOptions,
  selectedRoleId,
  onRoleChange,
  savingRoleResources,
  onSaveRoleResources,
  checkedResourceIds,
  onCheckedResourceIdsChange,
  treeData
}) {
  const collectDescendantKeys = (nodes = []) => {
    const keys = [];
    nodes.forEach((node) => {
      keys.push(node.key);
      if (node.children && node.children.length > 0) {
        keys.push(...collectDescendantKeys(node.children));
      }
    });
    return keys;
  };

  const handleCheck = (keys, info) => {
    const nextChecked = new Set(Array.isArray(keys) ? keys : keys.checked);
    const isMenuNode = info && info.node && info.node.resourceType === "MENU";
    if (isMenuNode) {
      const descendantKeys = collectDescendantKeys(info.node.children || []);
      if (info.checked) {
        descendantKeys.forEach((key) => nextChecked.add(key));
      } else {
        descendantKeys.forEach((key) => nextChecked.delete(key));
      }
    }
    onCheckedResourceIdsChange(Array.from(nextChecked));
  };

  return (
    <Card className="system-section-card system-fill-card">
      <Space direction="vertical" size={16} style={{ width: "100%" }}>
        <div className="system-toolbar">
          <Select
            placeholder="选择角色"
            style={{ width: 320 }}
            options={roleOptions}
            value={selectedRoleId}
            onChange={onRoleChange}
          />
          <Button type="primary" loading={savingRoleResources} onClick={onSaveRoleResources}>
            保存角色资源授权
          </Button>
        </div>
        <div className="resource-tree-wrap">
          <Tree
            checkable
            checkStrictly
            defaultExpandAll
            checkedKeys={checkedResourceIds}
            onCheck={handleCheck}
            treeData={treeData}
          />
        </div>
      </Space>
    </Card>
  );
}
