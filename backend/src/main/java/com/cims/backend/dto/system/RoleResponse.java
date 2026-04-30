package com.cims.backend.dto.system;

public class RoleResponse {

    private final Long id;
    private final String roleCode;
    private final String roleName;
    private final String description;
    private final String scopeType;
    private final boolean enabled;

    public RoleResponse(Long id, String roleCode, String roleName, String description, String scopeType, boolean enabled) {
        this.id = id;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.description = description;
        this.scopeType = scopeType;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }

    public String getScopeType() {
        return scopeType;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
