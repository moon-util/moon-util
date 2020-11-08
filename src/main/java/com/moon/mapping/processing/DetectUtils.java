package com.moon.mapping.processing;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import java.util.Set;

/**
 * @author benshaoye
 */
abstract class DetectUtils {

    private final static boolean IS_IMPORTED_LOMBOK;

    private final static String GET = "get", SET = "set", IS = "is", GET_CLASS = "getClass";

    static {
        boolean isImportedLombok;
        try {
            Data.class.toString();
            isImportedLombok = true;
        } catch (Throwable t) {
            isImportedLombok = false;
        }
        IS_IMPORTED_LOMBOK = isImportedLombok;
    }

    private DetectUtils() { }

    /**
     * 如果跟节点是接口：
     * 1. 是接口文件，没有特殊限制；
     * 2. 是内部类形式的接口，不允许被 private 修饰
     *
     * @param typeElement
     */
    static void assertInterface(TypeElement typeElement) {
        if (typeElement.getKind() != ElementKind.INTERFACE) {
            return;
        }
        Element element = typeElement;
        do {
            Element parent = element.getEnclosingElement();
            if (isPackage(parent)) {
                return;
            }
            if (isPrivate(parent)) {
                String target = ElementUtils.getQualifiedName((TypeElement) element);
                String location = parent.getSimpleName().toString();
                throw new IllegalStateException("类 " + target + " 所在位置: " + location + " 不能被 private 修饰的。");
            }
            element = parent;
        } while (true);
    }

    static void assertRootElement(TypeElement rootElement) {
        final Element parentElement = rootElement.getEnclosingElement();
        // 如果是类文件，要求必须是 public 类
        if (parentElement.getKind() == ElementKind.PACKAGE) {
            if (!DetectUtils.isPublic(rootElement)) {
                String thisClassname = rootElement.getQualifiedName().toString();
                throw new IllegalStateException("类 " + thisClassname + " 必须是被 public 修饰的公共类。");
            }
        }
    }

    static boolean hasLombokSetter(VariableElement field) {
        if (isImportedLombok()) {
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
        if (isImportedLombok()) {
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

    static boolean isImportedLombok() { return IS_IMPORTED_LOMBOK; }

    static boolean isPublic(Element elem) {
        return elem != null && elem.getModifiers().contains(Modifier.PUBLIC);
    }

    static boolean isPrivate(Element elem) {
        return elem != null && isAll(elem, Modifier.PRIVATE);
    }

    static boolean isNotPrivate(Element elem) { return !isPrivate(elem); }

    static boolean isMember(Element elem) {
        return elem != null && !elem.getModifiers().contains(Modifier.STATIC);
    }

    static boolean isMethod(Element elem) {
        return elem instanceof ExecutableElement && elem.getKind() == ElementKind.METHOD;
    }

    static boolean isField(Element elem) {
        return elem instanceof VariableElement && elem.getKind() == ElementKind.FIELD;
    }

    static boolean isAll(Element elem, Modifier modifier, Modifier... modifiers) {
        Set<Modifier> modifierSet = elem.getModifiers();
        boolean contains = modifierSet.contains(modifier);
        if (contains && modifiers != null) {
            for (Modifier m : modifiers) {
                if (!modifierSet.contains(m)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    static boolean isNotAll(Element elem, Modifier modifier, Modifier... modifiers) {
        return !isAll(elem, modifier, modifiers);
    }

    static boolean isPackage(Element elem) {
        return elem.getKind() == ElementKind.PACKAGE;
    }

    static boolean isMemberField(Element elem) {
        return isField(elem) && isMember(elem);
    }

    static boolean isSetterMethod(Element elem) {
        if (isMethod(elem) && isMember(elem) && isPublic(elem)) {
            ExecutableElement exe = (ExecutableElement) elem;
            String name = exe.getSimpleName().toString();
            boolean maybeSet = name.length() > 3 && name.startsWith(SET);
            maybeSet = maybeSet && exe.getParameters().size() == 1;
            maybeSet = maybeSet && exe.getReturnType().getKind() == TypeKind.VOID;
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
                return maybeGet && name.length() > 2 && exe.getReturnType().getKind() == TypeKind.BOOLEAN;
            }
        }
        return false;
    }

    static boolean isConstructor(Element elem) {
        return elem != null && elem.getKind() == ElementKind.CONSTRUCTOR;
    }
}
