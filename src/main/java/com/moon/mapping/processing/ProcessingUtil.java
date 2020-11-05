package com.moon.mapping.processing;

import lombok.Data;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import java.beans.Introspector;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
final class ProcessingUtil {

    private final static String CLASS_SUFFIX = ".class", GET = "get", SET = "set", IS = "is";

    private ProcessingUtil() { }

    private final static boolean IS_IMPORTED_LOMBOK;

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

    static boolean isImportedLombok() { return IS_IMPORTED_LOMBOK; }

    static Map<String, PropertyModel> toPropertiesMap(Elements utils, TypeElement typeElement) {
        Map<String, PropertyModel> modelMap = new LinkedHashMap<>();
        for (Element member : utils.getAllMembers(typeElement)) {
            ElementKind kind = member.getKind();
            switch (kind) {
                case FIELD:
                    VariableElement var = (VariableElement) member;
                    String propertyName = var.getSimpleName().toString();
                    modelMap.computeIfAbsent(propertyName, PropertyModel::new).setProperty(var);
                    break;
                case METHOD:
                    if (isSetterElem(member)) {
                        ExecutableElement exe = (ExecutableElement) member;
                        String name = executableToPropertyName(exe);
                        modelMap.computeIfAbsent(name, PropertyModel::new).setSetter(exe);
                    } else if (isGetterElem(member)) {
                        ExecutableElement exe = (ExecutableElement) member;
                        String name = executableToPropertyName(exe);
                        modelMap.computeIfAbsent(name, PropertyModel::new).setGetter(exe);
                    }
                    break;
                default:
            }
        }
        return modelMap;
    }

    static Collection<String> getMappingForClasses(TypeElement element) {
        Collection<String> classes = new HashSet<>();
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            for (AnnotationValue value : mirror.getElementValues().values()) {
                String classname = value.getValue().toString();
                if (classname.endsWith(CLASS_SUFFIX)) {
                    classname = classname.substring(0, classname.length() - 6);
                }
                classes.add(classname);
            }
        }
        return classes;
    }

    static boolean isPublic(Element element) {
        return element != null && element.getModifiers().contains(Modifier.PUBLIC);
    }

    private static boolean isSetterElem(Element member) {
        if (member instanceof ExecutableElement) {
            ExecutableElement exe = (ExecutableElement) member;
            String name = exe.getSimpleName().toString();
            boolean maybeSet = name.length() > 3 && name.startsWith(SET);
            maybeSet = maybeSet && exe.getParameters().size() == 1;
            maybeSet = maybeSet && exe.getReturnType().getKind() == TypeKind.VOID;
            return maybeSet && isPublic(exe);
        }
        return false;
    }

    private static boolean isGetterElem(Element member) {
        if (member instanceof ExecutableElement) {
            ExecutableElement exe = (ExecutableElement) member;
            String name = exe.getSimpleName().toString();
            boolean maybeSet = exe.getParameters().isEmpty() && isPublic(exe);
            if (name.startsWith(GET)) {
                return maybeSet && !name.equals("getClass");
            } else if (name.startsWith(IS)) {
                return maybeSet && exe.getReturnType().getKind() == TypeKind.BOOLEAN;
            }
        }
        return false;
    }

    private static String executableToPropertyName(ExecutableElement element) {
        String name = element.getSimpleName().toString();
        return Introspector.decapitalize(name.substring(name.startsWith(IS) ? 2 : 3));
    }
}
