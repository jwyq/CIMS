package com.cims.backend.mapper.approval;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cims.backend.entity.approval.ApprovalTaskEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApprovalTaskMapper extends BaseMapper<ApprovalTaskEntity> {
}
