package com.moon.processing.file;

import com.moon.processor.holder.Importer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public abstract class JavaAnnotable extends BaseDeclared {

    private final List<JavaAnnotation> annotations = new ArrayList<>();

    public JavaAnnotable(Importer importer) { super(importer); }

    public JavaAnnotable annotationOf(String annotationName) {
        annotations.add(new JavaAnnotation(getImporter(), annotationName));
        return this;
    }

    public JavaAnnotable annotationOf(String annotationName, Consumer<JavaAnnotation> annotationUsing) {
        JavaAnnotation annotation = new JavaAnnotation(getImporter(), annotationName);
        annotationUsing.accept(annotation);
        annotations.add(annotation);
        return this;
    }
}
