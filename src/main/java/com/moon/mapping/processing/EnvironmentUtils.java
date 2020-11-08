package com.moon.mapping.processing;

import javax.annotation.processing.Filer;
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
final class EnvironmentUtils {

    private static ProcessingEnvironment ENV;

    private EnvironmentUtils() { }

    static void initialize(ProcessingEnvironment environment) { ENV = environment; }

    public static ProcessingEnvironment getEnv() { return ENV; }

    public static Types getTypes() { return getEnv().getTypeUtils(); }

    public static Elements getUtils() { return getEnv().getElementUtils(); }

    public static void newJavaFile(String name, Consumer<PrintWriter> consumer) throws IOException {
        newJavaFile(getEnv().getFiler(), name, consumer);
    }

    public static void newJavaFile(Filer filer, String name, Consumer<PrintWriter> consumer) throws IOException {
        JavaFileObject javaFile = filer.createSourceFile(name);
        try (Writer jw = javaFile.openWriter(); PrintWriter writer = new PrintWriter(jw)) {
            consumer.accept(writer);
        }
    }
}
