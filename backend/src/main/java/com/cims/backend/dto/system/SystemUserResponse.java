package com.cims.backend.dto.system;

import java.util.List;

public class SystemUserResponse {

    private final Long id;
    private final String username;
    private final String displayName;
    private final boolean enabled;
    private final List<Long> roleIds;

    public SystemUserResponse(Long id, String username, String displayName, boolean enabled, List<Long> roleIds) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.enabled = enabled;
        this.roleIds = roleIds;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }
}
