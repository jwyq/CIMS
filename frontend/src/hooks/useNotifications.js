import { useEffect, useMemo, useState } from "react";
import { applyApiError, createAuthedApi } from "../api/client";

export function useNotifications(token) {
  const api = useMemo(() => createAuthedApi(token), [token]);
  const [data, setData] = useState([]);
  const [error, setError] = useState("");

  const load = async () => {
    setError("");
    try {
      setData(await api("/notifications"));
    } catch (e) {
      applyApiError(setError, e, "加载失败");
    }
  };

  useEffect(() => {
    load();
  }, []);

  return { data, error, load };
}
