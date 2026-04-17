package com.cims.backend.entity.approval;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("approval_task")
public class ApprovalTaskEntity {

    @TableId
    private Long id;
    private Long processId;
    private String nodeCode;
    private Long assigneeUserId;
    private String assigneeRoleCode;
    private String action;
    private String comment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProcessId() {
        return processId;
    }

    public void setProcessId(Long processId) {
        this.processId = processId;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public Long getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(Long assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }

    public String getAssigneeRoleCode() {
        return assigneeRoleCode;
    }

    public void setAssigneeRoleCode(String assigneeRoleCode) {
        this.assigneeRoleCode = assigneeRoleCode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
