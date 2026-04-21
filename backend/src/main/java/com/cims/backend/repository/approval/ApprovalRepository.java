package com.cims.backend.repository.approval;

/**
 * @autuor y5035
 * @since 2026-04-20
 * @description 审批任务数据访问仓储
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cims.backend.domain.approval.ApprovalTask;
import com.cims.backend.entity.approval.ApprovalTaskEntity;
import com.cims.backend.mapper.approval.ApprovalTaskMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ApprovalRepository {

    private static final String NODE_CODE_MANAGER_REVIEW = "MANAGER_REVIEW";
    private static final String ASSIGNEE_ROLE_MANAGER = "MANAGER";
    private static final String ACTION_PENDING = "PENDING";
    private static final String ACTION_APPROVED = "APPROVED";

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
        task.setNodeCode(NODE_CODE_MANAGER_REVIEW);
        task.setAssigneeRoleCode(ASSIGNEE_ROLE_MANAGER);
        task.setAction(ACTION_PENDING);
        approvalTaskMapper.insert(task);
    }

    public void markApproved(Long loanApplicationId) {
        List<ApprovalTaskEntity> tasks = approvalTaskMapper.selectList(
            new LambdaQueryWrapper<ApprovalTaskEntity>().eq(ApprovalTaskEntity::getProcessId, loanApplicationId)
        );
        tasks.forEach(this::markTaskApproved);
    }

    private ApprovalTask toDomain(ApprovalTaskEntity taskEntity) {
        String assignee = taskEntity.getAssigneeRoleCode() != null ? taskEntity.getAssigneeRoleCode() : String.valueOf(taskEntity.getAssigneeUserId());
        return new ApprovalTask(taskEntity.getId(), taskEntity.getProcessId(), assignee, taskEntity.getAction());
    }

    private void markTaskApproved(ApprovalTaskEntity task) {
        task.setAction(ACTION_APPROVED);
        approvalTaskMapper.updateById(task);
    }
}
