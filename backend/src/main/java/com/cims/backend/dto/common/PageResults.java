package com.cims.backend.dto.common;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Collections;

/**
 * 将 MyBatis-Plus {@link com.baomidou.mybatisplus.core.metadata.IPage} 转为对外 {@link PageResult}。
 */
public final class PageResults {

    private PageResults() {
    }

    public static <T> PageResult<T> from(IPage<T> page) {
        if (page == null) {
            return PageResult.of(Collections.emptyList(), 0L, 1, 10);
        }
        return PageResult.of(
            page.getRecords(),
            page.getTotal(),
            (int) page.getCurrent(),
            (int) page.getSize()
        );
    }
}
