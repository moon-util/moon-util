package com.moon.processor.holder;

import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * @author benshaoye
 */
enum Access2 {
    ;

    private static void throwException(String message, TypeElement element) {
        String classname = Element2.getQualifiedName(element);
        throw new IllegalStateException(message + classname);
    }

    public static void assertAccessorDecl(TypeElement element) {
        if (Test2.isAny(element, Modifier.FINAL)) {
            throwException("Accessor cannot be modified by 'final': ", element);
        }
        ElementKind kind = element.getKind();
        if (kind == ElementKind.ENUM) {
            throwException("Accessor cannot be an 'enum': ", element);
        }
        if (kind == ElementKind.ANNOTATION_TYPE) {
            throwException("Accessor cannot be an 'annotation': ", element);
        }
        // for future: record
        if ("record".equals(kind.name())) {
            throwException("Accessor cannot be a 'record': ", element);
        }
    }
}
