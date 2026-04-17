package com.cims.backend.dto.system;

import javax.validation.constraints.NotBlank;

public class RoleUpdateRequest {

    @NotBlank
    private String roleName;
    private String description;
    @NotBlank
    private String scopeType;
    private Boolean enabled;

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
