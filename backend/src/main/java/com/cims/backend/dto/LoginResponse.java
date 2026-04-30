package com.cims.backend.dto;

import java.util.List;

/**
 * 登录成功响应：用户标识、展示名、访问令牌、以及由角色授权聚合得到的资源编码列表。
 */
public class LoginResponse {

    private final Long userId;
    private final String username;
    private final String displayName;
    /** 当前用户所属管理机构（sys_org.id），可为空 */
    private final Long orgId;
    private final String token;
    private final List<String> resourceCodes;

    public LoginResponse(
        Long userId,
        String username,
        String displayName,
        Long orgId,
        String token,
        List<String> resourceCodes
    ) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.orgId = orgId;
        this.token = token;
        this.resourceCodes = resourceCodes;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public String getToken() {
        return token;
    }

    public List<String> getResourceCodes() {
        return resourceCodes;
    }
}
