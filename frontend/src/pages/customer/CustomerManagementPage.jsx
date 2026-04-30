import { BankOutlined, SearchOutlined } from "@ant-design/icons";
import { useCallback, useEffect, useState } from "react";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";
import { Alert, Form } from "antd";
import CustomerDetailDrawer from "../../components/customer/CustomerDetailDrawer";
import CustomerQueryPanel from "../../components/customer/CustomerQueryPanel";
import { useCustomers } from "../../hooks/useCustomers";
import { useReference } from "../../hooks/useReference";
import "../../styles/system/system.css";
import "../../styles/customer/customer.css";

const REGISTER_ITEMS = [{ key: "corp-register", label: "公司客户注册", icon: <BankOutlined /> }];
const QUERY_ITEMS = [{ key: "corp-query", label: "公司客户查询/管理", icon: <SearchOutlined /> }];

export default function CustomerManagementPage({ token }) {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const isCorpQueryRoute = location.pathname === "/customers/corp-query";
  const panelFromUrl =
    isCorpQueryRoute || searchParams.get("panel") === "query" ? "query" : "home";
  const [activePanel, setActivePanel] = useState(panelFromUrl);

  const [editForm] = Form.useForm();
  const [queryForm] = Form.useForm();
  const [list, setList] = useState([]);
  const [selected, setSelected] = useState(null);
  const [history, setHistory] = useState([]);
  const [error, setError] = useState("");
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const { queryCustomers, getCustomerDetail, updateBasicInfo, getBasicInfoHistory } = useCustomers(token);
  const { loadDictEntries } = useReference(token);
  const [idTypeOptions, setIdTypeOptions] = useState([]);
  const [regionLabelMap, setRegionLabelMap] = useState({});

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const rows = await loadDictEntries("ID_TYPE");
        if (!cancelled && rows?.length) {
          setIdTypeOptions(rows.map((r) => ({ label: r.labelZh, value: r.code })));
        }
      } catch {
        /* 字典加载失败时查询面板使用本地 CUSTOMER_ID_TYPE_OPTIONS 兜底 */
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [loadDictEntries]);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const [cr, idr] = await Promise.all([
          loadDictEntries("COUNTRY_REGION"),
          loadDictEntries("ID_COUNTRY_REGION")
        ]);
        if (cancelled) return;
        const map = {};
        [...(cr || []), ...(idr || [])].forEach((r) => {
          if (r.code) map[r.code] = r.labelZh;
        });
        setRegionLabelMap(map);
      } catch {
        /* 失败时用 customerConstants 内 REGION_CODE_LABEL 兜底 */
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [loadDictEntries]);

  useEffect(() => {
    setActivePanel(panelFromUrl);
  }, [panelFromUrl]);

  const loadList = useCallback(
    async (params = {}) => {
      setLoading(true);
      setError("");
      const data = await queryCustomers(params, setError);
      setList(data || []);
      setLoading(false);
    },
    [queryCustomers]
  );

  useEffect(() => {
    if (activePanel === "query") {
      loadList(queryForm.getFieldsValue());
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps -- 仅随面板切换拉列表；queryForm 为稳定实例
  }, [activePanel, loadList]);

  const openDetail = useCallback(
    async (customerId) => {
      const detail = await getCustomerDetail(customerId, setError);
      const logs = await getBasicInfoHistory(customerId, setError);
      if (detail) {
        setSelected(detail);
        setHistory(logs || []);
        setDrawerOpen(true);
      }
    },
    [getCustomerDetail, getBasicInfoHistory]
  );

  const openEdit = useCallback(
    async (customerId) => {
      const detail = await getCustomerDetail(customerId, setError);
      if (!detail) return;
      setSelected(detail);
      editForm.setFieldsValue(detail);
      setDrawerOpen(true);
    },
    [getCustomerDetail, editForm]
  );

  const handleUpdate = useCallback(async () => {
    if (!selected?.id) return;
    const values = await editForm.validateFields();
    const updated = await updateBasicInfo(selected.id, values, setError);
    if (updated) {
      await openDetail(selected.id);
      await loadList(queryForm.getFieldsValue());
    }
  }, [selected, editForm, updateBasicInfo, openDetail, loadList, queryForm]);

  const handleQuerySubmit = useCallback(
    (values) => {
      loadList(values || {});
    },
    [loadList]
  );

  const handleClearFilters = useCallback(() => {
    loadList({});
  }, [loadList]);

  return (
    <div className="system-page customer-page customer-module-page">
      <div className="customer-portal customer-portal--ref">
        {error ? <Alert className="form-alert customer-portal-alert" message={error} type="error" showIcon /> : null}
        <div className={`customer-portal-body${activePanel === "home" ? " customer-portal-body--home" : ""}`}>
          {activePanel === "home" && (
            <div className="customer-home-layout">
              <div className="customer-hero">
                <div className="customer-hero-icon">
                  <BankOutlined />
                </div>
                <div className="customer-hero-text">
                  <div className="customer-hero-title">Welcome!</div>
                  <div className="customer-hero-subtitle">欢迎使用客户管理系统（贷前）</div>
                </div>
              </div>
              <div className="customer-home-rest">
                <div className="customer-home-board customer-home-board--grid">
                  <div className="customer-row-heading-inline">
                    <span className="customer-row-no">01</span>
                    <span className="customer-row-label">客户注册</span>
                  </div>
                  <div className="customer-row-cards customer-row-cards--u3 customer-row-cards--u3-two">
                    {REGISTER_ITEMS.map((item) => (
                      <button
                        key={item.key}
                        type="button"
                        className="customer-feature-card customer-feature-card--framed"
                        onClick={() => navigate("/customers/corp-register")}
                      >
                        <span className="customer-feature-icon">{item.icon}</span>
                        <span className="customer-feature-label">{item.label}</span>
                      </button>
                    ))}
                  </div>
                  <div className="customer-row-heading-inline">
                    <span className="customer-row-no">02</span>
                    <span className="customer-row-label">客户查询/管理</span>
                  </div>
                  <div className="customer-row-cards customer-row-cards--u3 customer-row-cards--u3-three">
                    {QUERY_ITEMS.map((item) => (
                      <button
                        key={item.key}
                        type="button"
                        className="customer-feature-card customer-feature-card--framed"
                        onClick={() => navigate("/customers/corp-query")}
                      >
                        <span className="customer-feature-icon">{item.icon}</span>
                        <span className="customer-feature-label">{item.label}</span>
                      </button>
                    ))}
                  </div>
                </div>
                <div className="customer-home-rest-spacer" aria-hidden="true" />
              </div>
            </div>
          )}
          {activePanel === "query" && (
            <CustomerQueryPanel
              queryForm={queryForm}
              list={list}
              loading={loading}
              onQuery={handleQuerySubmit}
              onClearFilters={handleClearFilters}
              onOpenDetail={openDetail}
              onOpenEdit={openEdit}
              idTypeOptions={idTypeOptions}
              regionLabelMap={regionLabelMap}
            />
          )}
        </div>
      </div>

      <CustomerDetailDrawer
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        selected={selected}
        history={history}
        editForm={editForm}
        onSave={handleUpdate}
      />
    </div>
  );
}
