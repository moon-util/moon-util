package com.moon.mapper.processing;

import javax.annotation.processing.Filer;
import java.io.IOException;

/**
 * @author moonsky
 */
interface JavaFileWritable {

    /**
     * 创建 java 类
     *
     * @param filer 创建 java 源文件
     *
     * @throws IOException 异常
     */
    void writeJavaFile(Filer filer) throws IOException;
}
