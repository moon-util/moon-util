package com.moon.data.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.data.Available;
import com.moon.data.DataRecord;

import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class DataRecordEntity extends BaseRecordEntity implements DataRecord<String> {

    @JsonIgnore
    private Available available;

    public DataRecordEntity() { }

    public DataRecordEntity(String id) { super(id); }

    public DataRecordEntity(AbstractJpaRecord<String> recordable) { super(recordable); }

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
        DataRecordEntity that = (DataRecordEntity) o;
        return available == that.available;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), available);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataRecordEntity{");
        sb.append(super.toString());
        sb.append("available=").append(available);
        sb.append('}');
        return sb.toString();
    }
}
