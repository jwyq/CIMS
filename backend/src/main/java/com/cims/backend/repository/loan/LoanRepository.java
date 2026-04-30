package com.cims.backend.repository.loan;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description 贷款申请数据访问仓储
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cims.backend.domain.loan.LoanApplication;
import com.cims.backend.entity.loan.LoanApplicationEntity;
import com.cims.backend.mapper.loan.LoanApplicationMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LoanRepository {

    private static final String APP_NO_PREFIX = "APP-";
    private static final String STATUS_PENDING = "PENDING";
    private static final String NODE_MANAGER_REVIEW = "MANAGER_REVIEW";
    private static final Long DEFAULT_APPLICANT_USER_ID = 3L;

    private final LoanApplicationMapper loanApplicationMapper;

    public LoanRepository(LoanApplicationMapper loanApplicationMapper) {
        this.loanApplicationMapper = loanApplicationMapper;
    }

    public List<LoanApplication> findAllLoans() {
        return loanApplicationMapper.selectList(new LambdaQueryWrapper<LoanApplicationEntity>().orderByDesc(LoanApplicationEntity::getId))
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public LoanApplication saveLoan(Long customerId, BigDecimal amount, Integer termMonths) {
        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setAppNo(APP_NO_PREFIX + System.currentTimeMillis());
        entity.setCustomerId(customerId);
        entity.setAmount(amount);
        entity.setTermMonths(termMonths);
        entity.setStatus(STATUS_PENDING);
        entity.setCurrentNode(NODE_MANAGER_REVIEW);
        entity.setApplicantUserId(DEFAULT_APPLICANT_USER_ID);
        loanApplicationMapper.insert(entity);
        return toDomain(entity);
    }

    public void updateStatus(Long loanApplicationId, String status) {
        LoanApplicationEntity entity = findEntityOrThrow(loanApplicationId);
        entity.setStatus(status);
        loanApplicationMapper.updateById(entity);
    }

    private LoanApplication toDomain(LoanApplicationEntity entity) {
        return new LoanApplication(entity.getId(), entity.getCustomerId(), entity.getAmount(), entity.getTermMonths(), entity.getStatus());
    }

    private LoanApplicationEntity findEntityOrThrow(Long loanApplicationId) {
        LoanApplicationEntity entity = loanApplicationMapper.selectById(loanApplicationId);
        if (entity == null) {
            throw new IllegalStateException("Loan application not found: " + loanApplicationId);
        }
        return entity;
    }
}
