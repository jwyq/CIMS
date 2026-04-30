package com.cims.backend.dto.common;

import java.util.Collections;
import java.util.List;

/**
 * 分页响应体，作为 {@code ApiResponse.data}（与文档 {@code docs/api-pagination-convention.md} 一致）。
 */
public class PageResult<T> {

    private List<T> list;
    private long total;
    private int page;
    private int pageSize;

    public static <T> PageResult<T> of(List<T> list, long total, int page, int pageSize) {
        PageResult<T> r = new PageResult<>();
        r.setList(list != null ? list : Collections.emptyList());
        r.setTotal(total);
        r.setPage(page);
        r.setPageSize(pageSize);
        return r;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 总页数；total 为 0 时返回 0。
     */
    public int getPages() {
        if (pageSize <= 0) {
            return 0;
        }
        if (total <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / pageSize);
    }
}
