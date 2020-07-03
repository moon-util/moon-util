package com.moon.core.json;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * @author moonsky
 */
public enum JSONBoolean implements JSON<Object>, Comparable<JSONBoolean>, BooleanSupplier {
    /**
     * true
     */
    TRUE(true),
    /**
     * false
     */
    FALSE(false);

    final Boolean value;

    JSONBoolean(boolean value) { this.value = value; }

    @Override
    public Map<String, Object> getMap(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public List<Object> getList(Object key) { throw new UnsupportedOperationException(); }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T get() { return (T) value; }

    @Override
    public JSONObject getJsonObject(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public JSONArray getJsonArray(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public int getIntValue(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public Integer getInteger(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public long getLongValue(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public Long getLong(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public double getDoubleValue(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public Double getDouble(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public String getString(Object key) { return toString(); }

    @Override
    public String toString() { return Boolean.toString(value); }

    @Override
    public boolean getAsBoolean() { return value; }

    public Boolean getBoolean() { return value; }
}
