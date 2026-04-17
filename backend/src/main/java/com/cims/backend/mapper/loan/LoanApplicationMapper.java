package com.cims.backend.mapper.loan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cims.backend.entity.loan.LoanApplicationEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanApplicationMapper extends BaseMapper<LoanApplicationEntity> {
}
