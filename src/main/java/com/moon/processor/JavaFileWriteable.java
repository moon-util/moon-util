package com.moon.processor;

import javax.annotation.processing.Filer;

/**
 * @author benshaoye
 */
public interface JavaFileWriteable {


    /**
     * 生成 java 文件
     *
     * @param filer filer
     */
    void writeJavaFile(Filer filer);
}
