package com.cims.backend.service.approval;

import com.cims.backend.domain.approval.ApprovalTask;
import com.cims.backend.repository.approval.ApprovalRepository;
import com.cims.backend.repository.loan.LoanRepository;
import com.cims.backend.repository.notification.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalService {

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
        loanRepository.updateStatus(loanApplicationId, "APPROVED");
        notificationRepository.createNotification("OFFICER", "Loan application approved", loanApplicationId);
    }
}
