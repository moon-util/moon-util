package com.moon.spring.data.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.moon.core.lang.StringUtil;
import com.moon.data.Record;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class BaseStringAuditRecord extends AbstractJpaAuditRecord<String, LocalDateTime> {

    public BaseStringAuditRecord() { }

    public BaseStringAuditRecord(Record<String> record) { super(record); }

    public BaseStringAuditRecord(AbstractJpaAuditRecord<String, LocalDateTime> audit) { super(audit); }

    public BaseStringAuditRecord(String createdBy, String updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdBy, updatedBy, createdAt, updatedAt);
    }

    public BaseStringAuditRecord(
        String id, String createdBy, String updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt
    ) { super(id, createdBy, updatedBy, createdAt, updatedAt); }

    @Override
    @JSONField(serialize = false)
    public boolean isNew() { return StringUtil.isEmpty(getId()); }
}
