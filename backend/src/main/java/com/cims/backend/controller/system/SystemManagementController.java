package com.cims.backend.controller.system;

import com.cims.backend.dto.ApiResponse;
import com.cims.backend.dto.system.ResourceTreeNodeResponse;
import com.cims.backend.dto.system.RoleCreateRequest;
import com.cims.backend.dto.system.RoleIdRequest;
import com.cims.backend.dto.system.RoleResourceGrantCommand;
import com.cims.backend.dto.system.RoleResponse;
import com.cims.backend.dto.system.RoleUpdateCommand;
import com.cims.backend.dto.system.RoleUpdateRequest;
import com.cims.backend.dto.system.SystemUserResponse;
import com.cims.backend.dto.system.UserIdRequest;
import com.cims.backend.dto.system.UserRoleGrantCommand;
import com.cims.backend.dto.system.UserUpdateCommand;
import com.cims.backend.security.RequireApi;
import com.cims.backend.service.system.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统管理模块接口：角色、资源树、用户-角色授权等。
 */
@Validated
@RestController
@RequestMapping("/api/system")
public class SystemManagementController {

    private static final Logger log = LoggerFactory.getLogger(SystemManagementController.class);

    private final SystemService systemService;

    public SystemManagementController(SystemService systemService) {
        this.systemService = systemService;
    }

    /**
     * 查询全部角色列表。
     */
    @PostMapping("/queryRoles")
    public ApiResponse<List<RoleResponse>> queryRoles() {
        log.info("POST /api/system/queryRoles");
        return ApiResponse.success(systemService.listRoles());
    }

    /**
     * 创建角色。
     */
    @PostMapping("/createRole")
    @RequireApi("api:system:role:create")
    public ApiResponse<RoleResponse> createRole(@Valid @RequestBody RoleCreateRequest request) {
        log.info("POST /api/system/createRole roleCode={}", request.getRoleCode());
        return ApiResponse.success(systemService.createRole(request));
    }

    /**
     * 更新角色信息。
     */
    /**
     * 删除角色。
     */
    @PostMapping("/deleteRole")
    @RequireApi("api:system:role:update")
    public ApiResponse<Void> deleteRole(@Valid @RequestBody RoleIdRequest request) {
        log.info("POST /api/system/deleteRole roleId={}", request.getRoleId());
        systemService.deleteRole(request.getRoleId());
        return ApiResponse.successMessage("ok");
    }

    @PostMapping("/updateRole")
    @RequireApi("api:system:role:update")
    public ApiResponse<RoleResponse> updateRole(@Valid @RequestBody RoleUpdateCommand request) {
        log.info("POST /api/system/updateRole roleId={}", request.getRoleId());
        RoleUpdateRequest updateRequest = new RoleUpdateRequest();
        updateRequest.setRoleName(request.getRoleName());
        updateRequest.setDescription(request.getDescription());
        updateRequest.setScopeType(request.getScopeType());
        updateRequest.setEnabled(request.getEnabled());
        return ApiResponse.success(systemService.updateRole(request.getRoleId(), updateRequest));
    }

    /**
     * 查询资源树（菜单/页面/按钮/API 等）。
     */
    @PostMapping("/queryResourceTree")
    public ApiResponse<List<ResourceTreeNodeResponse>> queryResourceTree() {
        log.info("POST /api/system/queryResourceTree");
        return ApiResponse.success(systemService.resourceTree());
    }

    /**
     * 查询某角色已绑定的资源 ID 列表。
     */
    @PostMapping("/queryRoleResources")
    public ApiResponse<List<Long>> queryRoleResources(@Valid @RequestBody RoleIdRequest request) {
        log.info("POST /api/system/queryRoleResources roleId={}", request.getRoleId());
        return ApiResponse.success(systemService.roleResourceIds(request.getRoleId()));
    }

    /**
     * 为角色授权资源（覆盖写入）。
     */
    @PostMapping("/grantRoleResources")
    @RequireApi("api:system:role:grant")
    public ApiResponse<Void> grantRoleResources(@Valid @RequestBody RoleResourceGrantCommand request) {
        log.info("POST /api/system/grantRoleResources roleId={}, resourceIdCount={}",
            request.getRoleId(), request.getResourceIds() != null ? request.getResourceIds().size() : 0);
        systemService.grantRoleResources(request.getRoleId(), request.getResourceIds());
        return ApiResponse.successMessage("ok");
    }

    /**
     * 查询可用于授权的用户列表（含角色 ID）。
     */
    @PostMapping("/queryUsers")
    public ApiResponse<List<SystemUserResponse>> queryUsers() {
        log.info("POST /api/system/queryUsers");
        return ApiResponse.success(systemService.listUsers());
    }

    /**
     * 查询某用户已绑定的角色 ID 列表。
     */
    @PostMapping("/queryUserRoles")
    public ApiResponse<List<Long>> queryUserRoles(@Valid @RequestBody UserIdRequest request) {
        log.info("POST /api/system/queryUserRoles userId={}", request.getUserId());
        return ApiResponse.success(systemService.userRoleIds(request.getUserId()));
    }

    /**
     * 为用户授予角色（覆盖写入）。
     */
    @PostMapping("/grantUserRoles")
    @RequireApi("api:system:user:grant")
    public ApiResponse<Void> grantUserRoles(@Valid @RequestBody UserRoleGrantCommand request) {
        log.info("POST /api/system/grantUserRoles userId={}, roleIdCount={}",
            request.getUserId(), request.getRoleIds() != null ? request.getRoleIds().size() : 0);
        systemService.grantUserRoles(request.getUserId(), request.getRoleIds());
        return ApiResponse.successMessage("ok");
    }

    /**
     * 更新用户展示名与启用状态。
     */
    @PostMapping("/updateUser")
    @RequireApi("api:system:user:grant")
    public ApiResponse<Void> updateUser(@Valid @RequestBody UserUpdateCommand request) {
        log.info("POST /api/system/updateUser userId={}", request.getUserId());
        systemService.updateUser(request);
        return ApiResponse.successMessage("ok");
    }
}
