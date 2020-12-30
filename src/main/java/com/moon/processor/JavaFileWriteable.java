package com.moon.processor;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.io.IOException;

/**
 * @author benshaoye
 */
public interface JavaFileWriteable {

    /**
     * 生成 java 文件
     *
     * @param writer writer
     *
     * @throws IOException {@link Filer#createSourceFile(CharSequence, Element...)}
     */
    void writeJavaFile(JavaWriter writer);
}
