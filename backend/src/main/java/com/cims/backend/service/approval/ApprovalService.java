package com.cims.backend.service.approval;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description 审批流程业务服务
 */

import com.cims.backend.domain.approval.ApprovalTask;
import com.cims.backend.repository.approval.ApprovalRepository;
import com.cims.backend.repository.loan.LoanRepository;
import com.cims.backend.repository.notification.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalService {

    private static final String STATUS_APPROVED = "APPROVED";
    private static final String RECIPIENT_OFFICER = "OFFICER";
    private static final String MESSAGE_LOAN_APPROVED = "Loan application approved";

    private final ApprovalRepository approvalRepository;
    private final LoanRepository loanRepository;
    private final NotificationRepository notificationRepository;

    public ApprovalService(
        ApprovalRepository approvalRepository,
        LoanRepository loanRepository,
        NotificationRepository notificationRepository
    ) {
        this.approvalRepository = approvalRepository;
        this.loanRepository = loanRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<ApprovalTask> listApprovals() {
        return approvalRepository.findAllTasks();
    }

    public void approve(Long loanApplicationId) {
        approvalRepository.markApproved(loanApplicationId);
        loanRepository.updateStatus(loanApplicationId, STATUS_APPROVED);
        notificationRepository.createNotification(RECIPIENT_OFFICER, MESSAGE_LOAN_APPROVED, loanApplicationId);
    }
}
