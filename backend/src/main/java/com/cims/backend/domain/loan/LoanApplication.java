package com.cims.backend.domain.loan;

import java.math.BigDecimal;

public class LoanApplication {

    private final Long id;
    private final Long customerId;
    private final BigDecimal amount;
    private final Integer termMonths;
    private String status;

    public LoanApplication(Long id, Long customerId, BigDecimal amount, Integer termMonths, String status) {
        this.id = id;
        this.customerId = customerId;
        this.amount = amount;
        this.termMonths = termMonths;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
