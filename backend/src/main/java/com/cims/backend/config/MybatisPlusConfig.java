package com.cims.backend.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Mapper 扫描；并注册 MyBatis-Plus 分页插件（{@code Page} + {@code selectPage} 生效）。
 */
@Configuration
@MapperScan("com.cims.backend.mapper")
public class MybatisPlusConfig {

    /**
     * 物理分页：与 {@code Page} + {@code Mapper#selectPage} 配合，自动追加 limit 与 count。
     * {@code maxLimit} 与 {@link com.cims.backend.dto.common.PageRequest} 的 pageSize 上限一致。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor page = new PaginationInnerInterceptor(DbType.MYSQL);
        page.setMaxLimit(200L);
        interceptor.addInnerInterceptor(page);
        return interceptor;
    }
}
