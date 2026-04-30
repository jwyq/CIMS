package com.cims.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CustomerBasicInfoUpdateRequest {
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
}
