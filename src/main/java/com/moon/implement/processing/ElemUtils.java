package com.moon.implement.processing;

import javax.lang.model.element.Element;
import javax.lang.model.element.QualifiedNameable;

/**
 * @author moonsky
 */
final class ElemUtils {

    public static String getPackageName(Element element) {
        do {
            if (TestUtils.isPackage(element)) {
                return getQualifiedName((QualifiedNameable) element);
            }
            element = element.getEnclosingElement();
        } while (true);
    }

    public static String getQualifiedName(QualifiedNameable elem) {
        return elem.getQualifiedName().toString();
    }
}
