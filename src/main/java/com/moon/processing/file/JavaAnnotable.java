package com.moon.processing.file;

import com.moon.processing.ProcessingProcessor;
import com.moon.processor.holder.Importer;
import com.moon.processor.utils.Imported;
import com.moon.processor.utils.String2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.Generated;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public abstract class JavaAnnotable extends BaseDeclared implements Appender {

    private final List<JavaAnnotation> annotations = new ArrayList<>();

    public JavaAnnotable(Importer importer) { super(importer); }

    public JavaAnnotable annotationOf(Class<? extends Annotation> annotationClass) {
        return annotationOf(annotationClass.getCanonicalName());
    }

    public JavaAnnotable annotationOf(
        Class<? extends Annotation> annotationClass, Consumer<JavaAnnotation> annotationUsing
    ) { return annotationOf(annotationClass.getCanonicalName(), annotationUsing); }

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

    /**
     * 是否有注解
     *
     * @return true/false
     */
    public final boolean hasAnnotations() { return !annotations.isEmpty(); }

    private final static String DATETIME = LocalDateTime.now().toString();

    public JavaAnnotable annotationGenerated() {
        if (Imported.GENERATED) {
            annotationOf(Generated.class, annotation -> {
                annotation.stringOf("date", DATETIME);
                annotation.stringOf("value", ProcessingProcessor.class.getCanonicalName());
            });
        }
        return this;
    }

    public JavaAnnotable annotationForRepository() {
        if (Imported.REPOSITORY) {
            annotationOf(Repository.class);
        } else if (Imported.COMPONENT) {
            annotationOf(Component.class);
        }
        return this;
    }

    public JavaAnnotable annotationQualifier(String qualifierName) {
        if (Imported.QUALIFIER) {
            annotationOf(Qualifier.class, annotation -> {
                annotation.stringOf("value", qualifierName);
            });
        }
        return this;
    }

    public JavaAnnotable annotationQualifierIfNotBlank(String qualifierName) {
        if (String2.isNotBlank(qualifierName)) {
            return annotationQualifier(qualifierName);
        }
        return this;
    }

    public JavaAnnotable annotationAutowired(boolean required) {
        if (Imported.AUTOWIRED) {
            annotationOf(Autowired.class, annotation -> {
                annotation.booleanOf("required", required);
            });
        }
        return this;
    }

    @Override
    public void appendTo(JavaAddr addr) {
        for (JavaAnnotation annotation : annotations) {
            annotation.appendTo(addr);
        }
    }
}
