import { useEffect, useMemo, useState } from "react";
import { Button, Card, Form, Input, Modal, Radio, Select, Space, Table, Tag } from "antd";
import QueryActionTableLayout from "../system/QueryActionTableLayout";
import {
  CUSTOMER_ID_TYPE_OPTIONS,
  getCountryRegionCodeFromRemark,
  getCustomerIdTypeLabel,
  getCustomerStatusLabelZh,
  getIdCountryRegionCodeFromRemark,
  getRegionLabel
} from "../../pages/customer/customerConstants";

/**
 * 客户查询 / 列表：查询区 + 操作区（详情/修改）+ 表格；单选行与系统管理列表一致
 */
export default function CustomerQueryPanel({
  queryForm,
  list,
  loading,
  onQuery,
  onClearFilters,
  onOpenDetail,
  onOpenEdit,
  /** { label, value }[] 来自 /reference/dicts?type=ID_TYPE；空则用本地兜底 */
  idTypeOptions = null,
  /** COUNTRY_REGION + ID_COUNTRY_REGION 合并后的 code→中文，用于国别列展示 */
  regionLabelMap = null
}) {
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  useEffect(() => {
    setSelectedRowKeys((keys) => {
      if (!keys.length) return keys;
      return list.some((r) => r.id === keys[0]) ? keys : [];
    });
  }, [list]);

  const idTypeSelectOptions = idTypeOptions?.length ? idTypeOptions : CUSTOMER_ID_TYPE_OPTIONS;

  const columns = useMemo(
    () => [
      { title: "客户名称", dataIndex: "name", key: "name", width: 120, ellipsis: true },
      { title: "CIMS客户编号", dataIndex: "customerNo", key: "customerNo", width: 180, ellipsis: true },
      {
        title: "证件类型",
        dataIndex: "idType",
        key: "idType",
        width: 140,
        ellipsis: true,
        render: (_, row) => row.idTypeLabelZh || getCustomerIdTypeLabel(row.idType)
      },
      { title: "证件号码", dataIndex: "idNo", key: "idNo", width: 200, ellipsis: true },
      {
        title: "国别/地区",
        key: "countryRegion",
        width: 120,
        ellipsis: true,
        render: (_, row) => getRegionLabel(getCountryRegionCodeFromRemark(row.remark), regionLabelMap)
      },
      {
        title: "证件国别/地区",
        key: "idCountryRegion",
        width: 130,
        ellipsis: true,
        render: (_, row) => getRegionLabel(getIdCountryRegionCodeFromRemark(row.remark), regionLabelMap)
      },
      {
        title: "管理机构",
        dataIndex: "mgmtOrgName",
        key: "mgmtOrgName",
        width: 120,
        ellipsis: true,
        render: (v) => v || "—"
      },
      {
        title: "主办客户经理",
        dataIndex: "managerDisplayName",
        key: "managerDisplayName",
        width: 120,
        ellipsis: true,
        render: (v) => v || "—"
      },
      {
        title: "状态",
        dataIndex: "status",
        key: "status",
        width: 100,
        render: (v) => (
          <Tag
            color={v === "ACTIVE" ? "green" : v === "FROZEN" ? "red" : "default"}
          >
            {getCustomerStatusLabelZh(v)}
          </Tag>
        )
      }
    ],
    [regionLabelMap]
  );

  const goDetail = () => {
    if (!selectedRowKeys.length) {
      Modal.warning({
        title: "请先选择客户",
        content: "在列表中点击一行以选中客户，再点击「详情」。"
      });
      return;
    }
    onOpenDetail(selectedRowKeys[0]);
  };

  const goEdit = () => {
    if (!selectedRowKeys.length) {
      Modal.warning({
        title: "请先选择客户",
        content: "在列表中点击一行以选中客户，再点击「修改」。"
      });
      return;
    }
    onOpenEdit(selectedRowKeys[0]);
  };

  const filterForm = (
    <Form
      form={queryForm}
      layout="inline"
      colon={false}
      className="qat-filter-form customer-query-form"
      onFinish={onQuery}
    >
      <Form.Item name="name" label="客户名称" className="customer-query-field">
        <Input allowClear placeholder="模糊查询" />
      </Form.Item>
      <Form.Item name="customerNo" label="CIMS客户编号" className="customer-query-field">
        <Input allowClear placeholder="支持模糊" />
      </Form.Item>
      <Form.Item name="idType" label="证件类型" className="customer-query-field">
        <Select allowClear placeholder="全部" options={idTypeSelectOptions} size="small" />
      </Form.Item>
      <Form.Item name="idNo" label="证件号码" className="customer-query-field">
        <Input allowClear placeholder="支持模糊" />
      </Form.Item>
      <Form.Item className="qat-filter-actions">
        <Space size={10}>
          <Button type="primary" htmlType="submit">
            查询
          </Button>
          <Button
            onClick={() => {
              queryForm.resetFields();
              onClearFilters?.();
            }}
          >
            清空
          </Button>
        </Space>
      </Form.Item>
    </Form>
  );

  const actionBar = (
    <Space wrap size={[8, 8]}>
      <Button onClick={goDetail}>详情</Button>
      <Button type="primary" ghost onClick={goEdit}>
        修改
      </Button>
    </Space>
  );

  return (
    <div className="customer-query-panel">
      <Card
        className="customer-workbench customer-workbench--flat customer-query-card customer-query-card--no-head"
        bordered={false}
      >
        <QueryActionTableLayout variant="lc" filter={filterForm} actions={actionBar}>
          <Table
            rowKey="id"
            size="small"
            loading={loading}
            columns={columns}
            dataSource={list}
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
              pageSize: 10,
              showSizeChanger: true,
              pageSizeOptions: [10, 20, 50],
              showTotal: (total) => `共 ${total} 条`,
              hideOnSinglePage: false
            }}
          />
        </QueryActionTableLayout>
      </Card>
    </div>
  );
}
