/**
 * 与后端 {@code docs/api-pagination-convention.md}、{@code PageRequest}/{@code PageResult} 对齐。
 */

export const DEFAULT_PAGE_SIZE = 10;
export const MAX_PAGE_SIZE = 200;

/**
 * @param {number|undefined|null} page
 * @param {number|undefined|null} pageSize
 * @returns {{ page: number, pageSize: number }}
 */
export function normalizePageRequest(page, pageSize) {
  const p = Number.isFinite(Number(page)) && Number(page) >= 1 ? Math.floor(Number(page)) : 1;
  let ps = Number.isFinite(Number(pageSize)) ? Math.floor(Number(pageSize)) : DEFAULT_PAGE_SIZE;
  if (ps < 1) ps = 1;
  if (ps > MAX_PAGE_SIZE) ps = MAX_PAGE_SIZE;
  return { page: p, pageSize: ps };
}

/**
 * Ant Design Table 分页对象 → 请求体字段
 * @param {{ current?: number, pageSize?: number }} antdPagination
 * @returns {{ page: number, pageSize: number }}
 */
export function fromAntdPagination(antdPagination) {
  const current = antdPagination?.current ?? antdPagination?.page;
  return normalizePageRequest(current, antdPagination?.pageSize);
}

/**
 * 将 PageResult 映射为 Ant Design Table 的 pagination（受控）
 * @param {{ list?: unknown[], total?: number, page?: number, pageSize?: number, pages?: number }} pageResult
 * @param {{ current?: number, pageSize?: number }} [prev]
 * @returns {{ current: number, pageSize: number, total: number }}
 */
export function toAntdPagination(pageResult, prev = {}) {
  const total = Number(pageResult?.total) || 0;
  const pageSize = pageResult?.pageSize ?? prev.pageSize ?? DEFAULT_PAGE_SIZE;
  const page = pageResult?.page ?? prev.current ?? 1;
  return {
    current: page,
    pageSize,
    total
  };
}
