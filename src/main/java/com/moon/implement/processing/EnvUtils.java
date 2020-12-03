package com.moon.implement.processing;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.*;
import java.io.IOException;
import java.io.Writer;

/**
 * @author moonsky
 */
final class EnvUtils {

    private static ProcessingEnvironment ENV;

    private EnvUtils() { }

    static void initialize(ProcessingEnvironment environment) { ENV = environment; }

    public static void newSourceFile() {
        ENV.getMessager().printMessage(Diagnostic.Kind.WARNING, System.getProperty("java.class.path"));
        StandardLocation location = StandardLocation.CLASS_PATH;
        try {
            FileObject obj = ENV.getFiler().createResource(location, "META-INF", "moon-implement.factories");
            try (Writer writer = obj.openWriter()) {
                writer.write("创建完成");
            }
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // compiler.
        } catch (IOException e) {
            throw new IllegalStateException("文件创建失败");
        }
    }
}
