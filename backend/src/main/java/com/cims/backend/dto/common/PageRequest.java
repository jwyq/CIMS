package com.cims.backend.dto.common;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 列表分页请求公共字段（与文档 {@code docs/api-pagination-convention.md} 一致）。
 * 业务 DTO 可继承此类，或复制字段并委托 {@link #resolvePage()} / {@link #resolvePageSize()}。
 */
public class PageRequest {

    /**
     * 当前页码，从 1 开始；对应前端 Table pagination.current。
     */
    @Min(1)
    private Integer page;

    /**
     * 每页条数；建议后端上限 200。
     */
    @Min(1)
    @Max(200)
    private Integer pageSize;

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public int resolvePage() {
        return page == null ? 1 : page;
    }

    public int resolvePageSize() {
        return pageSize == null ? 10 : pageSize;
    }

    public int resolveOffset() {
        int p = resolvePage();
        int ps = resolvePageSize();
        return (p - 1) * ps;
    }
}
