package com.moon.mapping.processing;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Set;

import static com.moon.mapping.processing.ElementUtils.getQualifiedName;
import static javax.lang.model.element.ElementKind.*;

/**
 * @author benshaoye
 */
abstract class DetectUtils {

    private final static String GET = "get", SET = "set", IS = "is", GET_CLASS = "getClass";

    private DetectUtils() { }

    private static void requireOf(TypeElement elem, Modifier modifier) {
        if (!isAny(elem, modifier)) {
            String name = getQualifiedName(elem);
            String m = modifier.toString().toLowerCase();
            throw new IllegalStateException("类 " + name + " 必须被 " + m + " 修饰。");
        }
    }

    static void assertRootElement(TypeElement element) {
        if (isElemKind(element, ElementKind.INTERFACE)) {
            throw new IllegalStateException("不能映射接口: " + getQualifiedName(element));
        }
        if (isElemKind(element, ENUM)) {
            throw new IllegalStateException("不能映射枚举类: " + getQualifiedName(element));
        }
        if (isAny(element, Modifier.ABSTRACT)) {
            throw new IllegalStateException("不能映射抽象类: " + getQualifiedName(element));
        }
        for (; true;) {
            requireOf(element, Modifier.PUBLIC);
            Element enclosing = element.getEnclosingElement();
            if (isPackage(enclosing)) {
                break;
            }
            requireOf(element, Modifier.STATIC);
            element = (TypeElement) enclosing;
        }
    }

    static boolean hasLombokSetter(VariableElement field) {
        if (Imported.LOMBOK) {
            if (field == null) {
                return false;
            }
            Setter setter = field.getAnnotation(Setter.class);
            if (setter != null) {
                return setter.value() == AccessLevel.PUBLIC;
            } else {
                Element element = field.getEnclosingElement();
                return element.getAnnotation(Data.class) != null;
            }
        }
        return false;
    }

    static boolean hasLombokGetter(VariableElement field) {
        if (Imported.LOMBOK) {
            if (field == null) {
                return false;
            }
            Getter getter = field.getAnnotation(Getter.class);
            if (getter != null) {
                return getter.value() == AccessLevel.PUBLIC;
            } else {
                Element element = field.getEnclosingElement();
                return element.getAnnotation(Data.class) != null;
            }
        }
        return false;
    }


    static boolean isPublic(Element elem) { return isAny(elem, Modifier.PUBLIC); }

    static boolean isMember(Element elem) { return isNotAny(elem, Modifier.STATIC); }

    static boolean isMethod(Element elem) {
        return elem instanceof ExecutableElement && isElemKind(elem, METHOD);
    }

    static boolean isNotEnum(Element elem) { return !isElemKind(elem, ENUM); }

    static boolean isField(Element elem) {
        return elem instanceof VariableElement && isElemKind(elem, FIELD);
    }

    static boolean isDigit(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    static boolean isVar(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        char value = str.trim().charAt(0);
        return Character.isLetter(value) || value == '_' || value == '$';
    }

    static boolean isAny(Element elem, Modifier modifier, Modifier... modifiers) {
        Set<Modifier> modifierSet = elem.getModifiers();
        boolean contains = modifierSet.contains(modifier);
        if (contains) {
            return true;
        } else if (modifiers != null) {
            for (Modifier m : modifiers) {
                if (modifierSet.contains(m)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isNotAny(Element elem, Modifier modifier, Modifier... modifiers) {
        return !isAny(elem, modifier, modifiers);
    }

    static boolean isAnyNull(Object... values) {
        for (Object value : values) {
            if (value == null) {
                return true;
            }
        }
        return false;
    }

    static boolean isTypeKind(TypeMirror elem, TypeKind kind) {
        return elem != null && elem.getKind() == kind;
    }

    static boolean isElemKind(Element elem, ElementKind kind) {
        return elem != null && elem.getKind() == kind;
    }

    static boolean isMappableElement(Element elem) { return elem != null; }

    static boolean isConstructor(Element elem) { return isElemKind(elem, CONSTRUCTOR); }

    static boolean isPackage(Element elem) { return isElemKind(elem, PACKAGE); }

    static boolean isMemberField(Element elem) { return isField(elem) && isMember(elem); }

    static boolean isSetterMethod(Element elem) {
        if (isMethod(elem) && isMember(elem) && isPublic(elem)) {
            ExecutableElement exe = (ExecutableElement) elem;
            String name = exe.getSimpleName().toString();
            boolean maybeSet = name.length() > 3 && name.startsWith(SET);
            maybeSet = maybeSet && exe.getParameters().size() == 1;
            maybeSet = maybeSet && isTypeKind(exe.getReturnType(), TypeKind.VOID);
            return maybeSet;
        }
        return false;
    }

    static boolean isGetterMethod(Element elem) {
        if (isMethod(elem) && isMember(elem) && isPublic(elem)) {
            ExecutableElement exe = (ExecutableElement) elem;
            String name = exe.getSimpleName().toString();
            boolean maybeGet = exe.getParameters().isEmpty();
            if (name.startsWith(GET)) {
                return maybeGet && name.length() > 3 && !name.equals(GET_CLASS);
            } else if (name.startsWith(IS)) {
                return maybeGet && name.length() > 2 && isTypeKind(exe.getReturnType(), TypeKind.BOOLEAN);
            }
        }
        return false;
    }
}
