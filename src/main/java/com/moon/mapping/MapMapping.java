package com.moon.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @author moonsky
 */
public interface MapMapping<T> {

    <M extends Map<String, ?>> M toMap(T thisObject, M targetMap);

    default HashMap<String, ?> toHashMap(T thisObject) {
        return toMap(thisObject, new HashMap<>(16));
    }
}
