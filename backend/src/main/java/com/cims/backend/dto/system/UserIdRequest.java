package com.cims.backend.dto.system;

import javax.validation.constraints.NotNull;

public class UserIdRequest {

    @NotNull
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
