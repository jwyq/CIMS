package com.cims.backend.dto.system;

import javax.validation.constraints.NotNull;

public class RoleIdRequest {

    @NotNull
    private Long roleId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
