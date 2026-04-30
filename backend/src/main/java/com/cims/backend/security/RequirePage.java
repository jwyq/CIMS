package com.cims.backend.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明访问当前接口所需的<strong>页面资源码</strong>（与 {@code sys_resource.resource_code} 及 JWT 中 authorities 一致）。
 * 由 {@link PermissionGuardAspect} 在方法执行前校验。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePage {

    /**
     * @return 必须具备的资源编码，例如 {@code page:system:home}
     */
    String value();
}
