package com.cims.backend.service.system;

import com.cims.backend.domain.system.SystemResource;
import com.cims.backend.domain.system.SystemRole;
import com.cims.backend.dto.system.ResourceTreeNodeResponse;
import com.cims.backend.dto.system.RoleCreateRequest;
import com.cims.backend.dto.system.RoleResponse;
import com.cims.backend.dto.system.RoleUpdateRequest;
import com.cims.backend.dto.system.SystemUserResponse;
import com.cims.backend.dto.system.UserUpdateCommand;
import com.cims.backend.repository.system.SystemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统管理领域服务：角色 CRUD、资源树、角色-资源授权、用户-角色授权，以及按角色码聚合资源码（供登录发放）。
 */
@Service
public class SystemService {

    private static final Logger log = LoggerFactory.getLogger(SystemService.class);

    private final SystemRepository systemRepository;

    public SystemService(SystemRepository systemRepository) {
        this.systemRepository = systemRepository;
    }

    /**
     * 列出全部角色（按 id 排序）。
     */
    public List<RoleResponse> listRoles() {
        return systemRepository.findAllRoles().stream()
            .sorted(Comparator.comparing(SystemRole::getId))
            .map(this::toRoleResponse)
            .collect(Collectors.toList());
    }

    /**
     * 新建角色；角色编码唯一。
     */
    public RoleResponse createRole(RoleCreateRequest request) {
        systemRepository.findRoleByCode(request.getRoleCode())
            .ifPresent(role -> {
                log.warn("createRole rejected: duplicate roleCode={}", request.getRoleCode());
                throw new IllegalStateException("Role code already exists");
            });
        Long newRoleId = systemRepository.nextRoleId();
        SystemRole newRole = new SystemRole(
            newRoleId,
            request.getRoleCode(),
            request.getRoleName(),
            request.getDescription(),
            request.getScopeType(),
            true
        );
        RoleResponse saved = toRoleResponse(systemRepository.saveRole(newRole));
        log.info("createRole success: roleId={}, roleCode={}", saved.getId(), saved.getRoleCode());
        return saved;
    }

    /**
     * 更新角色展示名、描述、数据范围、启用状态等。
     */
    /**
     * 删除角色（含关联数据清理）。
     */
    public void deleteRole(Long roleId) {
        systemRepository.findRoleById(roleId)
            .orElseThrow(() -> {
                log.warn("deleteRole failed: role not found roleId={}", roleId);
                return new IllegalStateException("Role not found");
            });
        systemRepository.deleteRoleById(roleId);
        log.info("deleteRole success: roleId={}", roleId);
    }

    public RoleResponse updateRole(Long roleId, RoleUpdateRequest request) {
        SystemRole role = systemRepository.findRoleById(roleId)
            .orElseThrow(() -> {
                log.warn("updateRole failed: role not found roleId={}", roleId);
                return new IllegalStateException("Role not found");
            });
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setScopeType(request.getScopeType());
        if (request.getEnabled() != null) {
            role.setEnabled(request.getEnabled());
        }
        RoleResponse saved = toRoleResponse(systemRepository.saveRole(role));
        log.info("updateRole success: roleId={}", roleId);
        return saved;
    }

    /**
     * 构建资源树（根节点排序，孤儿节点挂到根）。
     */
    public List<ResourceTreeNodeResponse> resourceTree() {
        List<SystemResource> resources = systemRepository.findAllResources();
        Map<Long, ResourceTreeNodeResponse> map = new HashMap<Long, ResourceTreeNodeResponse>();
        List<ResourceTreeNodeResponse> roots = new ArrayList<ResourceTreeNodeResponse>();

        for (SystemResource resource : resources) {
            map.put(resource.getId(), new ResourceTreeNodeResponse(
                resource.getId(),
                resource.getParentId(),
                resource.getResourceType(),
                resource.getResourceCode(),
                resource.getResourceName(),
                resource.getSortNo(),
                resolveBindingApiCodes(resource)
            ));
        }

        for (ResourceTreeNodeResponse node : map.values()) {
            if (node.getParentId() == null) {
                roots.add(node);
                continue;
            }
            ResourceTreeNodeResponse parent = map.get(node.getParentId());
            if (parent != null) {
                parent.getChildren().add(node);
            } else {
                roots.add(node);
            }
        }

        sortResourceTree(roots);
        return roots;
    }

    /**
     * 按 sort_no、id 递归排序，保证菜单→页面→按钮→API 与种子顺序一致。
     */
    private void sortResourceTree(List<ResourceTreeNodeResponse> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        nodes.sort(Comparator
            .comparing(ResourceTreeNodeResponse::getSortNo, Comparator.nullsLast(Integer::compareTo))
            .thenComparing(ResourceTreeNodeResponse::getId));
        for (ResourceTreeNodeResponse n : nodes) {
            sortResourceTree(n.getChildren());
        }
    }

