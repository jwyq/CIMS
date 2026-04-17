package com.cims.backend.repository.loan;

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
        entity.setAppNo("APP-" + System.currentTimeMillis());
        entity.setCustomerId(customerId);
        entity.setAmount(amount);
        entity.setTermMonths(termMonths);
        entity.setStatus("PENDING");
        entity.setCurrentNode("MANAGER_REVIEW");
        entity.setApplicantUserId(3L);
        loanApplicationMapper.insert(entity);
        return toDomain(entity);
    }

    public void updateStatus(Long loanApplicationId, String status) {
        LoanApplicationEntity entity = loanApplicationMapper.selectById(loanApplicationId);
        if (entity == null) {
            return;
        }
        entity.setStatus(status);
        loanApplicationMapper.updateById(entity);
    }

    private LoanApplication toDomain(LoanApplicationEntity entity) {
        return new LoanApplication(entity.getId(), entity.getCustomerId(), entity.getAmount(), entity.getTermMonths(), entity.getStatus());
    }
}
