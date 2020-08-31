package com.moon.data.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.moon.core.lang.StringUtil;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class BaseAuditRecordEntity extends AbstractJpaAuditRecord<String, LocalDateTime> {

    public BaseAuditRecordEntity() { }

    public BaseAuditRecordEntity(AbstractJpaAuditRecord<String, LocalDateTime> audit) { super(audit); }

    public BaseAuditRecordEntity(String createdBy, String updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdBy, updatedBy, createdAt, updatedAt);
    }

    public BaseAuditRecordEntity(
        String id, String createdBy, String updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt
    ) { super(id, createdBy, updatedBy, createdAt, updatedAt); }

    @Override
    @JSONField(serialize = false)
    public boolean isNew() { return StringUtil.isEmpty(getId()); }
}
