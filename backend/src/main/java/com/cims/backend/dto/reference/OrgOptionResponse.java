package com.cims.backend.dto.reference;

public class OrgOptionResponse {
    private Long id;
    private String orgCode;
    private String orgName;

    public OrgOptionResponse() {
    }

    public OrgOptionResponse(Long id, String orgCode, String orgName) {
        this.id = id;
        this.orgCode = orgCode;
        this.orgName = orgName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
