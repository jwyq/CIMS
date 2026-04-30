package com.cims.backend.dto.reference;

public class DictEntryResponse {
    private String code;
    private String labelZh;
    private Integer sortNo;

    public DictEntryResponse() {
    }

    public DictEntryResponse(String code, String labelZh, Integer sortNo) {
        this.code = code;
        this.labelZh = labelZh;
        this.sortNo = sortNo;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabelZh() {
        return labelZh;
    }

    public void setLabelZh(String labelZh) {
        this.labelZh = labelZh;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }
}
