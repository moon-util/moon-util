package com.moon.spring.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.core.enums.Available;
import com.moon.more.data.DataRecordable;

import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * @author moonsky
 */
@MappedSuperclass
public abstract class BaseDataRecordable extends BaseRecordable implements DataRecordable<String> {

    @JsonIgnore
    private Available available;

    public BaseDataRecordable() { }

    public BaseDataRecordable(String id) { super(id); }

    public BaseDataRecordable(AbstractRecordable<String> recordable) { super(recordable); }

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
        BaseDataRecordable that = (BaseDataRecordable) o;
        return available == that.available;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), available);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataRecordable{");
        sb.append(super.toString());
        sb.append("available=").append(available);
        sb.append('}');
        return sb.toString();
    }
}
