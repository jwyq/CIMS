package com.cims.backend.domain.customer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Customer {
    private Long id;
    private String customerNo;
    private String name;
    private String idType;
    private String idNo;
    private LocalDate idValidUntil;
    private String mobile;
    private String email;
    private String maritalStatus;
    private String educationLevel;
    private String occupation;
    private String employerName;
    private BigDecimal annualIncome;
    private String contactAddress;
    private String riskLevel;
    private String remark;
    private String status;
    /** 管理机构 sys_org.id */
    private Long orgId;
    private Long deptId;
    /** 主办客户经理 sys_user.id */
    private Long managerUserId;
    /** 注册/创建操作人 */
    private Long createdBy;

    /** 展示：证件类型中文（不入库） */
    private String idTypeLabelZh;
    /** 展示：管理机构名称（不入库） */
    private String mgmtOrgName;
    /** 展示：主办客户经理姓名（不入库） */
    private String managerDisplayName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerNo() { return customerNo; }
    public void setCustomerNo(String customerNo) { this.customerNo = customerNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }
    public String getIdNo() { return idNo; }
    public void setIdNo(String idNo) { this.idNo = idNo; }
    public LocalDate getIdValidUntil() { return idValidUntil; }
    public void setIdValidUntil(LocalDate idValidUntil) { this.idValidUntil = idValidUntil; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getEmployerName() { return employerName; }
    public void setEmployerName(String employerName) { this.employerName = employerName; }
    public BigDecimal getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; }
    public String getContactAddress() { return contactAddress; }
    public void setContactAddress(String contactAddress) { this.contactAddress = contactAddress; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getOrgId() { return orgId; }
    public void setOrgId(Long orgId) { this.orgId = orgId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getManagerUserId() { return managerUserId; }
    public void setManagerUserId(Long managerUserId) { this.managerUserId = managerUserId; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public String getIdTypeLabelZh() { return idTypeLabelZh; }
    public void setIdTypeLabelZh(String idTypeLabelZh) { this.idTypeLabelZh = idTypeLabelZh; }
    public String getMgmtOrgName() { return mgmtOrgName; }
    public void setMgmtOrgName(String mgmtOrgName) { this.mgmtOrgName = mgmtOrgName; }
    public String getManagerDisplayName() { return managerDisplayName; }
    public void setManagerDisplayName(String managerDisplayName) { this.managerDisplayName = managerDisplayName; }
}
