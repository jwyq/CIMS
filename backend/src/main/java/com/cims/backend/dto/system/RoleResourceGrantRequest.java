package com.cims.backend.dto.system;

import javax.validation.constraints.NotNull;
import java.util.List;

public class RoleResourceGrantRequest {

    @NotNull
    private List<Long> resourceIds;

    public List<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(List<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }
}
