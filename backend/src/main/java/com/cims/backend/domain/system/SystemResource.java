package com.cims.backend.domain.system;

public class SystemResource {

    private final Long id;
    private final Long parentId;
    private final String resourceType;
    private final String resourceCode;
    private final String resourceName;
    private final Integer sortNo;

    public SystemResource(
        Long id,
        Long parentId,
        String resourceType,
        String resourceCode,
        String resourceName,
        Integer sortNo
    ) {
        this.id = id;
        this.parentId = parentId;
        this.resourceType = resourceType;
        this.resourceCode = resourceCode;
        this.resourceName = resourceName;
        this.sortNo = sortNo;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Integer getSortNo() {
        return sortNo;
    }
}