    /**
     * 查询角色已授权的资源主键列表。
     */
    public List<Long> roleResourceIds(Long roleId) {
        systemRepository.findRoleById(roleId)
            .orElseThrow(() -> {
                log.warn("roleResourceIds: role not found roleId={}", roleId);
                return new IllegalStateException("Role not found");
            });
        Set<Long> ids = systemRepository.findResourceIdsByRoleId(roleId);
        return ids.stream().sorted().collect(Collectors.toList());
    }

    /**
     * 覆盖写入角色与资源的绑定关系。
     */
    public void grantRoleResources(Long roleId, List<Long> resourceIds) {
        systemRepository.findRoleById(roleId)
            .orElseThrow(() -> {
                log.warn("grantRoleResources: role not found roleId={}", roleId);
                return new IllegalStateException("Role not found");
            });
        systemRepository.saveRoleResources(roleId, resourceIds);
        log.info("grantRoleResources done: roleId={}, resourceCount={}", roleId, resourceIds != null ? resourceIds.size() : 0);
    }

    /**
     * 列出用户及每个用户已绑角色 id（用于管理界面）。
     */
    public List<SystemUserResponse> listUsers() {
        return systemRepository.listAllUsersOrdered().stream()
            .map(user -> {
                List<Long> roleIds = systemRepository.findRoleIdsByUserId(user.getId()).stream()
                    .sorted()
                    .collect(Collectors.toList());
                boolean enabled = user.getStatus() != null && user.getStatus() == 1;
                return new SystemUserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getDisplayName(),
                    enabled,
                    roleIds
                );
            })
            .sorted(Comparator.comparing(SystemUserResponse::getId))
            .collect(Collectors.toList());
    }

    /**
     * 查询用户已绑定的角色 id 列表。
     */
    public List<Long> userRoleIds(Long userId) {
        systemRepository.findUserEntityById(userId)
            .orElseThrow(() -> {
                log.warn("userRoleIds: user not found userId={}", userId);
                return new IllegalStateException("User not found");
            });
        return systemRepository.findRoleIdsByUserId(userId).stream().sorted().collect(Collectors.toList());
    }

    /**
     * 覆盖写入用户与角色的绑定关系。
     */
    public void grantUserRoles(Long userId, List<Long> roleIds) {
        systemRepository.findUserEntityById(userId)
            .orElseThrow(() -> {
                log.warn("grantUserRoles: user not found userId={}", userId);
                return new IllegalStateException("User not found");
            });
        for (Long roleId : roleIds) {
            systemRepository.findRoleById(roleId)
                .orElseThrow(() -> {
                    log.warn("grantUserRoles: role not found roleId={}", roleId);
                    return new IllegalStateException("Role not found: " + roleId);
                });
        }
        systemRepository.saveUserRoles(userId, roleIds);
        log.info("grantUserRoles done: userId={}, roleCount={}", userId, roleIds != null ? roleIds.size() : 0);
    }

    /**
     * 更新用户展示名与启用状态。
     */
    public void updateUser(UserUpdateCommand cmd) {
        systemRepository.findUserEntityById(cmd.getUserId())
            .orElseThrow(() -> {
                log.warn("updateUser: user not found userId={}", cmd.getUserId());
                return new IllegalStateException("User not found");
            });
        systemRepository.updateUserDisplayAndStatus(cmd.getUserId(), cmd.getDisplayName(), cmd.getEnabled());
        log.info("updateUser success: userId={}", cmd.getUserId());
    }

    /**
     * 根据角色编码列表聚合去重后的资源编码（登录签发 JWT 与 resourceCodes 使用）。
     */
    public List<String> resourceCodesByRoleCodes(List<String> roleCodes) {
        return systemRepository.findResourceCodesByRoleCodes(roleCodes);
    }

    /**
     * 领域对象转 API 响应。
     */
    private RoleResponse toRoleResponse(SystemRole role) {
        return new RoleResponse(
            role.getId(),
            role.getRoleCode(),
            role.getRoleName(),
            role.getDescription(),
            role.getScopeType(),
            role.isEnabled()
        );
    }

    /**
     * 对按钮类型资源解析绑定的 API 编码列表（供树节点展示）。
     */
    private List<String> resolveBindingApiCodes(SystemResource resource) {
        if (!"BUTTON".equalsIgnoreCase(resource.getResourceType())) {
            return new ArrayList<String>();
        }
        List<String> mapped = systemRepository.findApiCodesByButtonCode(resource.getResourceCode());
        return mapped == null ? new ArrayList<String>() : new ArrayList<String>(mapped);
    }
}
