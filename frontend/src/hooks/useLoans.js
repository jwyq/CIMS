import { useEffect, useMemo, useState } from "react";
import { applyApiError, createAuthedApi } from "../api/client";

export function useLoans(token) {
  const api = useMemo(() => createAuthedApi(token), [token]);
  const [data, setData] = useState([]);
  const [error, setError] = useState("");

  const load = async () => {
    setError("");
    try {
      setData(await api("/loans"));
    } catch (e) {
      applyApiError(setError, e, "加载失败");
    }
  };

  const create = async (payload) => {
    setError("");
    try {
      await api("/loans", { method: "POST", body: JSON.stringify(payload) });
      await load();
    } catch (e) {
      applyApiError(setError, e, "创建失败");
    }
  };

  useEffect(() => {
    load();
  }, []);

  return { data, error, load, create };
}
