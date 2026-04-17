package com.cims.backend.dto.system;

import javax.validation.constraints.NotNull;
import java.util.List;

public class UserRoleGrantCommand {

    @NotNull
    private Long userId;

    @NotNull
    private List<Long> roleIds;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
