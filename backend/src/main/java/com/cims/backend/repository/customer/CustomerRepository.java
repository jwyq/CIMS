package com.cims.backend.repository.customer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cims.backend.domain.customer.Customer;
import com.cims.backend.domain.customer.CustomerBasicInfoHistory;
import com.cims.backend.entity.customer.CustomerBasicInfoHistoryEntity;
import com.cims.backend.entity.customer.CustomerEntity;
import com.cims.backend.mapper.customer.CustomerBasicInfoHistoryMapper;
import com.cims.backend.mapper.customer.CustomerMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CustomerRepository {
    private final CustomerMapper customerMapper;
    private final CustomerBasicInfoHistoryMapper historyMapper;

    public CustomerRepository(CustomerMapper customerMapper, CustomerBasicInfoHistoryMapper historyMapper) {
        this.customerMapper = customerMapper;
        this.historyMapper = historyMapper;
    }

    public List<Customer> queryCustomers(String name, String customerNo, String idType, String idNo) {
        // 注意：方法实参会先全部求值，不能写 .like(cond, col, name.trim())，否则 name 为 null 时仍会对 trim() 求值导致 NPE
        String nameQ = blankToNull(name);
        String customerNoQ = blankToNull(customerNo);
        String idTypeQ = blankToNull(idType);
        String idNoQ = blankToNull(idNo);
        LambdaQueryWrapper<CustomerEntity> wrapper = new LambdaQueryWrapper<CustomerEntity>()
            .like(nameQ != null, CustomerEntity::getName, nameQ)
            .like(customerNoQ != null, CustomerEntity::getCustomerNo, customerNoQ)
            .eq(idTypeQ != null, CustomerEntity::getIdType, idTypeQ)
            .like(idNoQ != null, CustomerEntity::getIdNo, idNoQ)
            .orderByDesc(CustomerEntity::getId);
        return customerMapper.selectList(wrapper).stream().map(this::toDomain).collect(Collectors.toList());
    }

    /** null 或全空白视为「未传条件」 */
    private static String blankToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public Optional<Customer> findById(Long customerId) {
        return Optional.ofNullable(customerMapper.selectById(customerId)).map(this::toDomain);
    }

    public Customer save(Customer customer) {
        CustomerEntity entity = toEntity(customer);
        if (entity.getId() == null) {
            customerMapper.insert(entity);
            customer.setId(entity.getId());
        } else {
            customerMapper.updateById(entity);
        }
        return customer;
    }

    public void insertHistory(CustomerBasicInfoHistory history) {
        CustomerBasicInfoHistoryEntity entity = new CustomerBasicInfoHistoryEntity();
        entity.setCustomerId(history.getCustomerId());
        entity.setChangeType(history.getChangeType());
        entity.setBeforeSnapshot(history.getBeforeSnapshot());
        entity.setAfterSnapshot(history.getAfterSnapshot());
        entity.setChangedBy(history.getChangedBy());
        historyMapper.insert(entity);
    }

    public List<CustomerBasicInfoHistory> listHistory(Long customerId) {
        return historyMapper.selectList(new LambdaQueryWrapper<CustomerBasicInfoHistoryEntity>()
                .eq(CustomerBasicInfoHistoryEntity::getCustomerId, customerId)
                .orderByDesc(CustomerBasicInfoHistoryEntity::getId))
            .stream()
            .map(this::toHistoryDomain)
            .collect(Collectors.toList());
    }

    private Customer toDomain(CustomerEntity entity) {
        Customer customer = new Customer();
        customer.setId(entity.getId());
        customer.setCustomerNo(entity.getCustomerNo());
        customer.setName(entity.getName());
        customer.setIdType(entity.getIdType());
        customer.setIdNo(entity.getIdNo());
        customer.setIdValidUntil(entity.getIdValidUntil());
        customer.setMobile(entity.getMobile());
        customer.setEmail(entity.getEmail());
        customer.setMaritalStatus(entity.getMaritalStatus());
        customer.setEducationLevel(entity.getEducationLevel());
        customer.setOccupation(entity.getOccupation());
        customer.setEmployerName(entity.getEmployerName());
        customer.setAnnualIncome(entity.getAnnualIncome());
        customer.setContactAddress(entity.getContactAddress());
        customer.setRiskLevel(entity.getRiskLevel());
        customer.setRemark(entity.getRemark());
        customer.setStatus(entity.getStatus());
        customer.setOrgId(entity.getOrgId());
        customer.setDeptId(entity.getDeptId());
        customer.setManagerUserId(entity.getManagerUserId());
        customer.setCreatedBy(entity.getCreatedBy());
        return customer;
    }

    private CustomerEntity toEntity(Customer customer) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(customer.getId());
        entity.setCustomerNo(customer.getCustomerNo());
        entity.setName(customer.getName());
        entity.setIdType(customer.getIdType());
        entity.setIdNo(customer.getIdNo());
        entity.setIdValidUntil(customer.getIdValidUntil());
        entity.setMobile(customer.getMobile());
        entity.setEmail(customer.getEmail());
        entity.setMaritalStatus(customer.getMaritalStatus());
        entity.setEducationLevel(customer.getEducationLevel());
        entity.setOccupation(customer.getOccupation());
        entity.setEmployerName(customer.getEmployerName());
        entity.setAnnualIncome(customer.getAnnualIncome());
        entity.setContactAddress(customer.getContactAddress());
        entity.setRiskLevel(customer.getRiskLevel());
        entity.setRemark(customer.getRemark());
        entity.setStatus(customer.getStatus());
        entity.setOrgId(customer.getOrgId());
        entity.setDeptId(customer.getDeptId());
        entity.setManagerUserId(customer.getManagerUserId());
        entity.setCreatedBy(customer.getCreatedBy());
        return entity;
    }

    private CustomerBasicInfoHistory toHistoryDomain(CustomerBasicInfoHistoryEntity entity) {
        CustomerBasicInfoHistory history = new CustomerBasicInfoHistory();
        history.setId(entity.getId());
        history.setCustomerId(entity.getCustomerId());
        history.setChangeType(entity.getChangeType());
        history.setBeforeSnapshot(entity.getBeforeSnapshot());
        history.setAfterSnapshot(entity.getAfterSnapshot());
        history.setChangedBy(entity.getChangedBy());
        return history;
    }
}
