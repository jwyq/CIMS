import { useEffect, useRef, useState } from "react";
import { ConfigProvider, theme, message } from "antd";
import { useNavigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import AppRoutes from "./routes/AppRoutes";

const FORBIDDEN_TOAST_COOLDOWN_MS = 500;

export default function App() {
  const lastForbiddenToastAt = useRef(0);
  const navigate = useNavigate();
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem("cims-user");
    return saved ? JSON.parse(saved) : null;
  });

  const handleLogin = (payload) => {
    localStorage.setItem("cims-user", JSON.stringify(payload));
    setUser(payload);
    navigate("/home", { replace: true });
  };

  const logout = () => {
    localStorage.removeItem("cims-user");
    setUser(null);
    navigate("/", { replace: true });
  };

  useEffect(() => {
    const handleAuthFailed = () => {
      localStorage.removeItem("cims-user");
      setUser(null);
      navigate("/", { replace: true });
    };
    const handleForbidden = (e) => {
      const text = (e.detail && e.detail.message) || "无权限执行此操作";
      const now = Date.now();
      if (now - lastForbiddenToastAt.current < FORBIDDEN_TOAST_COOLDOWN_MS) {
        return;
      }
      lastForbiddenToastAt.current = now;
      message.error({ content: text, key: "cims-forbidden" });
    };
    window.addEventListener("cims-auth-failed", handleAuthFailed);
    window.addEventListener("cims-forbidden", handleForbidden);
    return () => {
      window.removeEventListener("cims-auth-failed", handleAuthFailed);
      window.removeEventListener("cims-forbidden", handleForbidden);
    };
  }, [navigate]);

  if (!user) {
    return (
      <ConfigProvider
        theme={{
          algorithm: theme.defaultAlgorithm,
          token: {
            colorPrimary: "#4e86e8",
            borderRadius: 8,
            fontFamily: "\"IBM Plex Sans\", sans-serif"
          }
        }}
      >
        <LoginPage onLogin={handleLogin} />
      </ConfigProvider>
    );
  }

  return (
    <ConfigProvider
      theme={{
        algorithm: theme.defaultAlgorithm,
        token: {
          colorPrimary: "#4e86e8",
          colorBgBase: "#f2f5fb",
          colorBgContainer: "#ffffff",
          colorBorder: "#dbe4f0",
          borderRadius: 8,
          fontFamily: "\"IBM Plex Sans\", sans-serif"
        }
      }}
    >
      <AppRoutes user={user} onLogout={logout} />
    </ConfigProvider>
  );
}
