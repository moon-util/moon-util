package com.moon.processing.util;

import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public enum Element2 {
    ;

    public static <T> T cast(Object obj) { return (T) obj; }

    public static String getPackageName(Class<?> classDeclared) {
        return classDeclared.getPackage().getName();
    }

    public static String getPackageName(Element elem) {
        return getQualifiedName(Processing2.getUtils().getPackageOf(elem));
    }

    public static String getQualifiedName(Class<?> elem) { return elem.getCanonicalName(); }

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

    public static String getSetterDeclareType(ExecutableElement elem) {
        return stringifyTypeMirror(elem.getParameters().get(0).asType());
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

    public static List<Element> getEnums(Element elem) {
        if (!Test2.isEnum(elem)) {
            return new ArrayList<>();
        }
        List<Element> enums = new ArrayList<>();
        List<? extends Element> elements = elem.getEnclosedElements();
        for (Element element : elements) {
            if (element.getKind() == ElementKind.ENUM_CONSTANT) {
                enums.add(element);
            }
        }
        return enums;
    }

    public static Element findEnumAt(String classname, int index) {
        TypeElement elem = Processing2.getUtils().getTypeElement(classname);
        if (elem == null) {
            return null;
        }
        int indexer = 0;
        for (Element child : elem.getEnclosedElements()) {
            if (child.getKind() == ElementKind.ENUM_CONSTANT) {
                if (index == indexer) {
                    return child;
                } else {
                    indexer++;
                }
            }
        }
        return null;
    }

    public static Element findEnumAs(String classname, String name) {
        TypeElement elem = Processing2.getUtils().getTypeElement(classname);
        if (elem == null) {
            return null;
        }
        for (Element child : elem.getEnclosedElements()) {
            if (child.getKind() == ElementKind.ENUM_CONSTANT && Objects.equals(getSimpleName(child), name)) {
                return child;
            }
        }
        return null;
    }
}
