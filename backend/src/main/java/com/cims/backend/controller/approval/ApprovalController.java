package com.cims.backend.controller.approval;

import com.cims.backend.domain.approval.ApprovalTask;
import com.cims.backend.dto.ApiResponse;
import com.cims.backend.dto.ApproveRequest;
import com.cims.backend.service.approval.ApprovalService;
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
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping
    public ApiResponse<List<ApprovalTask>> approvals() {
        return ApiResponse.success(approvalService.listApprovals());
    }

    @PostMapping("/approve")
    public ApiResponse<Void> approve(@Valid @RequestBody ApproveRequest request) {
        approvalService.approve(request.getLoanApplicationId());
        return ApiResponse.successMessage("ok");
    }
}
