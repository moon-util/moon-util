package com.moon.mapping.processing;

import lombok.Data;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;

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

    static void assertRootElement(TypeElement rootElement) {
        Logger.warn(rootElement.getEnclosingElement());
        final Element parentElement = rootElement.getEnclosingElement();
        // 如果是类文件，要求必须是 public 类
        if (parentElement.getKind() == ElementKind.PACKAGE) {
            if (!DetectUtils.isPublic(rootElement)) {
                String thisClassname = rootElement.getQualifiedName().toString();
                throw new IllegalStateException("类 " + thisClassname + " 必须是被 public 修饰的公共类。");
            }
        }
        // Logger.warn("=====================================================================");
        // Logger.warn(rootElement.getEnclosingElement());
        // Logger.warn(rootElement.getEnclosingElement().getKind());
    }

    static boolean isImportedLombok() { return IS_IMPORTED_LOMBOK; }

    static boolean isPublic(Element elem) {
        return elem != null && elem.getModifiers().contains(Modifier.PUBLIC);
    }

    static boolean isMember(Element elem) {
        return elem != null && !elem.getModifiers().contains(Modifier.STATIC);
    }

    static boolean isMethod(Element elem) {
        return elem instanceof ExecutableElement && elem.getKind() == ElementKind.METHOD;
    }

    static boolean isField(Element elem) {
        return elem instanceof VariableElement && elem.getKind() == ElementKind.FIELD;
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
                return maybeGet && !name.equals(GET_CLASS);
            } else if (name.startsWith(IS)) {
                return maybeGet && exe.getReturnType().getKind() == TypeKind.BOOLEAN;
            }
        }
        return false;
    }
}
