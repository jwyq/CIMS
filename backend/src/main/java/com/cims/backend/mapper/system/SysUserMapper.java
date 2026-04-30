package com.cims.backend.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cims.backend.entity.system.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {

    SysUserEntity selectActiveByUsername(@Param("username") String username);

    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}
