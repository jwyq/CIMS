package com.cims.backend.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 基于方法注解 {@link RequirePage}、{@link RequireApi} 的资源码校验，与 JWT 中注入的 authorities 对齐。
 */
@Aspect
@Component
public class PermissionGuardAspect {

    private static final Logger log = LoggerFactory.getLogger(PermissionGuardAspect.class);

    /**
     * 页面级读类接口：要求当前主体具备给定资源码之一（与注解 value 完全一致）。
     */
    @Around("@annotation(requirePage)")
    public Object guardPage(ProceedingJoinPoint joinPoint, RequirePage requirePage) throws Throwable {
        validateAuthority(requirePage.value(), "RequirePage");
        return joinPoint.proceed();
    }

    /**
     * API/写类接口：要求当前主体具备给定资源码。
     */
    @Around("@annotation(requireApi)")
    public Object guardApi(ProceedingJoinPoint joinPoint, RequireApi requireApi) throws Throwable {
        validateAuthority(requireApi.value(), "RequireApi");
        return joinPoint.proceed();
    }

    /**
     * 在当前 SecurityContext 的 authorities 中查找与 {@code requiredCode} 匹配的项。
     */
    private void validateAuthority(String requiredCode, String source) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("{} denied: not authenticated, requiredCode={}", source, requiredCode);
            throw new AccessDeniedException("Authentication required");
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (requiredCode.equals(authority.getAuthority())) {
                return;
            }
        }
        log.warn("{} denied: missing authority, requiredCode={}, user={}",
            source, requiredCode, authentication.getName());
        throw new AccessDeniedException("No permission");
    }
}
