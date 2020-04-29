package com.moon.spring.jpa.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moon.core.enums.Available;
import com.moon.more.data.DataRecordable;

import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * @author benshaoye
 */
@MappedSuperclass
public abstract class DataAuditable extends BaseAuditable implements DataRecordable<String> {

    @JsonIgnore
    private Available available;

    public DataAuditable() { }

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
        DataAuditable that = (DataAuditable) o;
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
