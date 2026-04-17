package com.cims.backend.repository.approval;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cims.backend.domain.approval.ApprovalTask;
import com.cims.backend.entity.approval.ApprovalTaskEntity;
import com.cims.backend.mapper.approval.ApprovalTaskMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ApprovalRepository {

    private final ApprovalTaskMapper approvalTaskMapper;

    public ApprovalRepository(ApprovalTaskMapper approvalTaskMapper) {
        this.approvalTaskMapper = approvalTaskMapper;
    }

    public List<ApprovalTask> findAllTasks() {
        return approvalTaskMapper.selectList(new LambdaQueryWrapper<ApprovalTaskEntity>().orderByDesc(ApprovalTaskEntity::getId))
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    public void createPendingTask(Long loanApplicationId) {
        ApprovalTaskEntity task = new ApprovalTaskEntity();
        task.setProcessId(loanApplicationId);
        task.setNodeCode("MANAGER_REVIEW");
        task.setAssigneeRoleCode("MANAGER");
        task.setAction("PENDING");
        approvalTaskMapper.insert(task);
    }

    public void markApproved(Long loanApplicationId) {
        List<ApprovalTaskEntity> tasks = approvalTaskMapper.selectList(
            new LambdaQueryWrapper<ApprovalTaskEntity>().eq(ApprovalTaskEntity::getProcessId, loanApplicationId)
        );
        for (ApprovalTaskEntity task : tasks) {
            task.setAction("APPROVED");
            approvalTaskMapper.updateById(task);
        }
    }

    private ApprovalTask toDomain(ApprovalTaskEntity taskEntity) {
        String assignee = taskEntity.getAssigneeRoleCode() != null ? taskEntity.getAssigneeRoleCode() : String.valueOf(taskEntity.getAssigneeUserId());
        return new ApprovalTask(taskEntity.getId(), taskEntity.getProcessId(), assignee, taskEntity.getAction());
    }
}
