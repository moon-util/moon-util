package com.moon.mapper.processing;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.function.Consumer;

/**
 * @author moonsky
 */
final class EnvUtils {

    private static ProcessingEnvironment ENV;

    private EnvUtils() { }

    static void initialize(ProcessingEnvironment environment) { ENV = environment; }

    public static ProcessingEnvironment getEnv() { return ENV; }

    public static Types getTypes() { return getEnv().getTypeUtils(); }

    public static Elements getUtils() { return getEnv().getElementUtils(); }

    public static Messager getMessager() { return getEnv().getMessager(); }

    public static void newJavaFile(Filer filer, String name, Consumer<PrintWriter> consumer) throws IOException {
        JavaFileObject javaFile = filer.createSourceFile(name);
        try (Writer jw = javaFile.openWriter(); PrintWriter writer = new PrintWriter(jw)) {
            consumer.accept(writer);
        }
    }

    public static void newJavaFile(Filer filer, String name, ThrowingSupplier<String> supplier) throws IOException {
        newJavaFile(filer, name, writer -> {
            writer.write(supplier.get());
            writer.flush();
        });
    }
}
