package com.cims.backend.entity.customer;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("cst_customer_basic_info_history")
public class CustomerBasicInfoHistoryEntity {

    @TableId
    private Long id;
    private Long customerId;
    private String changeType;
    private String beforeSnapshot;
    private String afterSnapshot;
    private Long changedBy;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public String getBeforeSnapshot() { return beforeSnapshot; }
    public void setBeforeSnapshot(String beforeSnapshot) { this.beforeSnapshot = beforeSnapshot; }
    public String getAfterSnapshot() { return afterSnapshot; }
    public void setAfterSnapshot(String afterSnapshot) { this.afterSnapshot = afterSnapshot; }
    public Long getChangedBy() { return changedBy; }
    public void setChangedBy(Long changedBy) { this.changedBy = changedBy; }
}
