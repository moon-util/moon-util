package com.moon.processing.file;

import com.moon.processing.ProcessingProcessor;
import com.moon.processing.util.Processing2;
import com.moon.processing.util.Imported;
import com.moon.processing.util.String2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.Generated;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public abstract class BaseAnnotatedElement extends BaseModifiable implements Appender {

    private final static Map<String, Boolean> REPEATABLE = new HashMap<>();
    private final Map<String, Collection<JavaAnnotation>> annotationsMap = new LinkedHashMap<>();
    private final Elements utils;

    public BaseAnnotatedElement(Importer importer) {
        super(importer);
        this.utils = Processing2.getUtils();
    }

    private boolean isRepeatable(String annotationName) {
        Boolean repeatable = REPEATABLE.get(annotationName);
        if (repeatable == null) {
            TypeElement element = utils.getTypeElement(annotationName);
            if (element == null) {
                repeatable = false;
            } else {
                repeatable = element.getAnnotation(Repeatable.class) != null;
            }
            REPEATABLE.put(annotationName, repeatable);
        }
        return repeatable;
    }

    private Collection<JavaAnnotation> obtainAnnotations(String annotationName) {
        return annotationsMap.computeIfAbsent(annotationName, k -> new ArrayList<>());
    }

    public BaseAnnotatedElement annotationOf(Class<? extends Annotation> annotationClass) {
        return annotationOf(annotationClass.getCanonicalName());
    }

    public BaseAnnotatedElement annotationOf(
        Class<? extends Annotation> annotationClass, Consumer<JavaAnnotation> annotationUsing
    ) { return annotationOf(annotationClass.getCanonicalName(), annotationUsing); }

    public BaseAnnotatedElement annotationOf(String annotationName) {
        JavaAnnotation annotation = new JavaAnnotation(getImporter(), annotationName);
        Collection<JavaAnnotation> annotations = obtainAnnotations(annotationName);
        if (annotations.isEmpty() || isRepeatable(annotationName)) {
            annotations.add(annotation);
        }
        return this;
    }

    public BaseAnnotatedElement annotationOf(String annotationName, Consumer<JavaAnnotation> annotationUsing) {
        Collection<JavaAnnotation> annotations = obtainAnnotations(annotationName);
        if (annotations.isEmpty() || isRepeatable(annotationName)) {
            JavaAnnotation annotation = new JavaAnnotation(getImporter(), annotationName);
            annotationUsing.accept(annotation);
            annotations.add(annotation);
        }
        return this;
    }

    /**
     * 是否有注解
     *
     * @return true/false
     */
    public final boolean hasAnnotations() {
        if (annotationsMap.isEmpty()) {
            return false;
        }
        for (Collection<JavaAnnotation> value : annotationsMap.values()) {
            if (!value.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private final static String DATETIME = LocalDateTime.now().toString();

    public BaseAnnotatedElement annotationGenerated() {
        if (Imported.GENERATED) {
            annotationOf(Generated.class, annotation -> {
                annotation.stringOf("date", DATETIME);
                annotation.stringOf("value", ProcessingProcessor.class.getCanonicalName());
            });
        }
        return this;
    }

    public BaseAnnotatedElement annotationForRepository() {
        if (Imported.REPOSITORY) {
            annotationOf(Repository.class);
        } else if (Imported.COMPONENT) {
            annotationOf(Component.class);
        }
        return this;
    }

    public BaseAnnotatedElement annotationQualifier(String qualifierName) {
        if (Imported.QUALIFIER) {
            annotationOf(Qualifier.class, annotation -> {
                annotation.stringOf("value", qualifierName);
            });
        }
        return this;
    }

    public BaseAnnotatedElement annotationQualifierIfNotBlank(String qualifierName) {
        if (String2.isNotBlank(qualifierName)) {
            return annotationQualifier(qualifierName);
        }
        return this;
    }

    public BaseAnnotatedElement annotationAutowired(boolean required) {
        if (Imported.AUTOWIRED) {
            annotationOf(Autowired.class, annotation -> {
                annotation.booleanOf("required", required);
            });
        }
        return this;
    }

    @Override
    public void appendTo(JavaAddr addr) {
        for (Collection<JavaAnnotation> annotations : annotationsMap.values()) {
            for (JavaAnnotation annotation : annotations) {
                annotation.appendTo(addr);
            }
        }
    }
}
