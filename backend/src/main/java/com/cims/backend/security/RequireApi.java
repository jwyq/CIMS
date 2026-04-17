package com.cims.backend.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明<strong>接口/写操作</strong>所需的 API 资源码，与 URL 级 {@code hasAuthority} 及 JWT 中 authorities 一致。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireApi {

    /**
     * @return 必须具备的资源编码，例如 {@code api:system:role:create}
     */
    String value();
}
