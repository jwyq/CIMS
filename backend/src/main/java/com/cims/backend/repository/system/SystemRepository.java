package com.cims.backend.repository.system;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description 系统管理相关数据访问仓储
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cims.backend.domain.system.SystemResource;
import com.cims.backend.domain.system.SystemRole;
import com.cims.backend.entity.system.SysResourceEntity;
import com.cims.backend.entity.system.SysRoleEntity;
import com.cims.backend.entity.system.SysRoleResourceEntity;
import com.cims.backend.entity.system.SysUserEntity;
import com.cims.backend.entity.system.SysUserRoleEntity;
import com.cims.backend.mapper.system.SysResourceMapper;
import com.cims.backend.mapper.system.SysRoleMapper;
import com.cims.backend.mapper.system.SysRoleResourceMapper;
import com.cims.backend.mapper.system.SysUserMapper;
import com.cims.backend.mapper.system.SysUserRoleMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class SystemRepository {

    private static final int ENABLED_STATUS = 1;

    private final SysRoleMapper sysRoleMapper;
    private final SysResourceMapper sysResourceMapper;
    private final SysRoleResourceMapper sysRoleResourceMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    public SystemRepository(
        SysRoleMapper sysRoleMapper,
        SysResourceMapper sysResourceMapper,
        SysRoleResourceMapper sysRoleResourceMapper,
        SysUserMapper sysUserMapper,
        SysUserRoleMapper sysUserRoleMapper
    ) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysResourceMapper = sysResourceMapper;
        this.sysRoleResourceMapper = sysRoleResourceMapper;
        this.sysUserMapper = sysUserMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    public Collection<SystemRole> findAllRoles() {
        return sysRoleMapper.selectList(new LambdaQueryWrapper<SysRoleEntity>().orderByAsc(SysRoleEntity::getId))
            .stream()
            .map(this::toRole)
            .collect(Collectors.toList());
    }

    public Optional<SystemRole> findRoleById(Long roleId) {
        return Optional.ofNullable(sysRoleMapper.selectById(roleId)).map(this::toRole);
    }

    public Optional<SystemRole> findRoleByCode(String roleCode) {
        return Optional.ofNullable(
            sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRoleEntity>().eq(SysRoleEntity::getRoleCode, roleCode))
        ).map(this::toRole);
    }

    public SystemRole saveRole(SystemRole systemRole) {
        SysRoleEntity roleEntity = toRoleEntity(systemRole);
        if (roleEntity.getId() == null || sysRoleMapper.selectById(roleEntity.getId()) == null) {
            sysRoleMapper.insert(roleEntity);
            return new SystemRole(
                roleEntity.getId(),
                roleEntity.getRoleCode(),
                roleEntity.getRoleName(),
                roleEntity.getDescription(),
                roleEntity.getScopeType(),
                roleEntity.getStatus() != null && roleEntity.getStatus() == 1
            );
        } else {
            sysRoleMapper.updateById(roleEntity);
        }
        return systemRole;
    }

    public Long nextRoleId() {
        SysRoleEntity last = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRoleEntity>()
            .orderByDesc(SysRoleEntity::getId)
            .last("LIMIT 1"));
        return last == null || last.getId() == null ? 1L : last.getId() + 1L;
    }

    public List<SystemResource> findAllResources() {
        return sysResourceMapper.selectList(new LambdaQueryWrapper<SysResourceEntity>()
                .orderByAsc(SysResourceEntity::getSortNo)
                .orderByAsc(SysResourceEntity::getId))
            .stream()
            .map(resource -> new SystemResource(
                resource.getId(),
                resource.getParentId(),
                resource.getResourceType(),
                resource.getResourceCode(),
                resource.getResourceName(),
                resource.getSortNo()
            ))
            .collect(Collectors.toList());
    }

    public Set<Long> findResourceIdsByRoleId(Long roleId) {
        return sysRoleResourceMapper.selectList(
            new LambdaQueryWrapper<SysRoleResourceEntity>()
                .eq(SysRoleResourceEntity::getRoleId, roleId)
                .orderByAsc(SysRoleResourceEntity::getResourceId)
        ).stream().map(SysRoleResourceEntity::getResourceId).collect(Collectors.toSet());
    }

    public void saveRoleResources(Long roleId, List<Long> resourceIds) {
        sysRoleResourceMapper.delete(new LambdaQueryWrapper<SysRoleResourceEntity>().eq(SysRoleResourceEntity::getRoleId, roleId));
        if (resourceIds == null || resourceIds.isEmpty()) {
            return;
        }
        for (Long resourceId : resourceIds) {
            if (resourceId == null) {
                continue;
            }
            SysRoleResourceEntity entity = new SysRoleResourceEntity();
            entity.setRoleId(roleId);
            entity.setResourceId(resourceId);
            sysRoleResourceMapper.insert(entity);
        }
    }

    public List<SysUserEntity> listAllUsersOrdered() {
        return sysUserMapper.selectList(new LambdaQueryWrapper<SysUserEntity>().orderByAsc(SysUserEntity::getId));
    }

    public Optional<SysUserEntity> findUserEntityById(Long userId) {
        return Optional.ofNullable(sysUserMapper.selectById(userId));
    }

    public void updateUserDisplayAndStatus(Long userId, String displayName, boolean enabled) {
        SysUserEntity u = Optional.ofNullable(sysUserMapper.selectById(userId))
            .orElseThrow(() -> new IllegalStateException("User not found: " + userId));
        u.setDisplayName(displayName);
        u.setStatus(enabled ? ENABLED_STATUS : 0);
        sysUserMapper.updateById(u);
    }

    public Set<Long> findRoleIdsByUserId(Long userId) {
        return sysUserRoleMapper.selectList(
            new LambdaQueryWrapper<SysUserRoleEntity>()
                .eq(SysUserRoleEntity::getUserId, userId)
                .orderByAsc(SysUserRoleEntity::getRoleId)
        ).stream().map(SysUserRoleEntity::getRoleId).collect(Collectors.toSet());
    }

    public void saveUserRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRoleEntity>().eq(SysUserRoleEntity::getUserId, userId));
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (Long roleId : roleIds) {
            if (roleId == null) {
                continue;
            }
            SysUserRoleEntity userRole = new SysUserRoleEntity();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }
    }

    public List<String> findResourceCodesByRoleCodes(List<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> normalizedRoleCodes = roleCodes.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(code -> !code.isEmpty())
            .distinct()
            .collect(Collectors.toList());
        if (normalizedRoleCodes.isEmpty()) {
            return Collections.emptyList();
        }
        return sysResourceMapper.selectResourceCodesByRoleCodes(normalizedRoleCodes);
    }

    public List<String> findApiCodesByButtonCode(String buttonCode) {
        if (buttonCode == null || buttonCode.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return sysResourceMapper.selectApiCodesByButtonCode(buttonCode);
    }

    /**
     * 删除角色：先解除用户-角色、角色-资源关联，再删角色行。
     */
    public void deleteRoleById(Long roleId) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRoleEntity>().eq(SysUserRoleEntity::getRoleId, roleId));
        sysRoleResourceMapper.delete(new LambdaQueryWrapper<SysRoleResourceEntity>().eq(SysRoleResourceEntity::getRoleId, roleId));
        sysRoleMapper.deleteById(roleId);
    }

    private SystemRole toRole(SysRoleEntity roleEntity) {
        return new SystemRole(
            roleEntity.getId(),
            roleEntity.getRoleCode(),
            roleEntity.getRoleName(),
            roleEntity.getDescription(),
            roleEntity.getScopeType(),
            Objects.equals(roleEntity.getStatus(), ENABLED_STATUS)
        );
    }

    private SysRoleEntity toRoleEntity(SystemRole role) {
        SysRoleEntity entity = new SysRoleEntity();
        entity.setId(role.getId());
        entity.setRoleCode(role.getRoleCode());
        entity.setRoleName(role.getRoleName());
        entity.setDescription(role.getDescription());
        entity.setScopeType(role.getScopeType());
        entity.setStatus(role.isEnabled() ? ENABLED_STATUS : 0);
        return entity;
    }
}
