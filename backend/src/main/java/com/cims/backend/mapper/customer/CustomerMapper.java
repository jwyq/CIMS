package com.cims.backend.mapper.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cims.backend.entity.customer.CustomerEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<CustomerEntity> {
}
