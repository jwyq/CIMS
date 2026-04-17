package com.cims.backend.domain.approval;

public class ApprovalTask {

    private final Long id;
    private final Long loanApplicationId;
    private final String assignee;
    private String status;

    public ApprovalTask(Long id, Long loanApplicationId, String assignee, String status) {
        this.id = id;
        this.loanApplicationId = loanApplicationId;
        this.assignee = assignee;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getLoanApplicationId() {
        return loanApplicationId;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
