package com.moon.processing;

import com.moon.processing.util.Processing2;
import com.moon.processing.util.String2;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author benshaoye
 */
public class JavaFiler {

    private final static byte[] BYTES = {};
    private final Map<String, Object> written = new ConcurrentHashMap<>();
    private final Filer filer;
    private Elements elements;

    public JavaFiler() { this.filer = Processing2.getFiler(); }

    public Filer getFiler() { return filer; }

    public Elements getElements() {
        return elements == null ? (elements = Processing2.getUtils()) : elements;
    }

    public void write(Collection<? extends JavaProvider> providers) {
        providers.forEach(this::write);
    }

    public void write(JavaProvider declarable) {
        write(declarable.getJavaDeclare());
    }

    public void write(JavaDeclarable declarable) {
        if (declarable == null) {
            return;
        }
        String classname = declarable.getClassname();
        if (String2.isBlank(classname) || written.containsKey(classname)) {
            return;
        }
        TypeElement loaded = getElements().getTypeElement(classname);
        if (loaded != null) {
            written.put(classname, BYTES);
            return;
        }
        written.put(classname, BYTES);
        try (Writer jw = getFiler().createSourceFile(classname).openWriter();
             PrintWriter writer = new PrintWriter(jw)) {
            writer.write(declarable.getJavaContent());
        } catch (/* IOException */Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
