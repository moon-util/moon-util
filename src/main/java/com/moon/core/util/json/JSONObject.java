package com.moon.core.util.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.moon.core.util.TypeUtil.cast;

/**
 * @author benshaoye
 */
public class JSONObject extends HashMap<String, Object>
    implements JSON<String>, Iterable<Map.Entry<String, Object>> {

    public JSONObject(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public JSONObject(int initialCapacity) {
        super(initialCapacity);
    }

    public JSONObject() {
        super();
    }

    public JSONObject(Map<? extends String, ?> m) {
        super(m);
    }

    @Override
    public <T> T get() {
        return (T) this;
    }

    @Override
    public JSONObject getJSONObject(String key) {
        return cast().toType(get(key), JSONObject.class);
    }

    @Override
    public JSONArray getJSONArray(String key) {
        return cast().toType(get(key), JSONArray.class);
    }

    @Override
    public int getIntValue(String key) {
        return cast().toIntValue(get(key));
    }

    @Override
    public Integer getInteger(String key) {
        return cast().toInteger(get(key));
    }

    @Override
    public long getLongValue(String key) {
        return cast().toLongValue(get(key));
    }

    @Override
    public Long getLong(String key) {
        return cast().toLong(get(key));
    }

    @Override
    public double getDoubleValue(String key) {
        return cast().toDoubleValue(get(key));
    }

    @Override
    public Double getDouble(String key) {
        return cast().toDouble(get(key));
    }

    @Override
    public String getString(String key) {
        return cast().toString(get(key));
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return entrySet().iterator();
    }

    @Override
    public String toString() {
        return JSONCfg.WEAK.get().stringify(this);
    }
}
