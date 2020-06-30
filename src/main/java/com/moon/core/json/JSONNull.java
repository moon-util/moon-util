package com.moon.core.json;

import java.util.List;
import java.util.Map;

/**
 * @author moonsky
 */
public enum JSONNull implements JSON<Object>, Comparable<JSONNull> {
    /**
     * null 值
     */
    NULL;

    @Override
    public Map<String, Object> getMap(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public List<Object> getList(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public <T> T get() { return null; }

    @Override
    public JSONObject getJsonObject(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public JSONArray getJsonArray(Object key) { throw new UnsupportedOperationException(); }

    @Override
    public int getIntValue(Object key) { return 0; }

    @Override
    public Integer getInteger(Object key) { return null; }

    @Override
    public long getLongValue(Object key) { return 0; }

    @Override
    public Long getLong(Object key) { return null; }

    @Override
    public double getDoubleValue(Object key) { return 0; }

    @Override
    public Double getDouble(Object key) { return null; }

    @Override
    public String getString(Object key) { return null; }

    @Override
    public String toString() { return "null"; }
}
