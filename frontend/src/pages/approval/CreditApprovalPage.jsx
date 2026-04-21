import { Tabs } from "antd";
import LoansPage from "./LoansPage";
import ApprovalsPage from "./ApprovalsPage";
import { usePermissions } from "../../hooks/usePermissions";

export default function CreditApprovalPage({ token, user }) {
  const { canAccessCreditPanel } = usePermissions(user);
  const canLoanApply = canAccessCreditPanel("loanApply");
  const canApprove = canAccessCreditPanel("approvalFlow");

  const items = [
    ...(canLoanApply
      ? [{ key: "loanApply", label: "贷款申请", children: <LoansPage token={token} /> }]
      : []),
    ...(canApprove
      ? [{ key: "approvalFlow", label: "审批流程", children: <ApprovalsPage token={token} /> }]
      : [])
  ];

  if (items.length === 0) {
    return null;
  }

  return <Tabs defaultActiveKey={items[0]?.key} items={items} />;
}
