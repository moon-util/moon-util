package com.moon.mapping.processing;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.QualifiedNameable;
import java.beans.Introspector;

/**
 * @author benshaoye
 */
abstract class ElementUtils {

    private ElementUtils() {}

    public static String getQualifiedName(QualifiedNameable elem) {
        return elem.getQualifiedName().toString();
    }

    static <T> T cast(Object obj) { return (T) obj; }

    static String concat(Object... values) {
        StringBuilder str = new StringBuilder();
        for (Object value : values) {
            str.append(value);
        }
        return str.toString();
    }

    static String toPropertyName(ExecutableElement element) {
        String name = element.getSimpleName().toString();
        return Introspector.decapitalize(name.substring(name.startsWith("is") ? 2 : 3));
    }

    public static String getSimpleName(String fullName) {
        int index = fullName.lastIndexOf('.');
        return index < 0 ? fullName : fullName.substring(index + 1);
    }

    public static String getPackageName(Element element) {
        do {
            if (DetectUtils.isPackage(element)) {
                return getQualifiedName((QualifiedNameable) element);
            }
            element = element.getEnclosingElement();
        } while (true);
    }

    static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    final static String format(String classname) { return classname.replace('.', '_'); }

    static String getBeanMappingEnumName(String classname) {
        return "BeanMapping_" + format(classname);
    }
}
