package com.moon.processor;

import com.moon.processor.model.ThrowingConsumer;
import com.moon.processor.utils.Log2;

import javax.annotation.processing.Filer;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author benshaoye
 */
public class JavaWriter {

    private final static byte[] BYTES = {};
    private final Map<String, Object> written = new ConcurrentHashMap<>();
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
        if (written.containsKey(fullClassname)) {
            return;
        }
        written.put(fullClassname, BYTES);
        try (Writer jw = getFiler().createSourceFile(fullClassname).openWriter();
             PrintWriter writer = new PrintWriter(jw)) {
            consumer.accept(writer);
        } catch (/* IOException */Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
