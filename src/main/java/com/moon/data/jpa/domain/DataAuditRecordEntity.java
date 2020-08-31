package com.moon.data.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.data.Available;
import com.moon.data.DataRecord;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class DataAuditRecordEntity extends BaseAuditRecordEntity implements DataRecord<String> {

    @JsonIgnore
    private Available available;

    public DataAuditRecordEntity() { }

    public DataAuditRecordEntity(AbstractJpaAuditRecord<String, LocalDateTime> audit) { super(audit); }

    public DataAuditRecordEntity(
        String createdBy, String updatedBy, LocalDateTime createdAt, LocalDateTime updatedAt
    ) { super(createdBy, updatedBy, createdAt, updatedAt); }

    public DataAuditRecordEntity(
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
        DataAuditRecordEntity that = (DataAuditRecordEntity) o;
        return available == that.available;
    }

    @Override
    public int hashCode() { return Objects.hash(super.hashCode(), available); }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataAuditable{");
        sb.append(super.toString());
        sb.append("available=").append(available);
        sb.append('}');
        return sb.toString();
    }
}
