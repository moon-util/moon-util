package com.moon.accessor.util;

import com.moon.accessor.exception.InitException;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * @author benshaoye
 */
public enum Annotation2 {
    ;

    public static <T extends Annotation> T getMethodAnnotationFor(
        Class<T> annotationClass, Class<?> declaringClass, String methodName, Class<?>... types
    ) {
        try {
            return declaringClass.getDeclaredMethod(methodName, types).getAnnotation(annotationClass);
        } catch (Throwable t) {
            throw new InitException(t);
        }
    }

    public static <T extends Annotation> T getFieldAnnotationFor(
        Class<T> annotationClass, Class<?> declaringClass, String fieldName
    ) {
        try {
            return declaringClass.getDeclaredField(fieldName).getAnnotation(annotationClass);
        } catch (Throwable t) {
            throw new InitException(t);
        }
    }

    public static <T extends Annotation> T getAnnotationFor(
        Class<T> annotationClass, AnnotatedElement element
    ) { return element.getAnnotation(annotationClass); }
}
