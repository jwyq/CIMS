package com.cims.backend.repository;

import com.cims.backend.domain.UserAccount;
import com.cims.backend.entity.system.SysUserEntity;
import com.cims.backend.mapper.system.SysUserMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 登录用户装载：按用户名查启用用户并附带其角色编码列表（用于后续聚合资源码）。无业务日志。
 */
@Repository
public class UserRepository {

    private final SysUserMapper sysUserMapper;

    public UserRepository(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    /**
     * 根据用户名查找账户；不存在或已停用则返回 empty。
     *
     * @param username 登录名
     */
    public Optional<UserAccount> findByUsername(String username) {
        SysUserEntity user = sysUserMapper.selectActiveByUsername(username);
        if (user == null) {
            return Optional.empty();
        }
        List<String> roles = sysUserMapper.selectRoleCodesByUserId(user.getId());
        return Optional.of(new UserAccount(
            user.getId(),
            user.getUsername(),
            user.getDisplayName(),
            user.getOrgId(),
            user.getPasswordHash(),
            roles
        ));
    }
}
