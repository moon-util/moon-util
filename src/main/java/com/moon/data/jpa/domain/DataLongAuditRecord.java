package com.moon.data.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.data.Available;
import com.moon.data.Record;
import com.moon.data.jpa.JpaDataRecord;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class DataLongAuditRecord extends BaseLongAuditRecord implements JpaDataRecord<Long> {

    @JsonIgnore
    private Available available = Available.YES;

    public DataLongAuditRecord() { }

    public DataLongAuditRecord(Record<Long> record) { super(record); }

    public DataLongAuditRecord(AbstractJpaAuditRecord<Long, LocalDateTime> audit) { super(audit); }

    public DataLongAuditRecord(DataLongAuditRecord recordable) {
        this(recordable.getId(),
            recordable.getCreatedBy(),
            recordable.getUpdatedBy(),
            recordable.getCreatedAt(),
            recordable.getUpdatedAt());
    }

    public DataLongAuditRecord(
        Long createdBy, Long updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt
    ) { super(createdBy, updatedBy, createdAt, updatedAt); }

    public DataLongAuditRecord(
        Long id, Long createdBy, Long updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt
    ) { super(id, createdBy, updatedBy, createdAt, updatedAt); }

    @Override
    @JSONField(serialize = false)
    public Available getAvailable() { return available; }

    @Override
    public void setAvailable(Available available) { this.available = available; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        DataLongAuditRecord that = (DataLongAuditRecord) o;
        return available == that.available;
    }

    @Override
    public int hashCode() { return Objects.hash(super.hashCode(), available); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataLongAuditRecord{");
        sb.append(super.toString());
        sb.append("available=").append(available);
        sb.append('}');
        return sb.toString();
    }
}
