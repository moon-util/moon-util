package com.moon.mapping.processing;

import java.io.IOException;

/**
 * @author moonsky
 */
interface JavaFileWritable {

    /**
     * 创建 java 类
     *
     * @throws IOException 异常
     */
    void writeJavaFile() throws IOException;
}
