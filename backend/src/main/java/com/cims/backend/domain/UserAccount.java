package com.cims.backend.domain;

import java.util.Collections;
import java.util.List;

public class UserAccount {

    private final Long userId;
    private final String username;
    private final String displayName;
    /** 所属管理机构（sys_org.id），可为空 */
    private final Long orgId;
    private final String password;
    private final List<String> roles;

    public UserAccount(Long userId, String username, String displayName, Long orgId, String password, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.orgId = orgId;
        this.password = password;
        this.roles = Collections.unmodifiableList(roles);
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getOrgId() {
        return orgId;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }
}
