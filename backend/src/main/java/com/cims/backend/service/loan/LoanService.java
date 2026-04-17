package com.cims.backend.service.loan;

import com.cims.backend.domain.loan.LoanApplication;
import com.cims.backend.repository.approval.ApprovalRepository;
import com.cims.backend.repository.loan.LoanRepository;
import com.cims.backend.repository.notification.NotificationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanService {

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
        notificationRepository.createNotification("MANAGER", "New loan application pending approval", loanApplication.getId());
        return loanApplication;
    }
}
