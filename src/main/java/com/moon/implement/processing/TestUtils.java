package com.moon.implement.processing;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import static javax.lang.model.element.ElementKind.PACKAGE;

/**
 * @author moonsky
 */
abstract class TestUtils {

    private TestUtils(){}

    static boolean isElemKind(Element elem, ElementKind kind) {
        return elem != null && elem.getKind() == kind;
    }

    static boolean isPackage(Element elem) { return isElemKind(elem, PACKAGE); }
}
