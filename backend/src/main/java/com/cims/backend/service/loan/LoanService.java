package com.cims.backend.service.loan;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description 贷款申请业务服务
 */

import com.cims.backend.domain.loan.LoanApplication;
import com.cims.backend.repository.approval.ApprovalRepository;
import com.cims.backend.repository.loan.LoanRepository;
import com.cims.backend.repository.notification.NotificationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanService {

    private static final String RECIPIENT_MANAGER = "MANAGER";
    private static final String MESSAGE_PENDING_APPROVAL = "New loan application pending approval";

    private final LoanRepository loanRepository;
    private final ApprovalRepository approvalRepository;
    private final NotificationRepository notificationRepository;

    public LoanService(
        LoanRepository loanRepository,
        ApprovalRepository approvalRepository,
        NotificationRepository notificationRepository
    ) {
        this.loanRepository = loanRepository;
        this.approvalRepository = approvalRepository;
        this.notificationRepository = notificationRepository;
    }

    public List<LoanApplication> listLoans() {
        return loanRepository.findAllLoans();
    }

    public LoanApplication createLoan(Long customerId, BigDecimal amount, Integer termMonths) {
        LoanApplication loanApplication = loanRepository.saveLoan(customerId, amount, termMonths);
        approvalRepository.createPendingTask(loanApplication.getId());
        notificationRepository.createNotification(RECIPIENT_MANAGER, MESSAGE_PENDING_APPROVAL, loanApplication.getId());
        return loanApplication;
    }
}
