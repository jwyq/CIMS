package com.cims.backend.dto;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求体；密码仅用于传输与校验，禁止在日志中输出。
 */
public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
