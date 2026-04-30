package com.cims.backend.dto.system;

import javax.validation.constraints.NotNull;
import java.util.List;

public class RoleResourceGrantCommand {

    @NotNull
    private Long roleId;

    @NotNull
    private List<Long> resourceIds;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }
}
