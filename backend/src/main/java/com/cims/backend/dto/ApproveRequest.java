package com.cims.backend.dto;

import javax.validation.constraints.NotNull;

public class ApproveRequest {

    @NotNull
    private Long loanApplicationId;

    public Long getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Long loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }
}
