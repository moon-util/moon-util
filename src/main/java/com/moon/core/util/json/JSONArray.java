package com.moon.core.util.json;

import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

import static com.moon.core.util.TypeUtil.cast;

/**
 * @author benshaoye
 * @date 2018/9/14
 */

public class JSONArray extends ArrayList<Object>
    implements JSON<Integer>, List<Object>, RandomAccess {

    private static final long serialVersionUID = 1L;

    public JSONArray() { super(); }

    public JSONArray(int capacity) { super(capacity); }

    public JSONArray(List<Object> list) { super(list); }

    Object get(Object key) { return this.get((int) key); }

    @Override
    public <T> T get() { return (T) this; }

    @Override
    public JSONObject getJSONObject(Integer key) { return cast().toType(get(key), JSONObject.class); }

    @Override
    public JSONArray getJSONArray(Integer key) { return cast().toType(get(key), JSONArray.class); }

    @Override
    public int getIntValue(Integer key) { return cast().toIntValue(get(key)); }

    @Override
    public Integer getInteger(Integer key) { return cast().toInteger(get(key)); }

    @Override
    public long getLongValue(Integer key) { return cast().toLongValue(get(key)); }

    @Override
    public Long getLong(Integer key) { return cast().toLong(get(key)); }

    @Override
    public double getDoubleValue(Integer key) { return cast().toDoubleValue(get(key)); }

    @Override
    public Double getDouble(Integer key) { return cast().toDouble(get(key)); }

    @Override
    public String getString(Integer key) { return cast().toString(get(key)); }

    @Override
    public String toString() { return JSONCfg.WEAK.get().stringify(this); }
}
