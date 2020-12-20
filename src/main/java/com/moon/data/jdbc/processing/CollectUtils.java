package com.moon.data.jdbc.processing;

import java.util.Collection;

/**
 * @author benshaoye
 */
abstract class CollectUtils {

    private CollectUtils() {}

    public static boolean isNotEmpty(Collection<?> collect) {
        return collect != null && !collect.isEmpty();
    }
}
