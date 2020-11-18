package com.moon.mapping.annotation;

/**
 * 忽略类型
 *
 * @author moonsky
 */
public enum IgnoreMode {
    /**
     * 不忽略（默认）
     */
    NONE,
    /**
     * 忽略正向映射
     */
    FORWARD,
    /**
     * 忽略反向映射
     */
    BACKWARD,
    /**
     * 忽略这个字段所有映射
     */
    ALL
}
