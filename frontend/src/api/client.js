const API_BASE = "http://localhost:8080/api";

/** 无权限（HTTP 403 / 业务 40301）；不触发登出，可与全局提示配合 */
export class ApiForbiddenError extends Error {
  constructor(message = "") {
    super(message);
    this.name = "ApiForbiddenError";
  }
}

function dispatchForbidden(serverMsg) {
  window.dispatchEvent(
    new CustomEvent("cims-forbidden", { detail: { message: serverMsg || undefined } })
  );
}

/** 在 hook 的 catch 中使用：无权限时清空 error，避免与全局 toast 重复展示 */
export function applyApiError(setError, err, fallbackMessage) {
  if (err instanceof ApiForbiddenError) {
    setError("");
    return;
  }
  setError(err?.message || fallbackMessage || "");
}

export async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, options);
  const text = await response.text();
  let payload = null;
  if (text) {
    try {
      payload = JSON.parse(text);
    } catch (e) {
      payload = text;
    }
  }

  if (!response.ok) {
    // 仅未认证/登录失效时登出；403 为无权限，不应跳转登录页
    if (response.status === 401) {
      window.dispatchEvent(new CustomEvent("cims-auth-failed", { detail: { status: response.status } }));
    }
    if (response.status === 403) {
      const serverMsg = payload && typeof payload === "object" ? payload.message : "";
      dispatchForbidden(serverMsg);
      throw new ApiForbiddenError(serverMsg || "无权限执行此操作");
    }
    const message = payload && typeof payload === "object" ? payload.message : payload;
    throw new Error(message || `Request failed: ${response.status}`);
  }

  if (payload && typeof payload === "object" && Object.prototype.hasOwnProperty.call(payload, "code")) {
    if (payload.code !== 0) {
      if (payload.code === 40101) {
        window.dispatchEvent(new CustomEvent("cims-auth-failed", { detail: { code: payload.code } }));
      } else if (payload.code === 40301) {
        dispatchForbidden(payload.message);
        throw new ApiForbiddenError(payload.message || "无权限执行此操作");
      }
      throw new Error(payload.message || "Request failed");
    }
    return payload.data;
  }
  return payload;
}

export function createAuthedApi(token) {
  return async (path, options = {}) => {
    return request(path, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
        ...(options.headers || {})
      }
    });
  };
}
