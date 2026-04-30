package com.cims.backend.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cims.backend.entity.system.SysResourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysResourceMapper extends BaseMapper<SysResourceEntity> {

    List<String> selectResourceCodesByRoleCodes(@Param("roleCodes") List<String> roleCodes);

    List<String> selectApiCodesByButtonCode(@Param("buttonCode") String buttonCode);
}
