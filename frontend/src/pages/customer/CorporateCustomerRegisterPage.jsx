import { BankOutlined, CheckCircleOutlined, InfoCircleOutlined } from "@ant-design/icons";
import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Alert, Button, Form, Input, Select, Space, Tooltip, Typography, message } from "antd";
import { useCustomers } from "../../hooks/useCustomers";
import { useReference } from "../../hooks/useReference";
import { FALLBACK_REGION_OPTIONS } from "./customerConstants";
import "../../styles/system/system.css";
import "../../styles/customer/customer.css";
import "../../styles/customer/corp-register.css";

const { Title } = Typography;

const INTERBANK_OPTIONS = [
  { label: "否", value: "N" },
  { label: "是", value: "Y" }
];

const CORP_ID_TYPE_OPTIONS = [
  { label: "统一社会信用代码", value: "UNIFIED_SOCIAL_CREDIT" },
  { label: "营业执照", value: "BUSINESS_LICENSE" },
  { label: "组织机构代码", value: "ORG_CODE" }
];

const CORP_ID_TYPE_CODES = new Set(["UNIFIED_SOCIAL_CREDIT", "BUSINESS_LICENSE", "ORG_CODE"]);

function randomVirtualIdNo() {
  const n = () => Math.floor(Math.random() * 10);
  return `91${Array.from({ length: 16 }, n).join("")}`;
}

