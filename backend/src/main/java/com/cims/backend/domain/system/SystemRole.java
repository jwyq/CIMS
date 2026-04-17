package com.cims.backend.domain.system;

public class SystemRole {

    private final Long id;
    private final String roleCode;
    private String roleName;
    private String description;
    private String scopeType;
    private boolean enabled;

    public SystemRole(Long id, String roleCode, String roleName, String description, String scopeType, boolean enabled) {
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

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
