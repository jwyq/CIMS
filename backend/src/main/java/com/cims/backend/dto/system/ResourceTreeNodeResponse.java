package com.cims.backend.dto.system;

import java.util.ArrayList;
import java.util.List;

public class ResourceTreeNodeResponse {

    private final Long id;
    private final Long parentId;
    private final String resourceType;
    private final String resourceCode;
    private final String resourceName;
    private final Integer sortNo;
    private final List<String> bindingApiCodes;
    private final List<ResourceTreeNodeResponse> children;

    public ResourceTreeNodeResponse(
        Long id,
        Long parentId,
        String resourceType,
        String resourceCode,
        String resourceName,
        Integer sortNo,
        List<String> bindingApiCodes
    ) {
        this.id = id;
        this.parentId = parentId;
        this.resourceType = resourceType;
        this.resourceCode = resourceCode;
        this.resourceName = resourceName;
        this.sortNo = sortNo;
        this.bindingApiCodes = bindingApiCodes == null ? new ArrayList<String>() : bindingApiCodes;
        this.children = new ArrayList<ResourceTreeNodeResponse>();
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

    public List<String> getBindingApiCodes() {
        return bindingApiCodes;
    }

    public List<ResourceTreeNodeResponse> getChildren() {
        return children;
    }
}
