package com.moon.data.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.data.Available;
import com.moon.data.Record;
import com.moon.data.jpa.JpaDataRecord;

import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class DataLongRecord extends BaseLongRecord implements JpaDataRecord<Long> {

    @JsonIgnore
    private Available available = Available.YES;

    public DataLongRecord() { }

    public DataLongRecord(Long id) { super(id); }

    public DataLongRecord(Record<Long> recordable) { super(recordable); }

    public DataLongRecord(DataLongRecord recordable) {
        super(recordable);
        this.available = recordable.getAvailable();
    }

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
        DataLongRecord that = (DataLongRecord) o;
        return available == that.available;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), available);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataLongRecord{");
        sb.append(super.toString());
        sb.append("available=").append(available);
        sb.append('}');
        return sb.toString();
    }
}
