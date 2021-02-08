package com.moon.processor.create;

import com.moon.processor.holder.Importer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public abstract class DeclAnnotable extends BaseDeclared {

    private final List<DeclAnnotation> annotations = new ArrayList<>();

    public DeclAnnotable(Importer importer) { super(importer); }

    public DeclAnnotable annotationOf(String annotationName) {
        annotations.add(new DeclAnnotation(getImporter(), annotationName));
        return this;
    }

    public DeclAnnotable annotationOf(String annotationName, Consumer<DeclAnnotation> annotationUsing) {
        DeclAnnotation annotation = new DeclAnnotation(getImporter(), annotationName);
        annotationUsing.accept(annotation);
        annotations.add(annotation);
        return this;
    }
}
