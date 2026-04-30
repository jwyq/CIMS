import { useCallback, useMemo } from "react";
import { applyApiError, createAuthedApi } from "../api/client";

export function useCustomers(token) {
  const api = useMemo(() => createAuthedApi(token), [token]);

  const queryCustomers = useCallback(async (params = {}, setError) => {
    try {
      const query = new URLSearchParams();
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== "") {
          query.set(key, value);
        }
      });
      const suffix = query.toString() ? `?${query.toString()}` : "";
      return await api(`/customers${suffix}`);
    } catch (err) {
      applyApiError(setError, err, "加载客户列表失败");
      return [];
    }
  }, [api]);

  const getCustomerDetail = useCallback(
    async (customerId, setError) => {
      try {
        return await api(`/customers/${customerId}`);
      } catch (err) {
        applyApiError(setError, err, "加载客户详情失败");
        return null;
      }
    },
    [api]
  );

  const registerCustomer = useCallback(
    async (payload, setError) => {
      try {
        return await api("/customers/register", {
          method: "POST",
          body: JSON.stringify(payload)
        });
      } catch (err) {
        applyApiError(setError, err, "客户注册失败");
        return null;
      }
    },
    [api]
  );

  const updateBasicInfo = useCallback(
    async (customerId, payload, setError) => {
      try {
        return await api(`/customers/${customerId}/basic-info`, {
          method: "PUT",
          body: JSON.stringify(payload)
        });
      } catch (err) {
        applyApiError(setError, err, "客户基本信息修改失败");
        return null;
      }
    },
    [api]
  );

  const getBasicInfoHistory = useCallback(
    async (customerId, setError) => {
      try {
        return await api(`/customers/${customerId}/basic-info-history`);
      } catch (err) {
        applyApiError(setError, err, "加载客户修改历史失败");
        return [];
      }
    },
    [api]
  );

  return {
    queryCustomers,
    getCustomerDetail,
    registerCustomer,
    updateBasicInfo,
    getBasicInfoHistory
  };
}
