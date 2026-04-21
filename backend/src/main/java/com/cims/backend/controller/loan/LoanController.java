package com.cims.backend.controller.loan;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description 贷款申请相关接口控制器
 */

import com.cims.backend.domain.loan.LoanApplication;
import com.cims.backend.dto.ApiResponse;
import com.cims.backend.dto.CreateLoanRequest;
import com.cims.backend.service.loan.LoanService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public ApiResponse<List<LoanApplication>> listLoans() {
        return ApiResponse.success(loanService.listLoans());
    }

    @PostMapping
    public ApiResponse<LoanApplication> createLoan(@Valid @RequestBody CreateLoanRequest request) {
        LoanApplication createdLoan = loanService.createLoan(
            request.getCustomerId(),
            request.getAmount(),
            request.getTermMonths()
        );
        return ApiResponse.success(createdLoan);
    }
}
