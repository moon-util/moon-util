package com.moon.core.util.require;

import java.util.Collection;
import java.util.Map;

/**
 * @author benshaoye
 */
interface RequiresEmpty extends RequiresFail {
    /**
     * 一个空集合
     *
     * @param collection
     * @return
     */
    default boolean requireEmpty(Collection collection) {
        int size = collection == null ? 0 : collection.size();
        return success("Test result: {}\n\tExpect size: 0 (null or an EMPTY collection)\n\tActual size: {}",
            size == 0, size);
    }

    /**
     * 一个空集合
     *
     * @param collection
     * @return
     */
    default boolean requireEmpty(Map collection) {
        int size = collection == null ? 0 : collection.size();
        return success("Test result: {}\n\tExpect size: 0 (null or an EMPTY map)\n\tActual size: {}",
            size == 0, size);
    }

    /**
     * 不是一个空集合
     *
     * @param collection
     * @return
     */
    default boolean requireNotEmpty(Collection collection) {
        int size = collection == null ? 0 : collection.size();
        return success("Test result: {}\n\tExpect size: great than 0\n\tActual size: {}",
            size > 0, size);
    }

    /**
     * 不是一个空集合
     *
     * @param collection
     * @return
     */
    default boolean requireNotEmpty(Map collection) {
        int size = collection == null ? 0 : collection.size();
        return success("Test result: {}\n\tExpect size: great than 0\n\tActual size: {}",
            size > 0, size);
    }
}
