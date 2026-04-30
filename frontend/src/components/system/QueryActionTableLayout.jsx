import "../../styles/system/query-action-table-layout.css";
import "../../styles/system/query-action-table-layout-lc.css";

/**
 * 管理页通用布局：上方查询区、中间操作按钮、下方表格/内容。
 *
 * @param {React.ReactNode} [filter] 查询表单或任意节点（建议 Form inline）
 * @param {React.ReactNode} [actions] 工具栏按钮区
 * @param {React.ReactNode} children 一般为 Table
 * @param {string} [className] 追加到根节点
 * @param {"default"|"lc"} [variant] lc = 紧凑查询列表（白底卡片 + 灰表头 + 斑马纹）
 */
export default function QueryActionTableLayout({ filter, actions, children, className, variant = "default" }) {
  const rootClass = ["qat-root", variant === "lc" && "lc-query-page", className].filter(Boolean).join(" ");

  const sections = (
    <>
      {filter != null ? <section className="qat-filter">{filter}</section> : null}
      {actions != null ? <section className="qat-actions">{actions}</section> : null}
      <section className="qat-table">{children}</section>
    </>
  );

  return (
    <div className={rootClass}>
      {variant === "lc" ? <div className="lc-query-page__inner">{sections}</div> : sections}
    </div>
  );
}
