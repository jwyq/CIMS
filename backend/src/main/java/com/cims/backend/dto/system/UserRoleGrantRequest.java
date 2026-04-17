package com.cims.backend.dto.system;

import javax.validation.constraints.NotNull;
import java.util.List;

public class UserRoleGrantRequest {

    @NotNull
    private List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
