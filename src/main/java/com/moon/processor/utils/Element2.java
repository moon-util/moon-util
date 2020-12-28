package com.moon.processor.utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public enum Element2 {
    ;

    public static <T> T cast(Object obj) { return (T) obj; }

    public static String getPackageName(Element elem) {
        return getQualifiedName(Environment2.getUtils().getPackageOf(elem));
    }

    public static String getQualifiedName(QualifiedNameable elem) { return elem.getQualifiedName().toString(); }

    public static String getSimpleName(Element elem) { return elem.getSimpleName().toString(); }

    public static String getSimpleName(String fullName) {
        int last = fullName.indexOf("<"), idx;
        if (last > 0) {
            idx = fullName.lastIndexOf('.', last);
        } else {
            idx = fullName.lastIndexOf('.');
            last = fullName.length();
        }
        return fullName.substring(Math.max(idx + 1, 0), last);
    }

    public static String toPropertyName(ExecutableElement element) {
        String name = element.getSimpleName().toString();
        return String2.decapitalize(name.substring(name.startsWith("is") ? 2 : 3));
    }

    public static String getGetterDeclareType(ExecutableElement elem) {
        return stringifyTypeMirror(elem.getReturnType());
    }

    public static String getSetterDeclareType(ExecutableElement elem) {
        return stringifyTypeMirror(elem.getParameters().get(0).asType());
    }

    public static String getFieldDeclareType(VariableElement elem) {
        return stringifyTypeMirror(elem.asType());
    }

    private static String stringifyTypeMirror(TypeMirror mirror) {
        return mirror.toString();
    }

    public static <T> String getClassname(T t, Function<T, Class<?>> classGetter) {
        try {
            return classGetter.apply(t).getCanonicalName();
        } catch (MirroredTypeException mirrored) {
            return mirrored.getTypeMirror().toString();
        }
    }
}
