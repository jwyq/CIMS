import { useCallback, useMemo } from "react";
import { createAuthedApi } from "../api/client";

/** 字典、机构等参考数据（需登录） */
export function useReference(token) {
  const api = useMemo(() => createAuthedApi(token), [token]);

  const loadDictEntries = useCallback(
    async (type) => {
      const q = new URLSearchParams({ type });
      return await api(`/reference/dicts?${q}`);
    },
    [api]
  );

  const loadOrgs = useCallback(async () => api("/reference/orgs"), [api]);

  return { loadDictEntries, loadOrgs };
}
