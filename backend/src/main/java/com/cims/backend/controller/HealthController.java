package com.cims.backend.controller;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description 健康检查接口控制器
 */

import com.cims.backend.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private static final String HEALTH_UP = "UP";

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success(HEALTH_UP);
    }
}
