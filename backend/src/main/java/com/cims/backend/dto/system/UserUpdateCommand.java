package com.cims.backend.dto.system;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserUpdateCommand {

    @NotNull
    private Long userId;

    @NotNull
    @Size(max = 128)
    private String displayName;

    @NotNull
    private Boolean enabled;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
