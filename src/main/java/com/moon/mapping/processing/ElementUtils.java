package com.moon.mapping.processing;

import javax.lang.model.element.TypeElement;

/**
 * @author benshaoye
 */
abstract class ElementUtils {

    private ElementUtils() {}

    public static String getQualifiedName(TypeElement elem) {
        return elem.getQualifiedName().toString();
    }
}
