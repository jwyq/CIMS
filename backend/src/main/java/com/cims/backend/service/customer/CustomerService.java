package com.cims.backend.service.customer;

import com.cims.backend.domain.customer.Customer;
import com.cims.backend.domain.customer.CustomerBasicInfoHistory;
import com.cims.backend.dto.CustomerBasicInfoUpdateRequest;
import com.cims.backend.dto.CustomerRegisterRequest;
import com.cims.backend.entity.system.SysUserEntity;
import com.cims.backend.mapper.system.SysUserMapper;
import com.cims.backend.repository.customer.CustomerRepository;
import com.cims.backend.service.reference.ReferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final ReferenceService referenceService;
    private final SysUserMapper sysUserMapper;

    public CustomerService(
        CustomerRepository customerRepository,
        ReferenceService referenceService,
        SysUserMapper sysUserMapper
    ) {
        this.customerRepository = customerRepository;
        this.referenceService = referenceService;
        this.sysUserMapper = sysUserMapper;
    }

    public List<Customer> queryCustomers(String name, String customerNo, String idType, String idNo) {
        List<Customer> list = customerRepository.queryCustomers(name, customerNo, idType, idNo);
        enrichCustomers(list);
        return list;
    }

    public Customer getCustomerDetail(Long customerId) {
        Customer c = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        enrichCustomer(c);
        return c;
    }

    public Customer registerCustomer(CustomerRegisterRequest request, Long operatorUserId) {
        SysUserEntity operator = sysUserMapper.selectById(operatorUserId);
        if (operator == null) {
            throw new IllegalArgumentException("Operator not found");
        }
        Customer customer = new Customer();
        customer.setCustomerNo("CUST" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        customer.setName(request.getName());
        customer.setIdType(request.getIdType());
        customer.setIdNo(request.getIdNo());
        customer.setIdValidUntil(request.getIdValidUntil());
        customer.setMobile(request.getMobile());
        customer.setEmail(request.getEmail());
        customer.setMaritalStatus(request.getMaritalStatus());
        customer.setEducationLevel(request.getEducationLevel());
        customer.setOccupation(request.getOccupation());
        customer.setEmployerName(request.getEmployerName());
        customer.setAnnualIncome(request.getAnnualIncome());
        customer.setContactAddress(request.getContactAddress());
        customer.setRiskLevel(request.getRiskLevel());
        customer.setRemark(request.getRemark());
        customer.setStatus("ACTIVE");
        customer.setOrgId(operator.getOrgId());
        customer.setDeptId(operator.getDeptId());
        customer.setManagerUserId(operatorUserId);
        customer.setCreatedBy(operatorUserId);
        Customer saved = customerRepository.save(customer);

        CustomerBasicInfoHistory history = new CustomerBasicInfoHistory();
        history.setCustomerId(saved.getId());
        history.setChangeType("REGISTER");
        history.setBeforeSnapshot("{}");
        history.setAfterSnapshot(buildSnapshot(saved));
        history.setChangedBy(operatorUserId);
        customerRepository.insertHistory(history);
        log.info("Registered customer id={}, customerNo={}", saved.getId(), saved.getCustomerNo());
        enrichCustomer(saved);
        return saved;
    }

    public Customer updateBasicInfo(Long customerId, CustomerBasicInfoUpdateRequest request, Long operatorUserId) {
        Customer existing = getCustomerDetail(customerId);
        String before = buildSnapshot(existing);

        if (request.getName() != null) { existing.setName(request.getName()); }
        if (request.getIdType() != null) { existing.setIdType(request.getIdType()); }
        if (request.getIdNo() != null) { existing.setIdNo(request.getIdNo()); }
        if (request.getIdValidUntil() != null) { existing.setIdValidUntil(request.getIdValidUntil()); }
        if (request.getMobile() != null) { existing.setMobile(request.getMobile()); }
        if (request.getEmail() != null) { existing.setEmail(request.getEmail()); }
        if (request.getMaritalStatus() != null) { existing.setMaritalStatus(request.getMaritalStatus()); }
        if (request.getEducationLevel() != null) { existing.setEducationLevel(request.getEducationLevel()); }
        if (request.getOccupation() != null) { existing.setOccupation(request.getOccupation()); }
        if (request.getEmployerName() != null) { existing.setEmployerName(request.getEmployerName()); }
        if (request.getAnnualIncome() != null) { existing.setAnnualIncome(request.getAnnualIncome()); }
        if (request.getContactAddress() != null) { existing.setContactAddress(request.getContactAddress()); }
        if (request.getRiskLevel() != null) { existing.setRiskLevel(request.getRiskLevel()); }
        if (request.getRemark() != null) { existing.setRemark(request.getRemark()); }
        if (request.getStatus() != null) { existing.setStatus(request.getStatus()); }

        Customer saved = customerRepository.save(existing);
        CustomerBasicInfoHistory history = new CustomerBasicInfoHistory();
        history.setCustomerId(saved.getId());
        history.setChangeType("UPDATE_BASIC_INFO");
        history.setBeforeSnapshot(before);
        history.setAfterSnapshot(buildSnapshot(saved));
        history.setChangedBy(operatorUserId);
        customerRepository.insertHistory(history);
        log.info("Updated customer basic info id={}", saved.getId());
        enrichCustomer(saved);
        return saved;
    }

    public List<CustomerBasicInfoHistory> queryBasicInfoHistory(Long customerId) {
        return customerRepository.listHistory(customerId);
    }

    private String buildSnapshot(Customer c) {
        return String.format("{\"name\":\"%s\",\"idNo\":\"%s\",\"mobile\":\"%s\",\"riskLevel\":\"%s\",\"status\":\"%s\"}",
            safe(c.getName()), safe(c.getIdNo()), safe(c.getMobile()), safe(c.getRiskLevel()), safe(c.getStatus()));
    }

    private String safe(String v) {
        return v == null ? "" : v.replace("\"", "");
    }

    private void enrichCustomer(Customer c) {
        if (c == null) {
            return;
        }
        if (c.getIdType() != null) {
            String zh = referenceService.findDictLabel(ReferenceService.DICT_ID_TYPE, c.getIdType());
            c.setIdTypeLabelZh(zh != null ? zh : c.getIdType());
        }
        if (c.getOrgId() != null) {
            c.setMgmtOrgName(referenceService.findOrgName(c.getOrgId()));
        }
        if (c.getManagerUserId() != null) {
            c.setManagerDisplayName(referenceService.findUserDisplayName(c.getManagerUserId()));
        }
    }

    private void enrichCustomers(List<Customer> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Set<String> idTypes = list.stream().map(Customer::getIdType).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<String, String> idTypeLabels = referenceService.labelsByCodes(ReferenceService.DICT_ID_TYPE, idTypes);
        Set<Long> orgIds = list.stream().map(Customer::getOrgId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> orgNames = referenceService.orgNamesByIds(orgIds);
        Set<Long> mgrIds = list.stream().map(Customer::getManagerUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> mgrNames = referenceService.userDisplayNamesByIds(mgrIds);
        for (Customer c : list) {
            if (c.getIdType() != null) {
                String zh = idTypeLabels.get(c.getIdType());
                c.setIdTypeLabelZh(zh != null ? zh : c.getIdType());
            }
            if (c.getOrgId() != null) {
                c.setMgmtOrgName(orgNames.get(c.getOrgId()));
            }
            if (c.getManagerUserId() != null) {
                c.setManagerDisplayName(mgrNames.get(c.getManagerUserId()));
            }
        }
    }
}