export default function CorporateCustomerRegisterPage({ token }) {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const { registerCustomer } = useCustomers(token);
  const { loadDictEntries } = useReference(token);
  const [idTypeOptions, setIdTypeOptions] = useState([]);
  const [countryRegionOptions, setCountryRegionOptions] = useState([]);
  const [idCountryRegionOptions, setIdCountryRegionOptions] = useState([]);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const [idRows, crRows, idcRows] = await Promise.all([
          loadDictEntries("ID_TYPE"),
          loadDictEntries("COUNTRY_REGION"),
          loadDictEntries("ID_COUNTRY_REGION")
        ]);
        if (cancelled) return;
        if (idRows?.length) {
          const mapped = idRows.map((r) => ({ label: r.labelZh, value: r.code }));
          setIdTypeOptions(mapped.filter((o) => CORP_ID_TYPE_CODES.has(o.value)));
        }
        if (crRows?.length) {
          setCountryRegionOptions(crRows.map((r) => ({ label: r.labelZh, value: r.code })));
        }
        if (idcRows?.length) {
          setIdCountryRegionOptions(idcRows.map((r) => ({ label: r.labelZh, value: r.code })));
        }
      } catch {
        /* 使用表单内本地兜底 */
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [loadDictEntries]);

  const corpIdTypeSelectOptions = useMemo(() => {
    if (idTypeOptions.length) return idTypeOptions;
    return CORP_ID_TYPE_OPTIONS;
  }, [idTypeOptions]);

  const countryRegionSelectOptions = useMemo(
    () => (countryRegionOptions.length ? countryRegionOptions : FALLBACK_REGION_OPTIONS),
    [countryRegionOptions]
  );

  const idCountryRegionSelectOptions = useMemo(
    () => (idCountryRegionOptions.length ? idCountryRegionOptions : FALLBACK_REGION_OPTIONS),
    [idCountryRegionOptions]
  );

  const handleSubmit = async () => {
    setError("");
    const values = await form.validateFields();
    if (!token) {
      message.error("请先登录");
      return;
    }

    const remarkPayload = {
      channel: "CORP_REGISTER",
      interbankFlag: values.interbankFlag,
      countryRegion: values.countryRegion,
      idCountryRegion: values.idCountryRegion
    };

    setSubmitting(true);
    const created = await registerCustomer(
      {
        name: values.customerName.trim(),
        idType: values.idType,
        idNo: values.idNo.trim(),
        mobile: values.mobile.trim(),
        remark: JSON.stringify(remarkPayload)
      },
      setError
    );
    setSubmitting(false);
    if (created) {
      message.success("注册成功");
      navigate("/customers");
    }
  };

  const handleClear = () => {
    form.resetFields();
    setError("");
  };

  const labelWithInfo = (text, tip) => (
    <span className="corp-register-label">
      {text}
      <Tooltip title={tip}>
        <InfoCircleOutlined className="corp-register-label-info" aria-label="说明" />
      </Tooltip>
    </span>
  );

  return (
    <div className="system-page customer-page customer-module-page corp-register-page-root">
      <div className="customer-portal customer-portal--ref">
        {error ? <Alert className="form-alert customer-portal-alert" message={error} type="error" showIcon /> : null}
        <div className="customer-portal-body customer-portal-body--corp-register">
          <div className="corp-register-shell">
            <aside className="corp-register-aside" aria-labelledby="corp-register-aside-heading">
              <div className="corp-register-aside-inner">
                <div className="corp-register-aside-icon-wrap" aria-hidden>
                  <BankOutlined className="corp-register-aside-icon" aria-hidden />
                </div>
                <h2 id="corp-register-aside-heading" className="corp-register-aside-title">
                  公司客户
                </h2>
                <p className="corp-register-aside-lead">合规登记与基础信息录入，请如实填写以下字段。</p>
                <ul className="corp-register-aside-list">
                  <li>
                    <CheckCircleOutlined className="corp-register-aside-list-icon" aria-hidden />
                    证件信息需与证照保持一致
                  </li>
                  <li>
                    <CheckCircleOutlined className="corp-register-aside-list-icon" aria-hidden />
                    联系电话用于后续业务通知
                  </li>
                  <li>
                    <CheckCircleOutlined className="corp-register-aside-list-icon" aria-hidden />
                    提交前请核对同业与国别信息
                  </li>
                </ul>
              </div>
            </aside>
            <main className="corp-register-main">
              <div className="corp-register-main-header">
                <Title level={3} className="corp-register-title">
                  公司客户注册
                </Title>
              </div>
              <Form
                form={form}
                className="corp-register-form"
                layout="horizontal"
                labelAlign="left"
                colon={false}
                requiredMark
                initialValues={{ interbankFlag: "N" }}
                onFinish={handleSubmit}
              >
                <Form.Item label="客户名称" name="customerName" rules={[{ required: true, message: "请输入客户名称" }]}>
                  <Input allowClear placeholder="请输入" autoComplete="off" className="corp-register-control" />
                </Form.Item>
                <Form.Item
                  label={labelWithInfo("同业标识", "是否属于金融同业客户")}
                  name="interbankFlag"
                  rules={[{ required: true, message: "请选择" }]}
                >
                  <Select options={INTERBANK_OPTIONS} className="corp-register-control" />
                </Form.Item>
                <Form.Item label="国别/地区" name="countryRegion" rules={[{ required: true, message: "请选择国别/地区" }]}>
                  <Select allowClear placeholder="请选择" options={countryRegionSelectOptions} className="corp-register-control" />
                </Form.Item>
                <Form.Item label="证件国别/地区" name="idCountryRegion" rules={[{ required: true, message: "请选择" }]}>
                  <Select allowClear placeholder="请选择" options={idCountryRegionSelectOptions} className="corp-register-control" />
                </Form.Item>
                <Form.Item label="证件类型" name="idType" rules={[{ required: true, message: "请选择证件类型" }]}>
                  <Select allowClear placeholder="请选择" options={corpIdTypeSelectOptions} className="corp-register-control" />
                </Form.Item>
                <Form.Item label="证件号码" required className="corp-register-item-idno">
                  <div className="corp-register-field-block">
                    <Form.Item name="idNo" noStyle rules={[{ required: true, message: "请输入证件号码" }]}>
                      <Input allowClear placeholder="请输入" autoComplete="off" className="corp-register-control" />
                    </Form.Item>
                    <div className="corp-register-id-extra">
                      <Button
                        type="link"
                        className="corp-register-gen-id"
                        onClick={() => form.setFieldValue("idNo", randomVirtualIdNo())}
                      >
                        生成虚拟证件号
                      </Button>
                    </div>
                  </div>
                </Form.Item>
                <Form.Item
                  label={labelWithInfo("证件号码确认", "请再次输入证件号码")}
                  name="idNoConfirm"
                  dependencies={["idNo"]}
                  rules={[
                    { required: true, message: "请确认证件号码" },
                    ({ getFieldValue }) => ({
                      validator(_, value) {
                        const id = getFieldValue("idNo");
                        if (!value || id === value) {
                          return Promise.resolve();
                        }
                        return Promise.reject(new Error("与证件号码不一致"));
                      }
                    })
                  ]}
                >
                  <Input allowClear placeholder="请输入" autoComplete="off" className="corp-register-control" />
                </Form.Item>
                <Form.Item label="联系电话" name="mobile" rules={[{ required: true, message: "请输入联系电话" }]}>
                  <Input allowClear placeholder="请输入手机号" autoComplete="tel" className="corp-register-control" />
                </Form.Item>
                <Form.Item label=" " className="corp-register-actions">
                  <Space size="middle">
                    <Button className="corp-register-btn-clear" onClick={handleClear}>
                      清空
                    </Button>
                    <Button type="primary" className="corp-register-btn-submit" htmlType="submit" loading={submitting}>
                      确认
                    </Button>
                  </Space>
                </Form.Item>
              </Form>
            </main>
          </div>
        </div>
      </div>
    </div>
  );
}
