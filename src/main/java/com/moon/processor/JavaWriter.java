package com.moon.processor;

import com.moon.processor.model.ThrowingConsumer;

import javax.annotation.processing.Filer;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
public class JavaWriter {

    private final Set<String> written = new LinkedHashSet<>();
    private final Filer filer;

    public JavaWriter(Filer filer) {
        this.filer = filer;
    }

    private Filer getFiler() { return filer; }

    /**
     * 写
     *
     * @param fullClassname com.example.ClassName
     * @param consumer
     */
    public void write(String fullClassname, ThrowingConsumer<Writer> consumer) {
        if (written.contains(fullClassname)) {
            return;
        }
        try (Writer jw = getFiler().createSourceFile(fullClassname).openWriter();
            PrintWriter writer = new PrintWriter(jw)) {
            consumer.accept(writer);
            written.add(fullClassname);
        } catch (/* IOException */Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
