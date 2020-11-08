package com.moon.mapping.processing;

import javax.annotation.processing.Filer;
import java.io.IOException;

/**
 * @author moonsky
 */
public interface JavaFileWritable {

    void writeJavaFile(Filer filer) throws IOException;
}
