package com.moon.processing.util;

import com.moon.accessor.annotation.Provided;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

/**
 * @author benshaoye
 */
public enum Assert2 {
    ;

    static final String SIMPLE_NAME = Provided.class.getSimpleName();

    private static void illegal(String template, ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        String classname = ((TypeElement) method.getEnclosingElement()).getQualifiedName().toString();
        String errorMessage = String2.format(template, SIMPLE_NAME, classname + "#" + methodName);
        throw new IllegalStateException(errorMessage);
    }

    public static void assertProvidedMethodParameters(ExecutableElement method) {
        if (!method.getParameters().isEmpty()) {
            illegal("被（{}）注解的方法 [{}] 返回值不能有参数.", method);
        }
        if (method.getReturnType().getKind() == TypeKind.VOID) {
            illegal("被（{}）注解的方法 [{}] 返回值不能是 'void'.", method);
        }
    }
}
