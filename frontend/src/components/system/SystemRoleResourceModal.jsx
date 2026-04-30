import { useEffect, useMemo, useState } from "react";
import { Button, Input, Modal, Space, Tree } from "antd";

function collectDescendantKeys(nodes = []) {
  const keys = [];
  nodes.forEach((node) => {
    keys.push(node.key);
    if (node.children && node.children.length > 0) {
      keys.push(...collectDescendantKeys(node.children));
    }
  });
  return keys;
}

function collectAllKeys(nodes = []) {
  const keys = [];
  const walk = (arr) => {
    arr.forEach((n) => {
      keys.push(n.key);
      if (n.children?.length) walk(n.children);
    });
  };
  walk(nodes);
  return keys;
}

function selfMatchNode(n, lower) {
  const name = (n.resourceName || "").toLowerCase();
  const code = (n.resourceCode || "").toLowerCase();
  const apis = (n.bindingApiCodes || []).join(" ").toLowerCase();
  return name.includes(lower) || code.includes(lower) || apis.includes(lower);
}

/** 保留匹配节点及其子树；非匹配节点仅在有匹配后代时保留并收缩子树 */
function filterResourceTreeNodes(nodes, q) {
  const trimmed = q.trim();
  if (!trimmed) return nodes;
  const lower = trimmed.toLowerCase();
  const walk = (arr) => {
    const out = [];
    for (const n of arr) {
      const rawChildren = n.children || [];
      if (selfMatchNode(n, lower)) {
        out.push({ ...n, children: rawChildren });
        continue;
      }
      const filteredChildren = walk(rawChildren);
      if (filteredChildren.length > 0) {
        out.push({ ...n, children: filteredChildren });
      }
    }
    return out;
  };
  return walk(nodes);
}

/** 角色资源授权弹窗：树形多选；支持搜索筛选、展开控制与虚拟滚动 */
export default function SystemRoleResourceModal({
  open,
  title,
  treeData,
  checkedResourceIds,
  onCheckedResourceIdsChange,
  saving,
  onCancel,
  onSave
}) {
  const [searchText, setSearchText] = useState("");
  const [expandedKeys, setExpandedKeys] = useState([]);

  const displayTree = useMemo(() => {
    return filterResourceTreeNodes(treeData || [], searchText);
  }, [treeData, searchText]);

  useEffect(() => {
    if (!open) return;
    setSearchText("");
  }, [open]);

  useEffect(() => {
    if (!open) return;
    setExpandedKeys(collectAllKeys(displayTree));
  }, [open, displayTree]);

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

  const handleOk = async () => {
    const ok = await onSave();
    if (ok === false) {
      return Promise.reject();
    }
  };

  const expandAll = () => setExpandedKeys(collectAllKeys(displayTree));
  const collapseAll = () => setExpandedKeys([]);

  const treeHeight = 420;

  return (
    <Modal
      title={title || "资源授权"}
      open={open}
      onCancel={onCancel}
      onOk={handleOk}
      okText="保存授权"
      cancelText="关闭"
      width={840}
      confirmLoading={saving}
      destroyOnClose
      styles={{ body: { maxHeight: "min(78vh, 640px)", overflow: "hidden", display: "flex", flexDirection: "column", gap: 10 } }}
    >
      <p style={{ margin: 0, color: "rgba(0,0,0,0.55)" }}>
        勾选该角色可访问的资源；支持按名称、资源编码、绑定 API 检索。保存后生效。
      </p>
      <div className="resource-tree-toolbar">
        <Input.Search
          allowClear
          placeholder="搜索资源名称、编码或绑定 API"
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          style={{ maxWidth: 360 }}
        />
        <Space size={8} wrap>
          <Button size="small" onClick={expandAll}>
            展开全部
          </Button>
          <Button size="small" onClick={collapseAll}>
            收起全部
          </Button>
        </Space>
      </div>
      <div className="resource-tree-wrap resource-tree-wrap--modal">
        <Tree
          checkable
          checkStrictly
          showLine
          blockNode
          height={treeHeight}
          expandedKeys={expandedKeys}
          onExpand={setExpandedKeys}
          checkedKeys={checkedResourceIds}
          onCheck={handleCheck}
          treeData={displayTree}
        />
      </div>
    </Modal>
  );
}
