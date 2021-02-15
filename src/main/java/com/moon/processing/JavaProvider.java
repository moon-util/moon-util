package com.moon.processing;

/**
 * @author benshaoye
 */
public interface JavaProvider {

    /**
     * 返回 java 文件完整声明
     *
     * @return java 文件
     */
    JavaDeclarable getJavaDeclare();
}
