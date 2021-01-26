package com.moon.accessor.dialect;

/**
 * 关键字符号处理策略
 *
 * @author benshaoye
 */
public enum KeywordPolicy {
    /**
     * 所有列名、表名等不差别均处理
     */
    ALWAYS,
    /**
     * 所有列名、表名等不差别均不处理
     */
    NONE,
    /**
     * 自动判断处理，当列名等是关键字时处理，否则不处理
     *
     * @see Keywords#asAvailableName(String, KeywordPolicy)
     */
    AUTO
}
