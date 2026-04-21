import { Navigate, Outlet } from "react-router-dom";
import { canAccess } from "../auth/canAccess";

export default function ProtectedRoute({ user, rule = {} }) {
  if (!user) {
    return <Navigate to="/" replace />;
  }
  if (!canAccess(user, rule)) {
    return <Navigate to="/no-permission" replace />;
  }
  return <Outlet />;
}
