package com.moon.more.excel.annotation;

/**
 * @author benshaoye
 */
public enum DefaultStrategy {
    /**
     * null
     */
    NULL,
    /**
     * 空字符串
     */
    EMPTY,
    /**
     * 空白字符串
     */
    BLANK,
    /**
     * 等于数字 0
     */
    ZERO,
    /**
     * 负数
     */
    NEGATIVE,
    /**
     * 正数
     */
    POSITIVE
}
