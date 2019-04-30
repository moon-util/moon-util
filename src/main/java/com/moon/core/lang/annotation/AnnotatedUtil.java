package com.moon.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author benshaoye
 */
public final class AnnotatedUtil {
    private AnnotatedUtil() {
        noInstanceError();
    }

    public final static <T extends Annotation> boolean
    isAnnotationPresent(AnnotatedElement ae, Class<T> annotationType) {
        return ae.isAnnotationPresent(annotationType);
    }

    public final static <T extends Annotation> T
    get(AnnotatedElement ae, Class<T> annotationType) {
        return ae.getAnnotation(annotationType);
    }

    public final static <A extends Annotation> boolean equals(A a1, A a2) {
        return a1 == a2 || (a1 != null && a1.equals(a2));
    }
}
