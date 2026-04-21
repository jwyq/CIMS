import { Navigate, Route, Routes } from "react-router-dom";
import HomePage from "../pages/HomePage";
import CustomerManagementPage from "../pages/customer/CustomerManagementPage";
import CorporateCustomerRegisterPage from "../pages/customer/CorporateCustomerRegisterPage";
import CreditApprovalPage from "../pages/approval/CreditApprovalPage";
import SystemManagementPage from "../pages/system/SystemManagementPage";
import ForbiddenPage from "../pages/ForbiddenPage";
import Layout from "../components/Layout";
import ProtectedRoute from "../components/ProtectedRoute";
import { getUiNavigation } from "../auth/uiNavigation";

const pageComponents = {
  "/home": HomePage,
  "/credit-approval": CreditApprovalPage,
  "/system-management": SystemManagementPage
};

export default function AppRoutes({ user, onLogout }) {
  const { routes, routeOrder } = getUiNavigation(user);
  const customerRule = (routes["/customers"] || {}).rule || {};
  return (
    <Routes>
      <Route element={<Layout user={user} onLogout={onLogout} />}>
        <Route path="/" element={<Navigate to="/home" />} />
        <Route path="/no-permission" element={<ForbiddenPage />} />
        <Route path="/forbidden" element={<ForbiddenPage />} />
        <Route element={<ProtectedRoute user={user} rule={customerRule} />}>
          <Route path="/customers" element={<CustomerManagementPage user={user} token={user.token} />} />
          <Route path="/customers/corp-register" element={<CorporateCustomerRegisterPage token={user.token} />} />
          <Route path="/customers/corp-query" element={<CustomerManagementPage user={user} token={user.token} />} />
        </Route>
        {(routeOrder || [])
          .filter((path) => pageComponents[path])
          .map((path) => {
            const Page = pageComponents[path];
            return (
              <Route key={path} element={<ProtectedRoute user={user} rule={(routes[path] || {}).rule || {}} />}>
                <Route path={path} element={<Page user={user} token={user.token} />} />
              </Route>
            );
          })}
      </Route>
    </Routes>
  );
}
