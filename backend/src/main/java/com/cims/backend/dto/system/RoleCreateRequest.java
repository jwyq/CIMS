package com.cims.backend.dto.system;

import javax.validation.constraints.NotBlank;

public class RoleCreateRequest {

    @NotBlank
    private String roleCode;
    @NotBlank
    private String roleName;
    private String description;
    @NotBlank
    private String scopeType;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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
}
