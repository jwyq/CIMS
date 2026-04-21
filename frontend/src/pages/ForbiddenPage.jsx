import { Result } from "antd";

export default function ForbiddenPage() {
  return <Result status="403" title="Forbidden" subTitle="You do not have permission to access this page." />;
}
