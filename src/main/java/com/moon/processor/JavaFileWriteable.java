package com.moon.processor;

/**
 * @author benshaoye
 */
public interface JavaFileWriteable {

    /**
     * 生成 java 文件
     *
     * @param writer writer
     */
    void writeJavaFile(JavaWriter writer);
}
