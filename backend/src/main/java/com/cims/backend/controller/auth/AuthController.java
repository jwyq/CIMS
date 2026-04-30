package com.cims.backend.controller.auth;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description 认证相关接口控制器，提供登录能力
 */

import com.cims.backend.dto.ApiResponse;
import com.cims.backend.dto.LoginRequest;
import com.cims.backend.dto.LoginResponse;
import com.cims.backend.service.auth.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 认证相关 HTTP 接口（登录等）。
 */
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录；密码不在日志中输出。
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("POST /api/auth/login username={}", loginRequest.getUsername());
        LoginResponse loginResponse = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ApiResponse.success(loginResponse);
    }
}
