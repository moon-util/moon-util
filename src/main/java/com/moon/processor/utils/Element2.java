package com.moon.processor.utils;

import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public enum Element2 {
    ;

    public static String toImplemented(TypeElement elem) {
        String classname = getQualifiedName(elem);
        if (Test2.isAbstractClass(elem)) {

        }
        return classname;
    }

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

    public static <T> String getClassname(T t, Function<T, Class<?>> classGetter) {
        try {
            return classGetter.apply(t).getCanonicalName();
        } catch (MirroredTypeException mirrored) {
            return mirrored.getTypeMirror().toString();
        }
    }
}
