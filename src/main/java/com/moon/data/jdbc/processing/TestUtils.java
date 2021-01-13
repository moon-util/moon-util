package com.moon.data.jdbc.processing;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import java.util.Set;

import static javax.lang.model.element.ElementKind.*;

/**
 * @author benshaoye
 */
abstract class TestUtils {

    private TestUtils() {}


    static boolean isEnum(String value) {
        return value != null && isEnum(EnvUtils.getUtils().getTypeElement(value));
    }

    static boolean isPublic(Element elem) { return isAny(elem, Modifier.PUBLIC); }

    static boolean isMember(Element elem) { return isNotAny(elem, Modifier.STATIC); }

    static boolean isMethod(Element elem) { return elem instanceof ExecutableElement && isElemKind(elem, METHOD); }

    static boolean isEnum(Element elem) { return isElemKind(elem, ENUM); }

    static boolean isNotEnum(Element elem) { return !isEnum(elem); }

    static boolean isAbstractClass(TypeElement type) {
        return isElemKind(type, INTERFACE) || isAny(type, Modifier.ABSTRACT);
    }


    static boolean isAny(Element elem, Modifier modifier, Modifier... modifiers) {
        return a(elem, modifier, modifiers);
    }

    static boolean isNotAny(Element elem, Modifier modifier, Modifier... modifiers) {
        return !isAny(elem, modifier, modifiers);
    }

    static boolean isField(Element elem) {
        return elem instanceof VariableElement && isElemKind(elem, FIELD);
    }

    static boolean isTypeKind(TypeMirror elem, TypeKind kind) { return elem != null && elem.getKind() == kind; }

    static boolean isElemKind(Element elem, ElementKind kind) { return elem != null && elem.getKind() == kind; }

    static boolean isMappableElement(Element elem) { return elem != null; }

    static boolean isConstructor(Element elem) { return isElemKind(elem, CONSTRUCTOR); }

    static boolean isPackage(Element elem) { return isElemKind(elem, PACKAGE); }

    static boolean isMemberField(Element elem) { return isField(elem) && isMember(elem); }

    static boolean isPublicMemberMethod(Element elem) { return isMethod(elem) && isMember(elem) && isPublic(elem); }

}
