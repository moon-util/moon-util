package com.moon.spring.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.core.enums.Available;

import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * @author benshaoye
 */
@MappedSuperclass
public abstract class DataRecordable extends BaseRecordable implements com.moon.more.data.DataRecordable<String> {

    @JsonIgnore
    private Available available;

    public DataRecordable() { }

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
        DataRecordable that = (DataRecordable) o;
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
