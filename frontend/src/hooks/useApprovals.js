import { useEffect, useMemo, useState } from "react";
import { applyApiError, createAuthedApi } from "../api/client";

export function useApprovals(token) {
  const api = useMemo(() => createAuthedApi(token), [token]);
  const [data, setData] = useState([]);
  const [error, setError] = useState("");

  const load = async () => {
    setError("");
    try {
      setData(await api("/approvals"));
    } catch (e) {
      applyApiError(setError, e, "加载失败");
    }
  };

  const approve = async (loanApplicationId) => {
    setError("");
    try {
      await api("/approvals/approve", {
        method: "POST",
        body: JSON.stringify({ loanApplicationId })
      });
      await load();
    } catch (e) {
      applyApiError(setError, e, "审批失败");
    }
  };

  useEffect(() => {
    load();
  }, []);

  return { data, error, load, approve };
}
