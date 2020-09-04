package com.moon.data.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.data.Available;
import com.moon.data.jpa.JpaDataRecord;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class DataStringAuditRecord extends BaseStringAuditRecord implements JpaDataRecord<String> {

    @JsonIgnore
    private Available available = Available.YES;

    public DataStringAuditRecord() { }

    public DataStringAuditRecord(AbstractJpaAuditRecord<String, LocalDateTime> audit) { super(audit); }

    public DataStringAuditRecord(DataStringAuditRecord recordable) {
        this(recordable.getId(),
            recordable.getCreatedBy(),
            recordable.getUpdatedBy(),
            recordable.getCreatedAt(),
            recordable.getUpdatedAt());
    }

    public DataStringAuditRecord(
        String createdBy, String updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt
    ) { super(createdBy, updatedBy, createdAt, updatedAt); }

    public DataStringAuditRecord(
        String id, String createdBy, String updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt
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
        DataStringAuditRecord that = (DataStringAuditRecord) o;
        return available == that.available;
    }

    @Override
    public int hashCode() { return Objects.hash(super.hashCode(), available); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataStringAuditRecord{");
        sb.append(super.toString());
        sb.append("available=").append(available);
        sb.append('}');
        return sb.toString();
    }
}
