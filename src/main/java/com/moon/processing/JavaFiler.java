package com.moon.processing;

import com.moon.processing.util.Processing2;

import javax.annotation.processing.Filer;
import java.util.Map;

/**
 * @author benshaoye
 */
public class JavaFiler {

    private final Filer filer;

    public JavaFiler() {
        this.filer = Processing2.getFiler();
    }

    public void write(Map<?, ? extends JavaProvider> providerMap) {
        providerMap.values().forEach(this::write);
    }

    public void write(JavaProvider declarable) {
        write(declarable.getJavaDeclare());
    }

    public void write(JavaDeclarable declarable) {
        declarable.getClassname();
        declarable.toString();
    }
}
