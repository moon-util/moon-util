package com.moon.core.util;

import com.moon.core.lang.JoinerUtil;
import com.moon.core.lang.ThrowUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public class PropertiesGroup extends HashMap<String, Object> implements PropertiesSupplier {

    private static final long serialVersionUID = 362498820763181266L;

    final String DEFAULT_KEY = RandomStringUtil.next(64);

    private final PropertiesGroup top;
    private final PropertiesGroup parent;

    private PropertiesGroup(Map<? extends String, ?> properties, PropertiesGroup top, PropertiesGroup parent) {
        this(top, parent);
        putAll(properties);
    }

    private PropertiesGroup(PropertiesGroup top, PropertiesGroup parent) {
        this.parent = parent == null ? this : parent;
        this.top = top == null ? this : top;
    }

    public PropertiesGroup(Map<? extends String, ?> properties) { this(properties, null, null); }

    public PropertiesGroup() { this(null); }

    public final static PropertiesGroup of() { return new PropertiesGroup(); }

    public final static PropertiesGroup of(Map<? extends String, ?> properties) { return new PropertiesGroup(properties); }

    /*
     * ----------------------------------------------------------------------------
     * overrides
     * ----------------------------------------------------------------------------
     */

    @Override
    public Object get(Object key) {
        Object value = super.get(key);
        if (value == null) { return null; }
        if (value instanceof PropertiesGroup){
            PropertiesGroup group = (PropertiesGroup) value;
            Object result = group.get(DEFAULT_KEY);
            return result == null ? group : result;
        }
        return value;
    }

    private Object simplePut(String key, Object value){ return super.put(key, value); }

    @Override
    public Object put(String key, Object value) {
        int index = key.indexOf('.');
        return index < 0 ? this.simplePut(key, value)
            : getChildOrEmpty(key.substring(0, index))
            .simplePut(key.substring(index + 1), value);
    }

    @Override
    public void putAll(Map<? extends String, ?> properties) {
        if (properties != null) { properties.forEach(this::put); }
    }

    private PropertiesGroup getChildOrEmpty(String key) {
        PropertiesGroup group = getChildOnly(key);
        if (group == null) {
            super.put(key, group = new PropertiesGroup(this.top, this));
        }
        return group;
    }

    private PropertiesGroup getChildOnly(String key) {
        Object present = super.get(key);
        if (present == null) {
            return null;
        }
        if (present instanceof PropertiesGroup) {
            return (PropertiesGroup) present;
        }
        PropertiesGroup group = new PropertiesGroup(this.top, this);
        group.simplePut(DEFAULT_KEY, present);
        simplePut(key, group);
        return group;
    }

    /*
     * ----------------------------------------------------------------------------
     * get group
     * ----------------------------------------------------------------------------
     */

    public PropertiesGroup getParent() { return parent; }

    public PropertiesGroup getTop() { return top; }

    public PropertiesGroup getChildGroup(String key) { return getChildOrEmpty(key); }

    public Set<PropertiesGroup> getChildrenGroups() {
        Set groups = new HashSet();
        forEach((key, value) -> {
            if (value instanceof PropertiesGroup) {
                groups.add(value);
            }
        });
        return groups;
    }

    /*
     * ----------------------------------------------------------------------------
     * get value
     * ----------------------------------------------------------------------------
     */

    public String getString(String... keys) {
        if (keys != null && keys.length > 0) {
            Object value;
            PropertiesGroup group = this;
            int endIndex = keys.length - 1;
            for (int i = 0; i < endIndex; i++) {
                value = group.get(keys[i]);
                if (value instanceof PropertiesGroup) {
                    group = (PropertiesGroup) value;
                } else if (value == null) {
                    return null;
                } else {
                    ThrowUtil.doThrow(JoinerUtil.join(keys));
                }
            }
            value = group.get(keys[endIndex]);
            return value == null ? null : value.toString();
        }
        return null;
    }

    public <T> T getAndTransform(Function<String, T> transformer, String... keys) { return transformer.apply(getString(keys)); }
}
