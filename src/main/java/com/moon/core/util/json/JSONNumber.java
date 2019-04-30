package com.moon.core.util.json;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author benshaoye
 * @date 2018/9/14
 */
public final class JSONNumber extends Number
    implements JSON<Object>, Comparable<JSONNumber> {

    final Double data;

    JSONNumber(double data) {
        this.data = data;
    }

    @Override
    public Map<String, Object> getMap(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Object> getList(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T get() {
        return (T) data;
    }

    @Override
    public JSONObject getJSONObject(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONArray getJSONArray(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIntValue(Object key) {
        return intValue();
    }

    @Override
    public Integer getInteger(Object key) {
        return intValue();
    }

    @Override
    public long getLongValue(Object key) {
        return longValue();
    }

    @Override
    public Long getLong(Object key) {
        return longValue();
    }

    @Override
    public double getDoubleValue(Object key) {
        return doubleValue();
    }

    @Override
    public Double getDouble(Object key) {
        return doubleValue();
    }

    @Override
    public String getString(Object key) {
        return String.valueOf(data);
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(data);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this
            || (obj instanceof JSONNumber && ((JSONNumber) obj).data.equals(this.data))
            || (obj instanceof Number && ((Number) obj).doubleValue() == this.data);
    }

    @Override
    public int compareTo(JSONNumber o) {
        return o == null ? -1 : Double.valueOf(this.data).compareTo(o.data);
    }

    @Override
    public int intValue() {
        return data.intValue();
    }

    @Override
    public long longValue() {
        return data.longValue();
    }

    @Override
    public float floatValue() {
        return data.floatValue();
    }

    @Override
    public double doubleValue() {
        return data.doubleValue();
    }
}
