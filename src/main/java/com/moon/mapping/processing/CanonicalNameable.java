package com.moon.mapping.processing;

/**
 * @author moonsky
 */
@FunctionalInterface
interface CanonicalNameable {

    /**
     * String name
     *
     * @return
     */
    String getCanonicalName();

    /**
     * 简单名称
     *
     * @return
     */
    default String getSimpleName() {
        return ElemUtils.getSimpleName(getCanonicalName());
    }
}
