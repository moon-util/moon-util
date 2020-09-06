package com.moon.data.jpa.domain;

import com.moon.data.Record;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class BaseLongAuditRecord extends AbstractJpaAuditRecord<Long, LocalDateTime> {

    public BaseLongAuditRecord() { }

    public BaseLongAuditRecord(Record<Long> record) { super(record); }

    public BaseLongAuditRecord(AbstractJpaAuditRecord<Long, LocalDateTime> audit) { super(audit); }

    public BaseLongAuditRecord(Long createdBy, Long updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdBy, updatedBy, createdAt, updatedAt);
    }

    public BaseLongAuditRecord(
        Long id, Long createdBy, Long updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt
    ) { super(id, createdBy, updatedBy, createdAt, updatedAt); }
}
