package com.moon.core.util.json;

import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public enum JSONBoolean implements JSON<Object>,
    Comparable<JSONBoolean> {

    TRUE(true),
    FALSE(false);

    final Boolean value;

    JSONBoolean(boolean value) { this.value = value; }

    @Override
    public Map<String, Object> getMap(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public List<Object> getList(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public <T> T get() { return (T) value; }

    @Override
    public JSONObject getJSONObject(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public JSONArray getJSONArray(Object key) { throw new UnsupportedOperationException(); }

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
}
