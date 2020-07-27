package com.moon.core.json;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.moon.core.util.TypeUtil.cast;

/**
 * @author moonsky
 * @see #hashCode() == {@link String#hashCode()}
 * @see #equals(Object) == {@link String#equals(Object)}
 */
public final class JSONString implements JSON<Object>,
    CharSequence, Comparable<JSONString> {

    final String data;

    public JSONString(String data) {
        this.data = Objects.requireNonNull(data);
    }

    @Override
    public int length() {
        return data.length();
    }

    @Override
    public char charAt(int index) {
        return data.charAt(index);
    }

    @Override
    public JSONString subSequence(int start, int end) {
        return new JSONString(data.substring(start, end));
    }

    @Override
    public String toString() {
        return new StringBuilder().append('"').append(data).append('"').toString();
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
    public JSONObject getJsonObject(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONArray getJsonArray(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getIntValue(Object key) {
        return cast().toIntValue(data);
    }

    @Override
    public Integer getInteger(Object key) {
        return cast().toInteger(data);
    }

    @Override
    public long getLongValue(Object key) {
        return cast().toLongValue(data);
    }

    @Override
    public Long getLong(Object key) {
        return cast().toLong(data);
    }

    @Override
    public double getDoubleValue(Object key) {
        return cast().toDoubleValue(data);
    }

    @Override
    public Double getDouble(Object key) {
        return cast().toDouble(data);
    }

    @Override
    public String getString(Object key) {
        return data;
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof JSONString) {
            final String thatData = ((JSONString) obj).data;
            final String thisData = this.data;

            return thatData == thisData ||
                (thisData == null || thatData == null
                    ? false
                    : thisData.equals(thatData));
        }
        return false;
    }

    @Override
    public int compareTo(JSONString o) {
        return o == null ? -1 : this.data.compareTo(o.data);
    }
}
