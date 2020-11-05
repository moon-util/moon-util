package com.moon.mapping.processing;

import lombok.Data;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.beans.Introspector;
import java.util.*;

/**
 * @author benshaoye
 */
final class ProcessingUtil {

    private final static String TOP_SUPER = Object.class.getName();
    private final static String CLASS_SUFFIX = ".class", GET_CLASS = "getClass";
    private final static String GET = "get", SET = "set", IS = "is";

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

    private static Map<String, PropertyModel> parseRootPropertiesMap(TypeElement typeElement) {
        Map<String, PropertyModel> propertiesModel = new LinkedHashMap<>();
        List<? extends Element> elements = typeElement.getEnclosedElements();
        for (Element element : elements) {
            if (isMemberField(element)) {
                VariableElement var = (VariableElement) element;
                String name = var.getSimpleName().toString();
                propertiesModel.computeIfAbsent(name, name1 -> new PropertyModel(name1, null)).setProperty(var);
            } else if (isSetterMethod(element)) {
                ExecutableElement exe = (ExecutableElement) element;
                String name = executableToPropertyName(exe);
                propertiesModel.computeIfAbsent(name, name1 -> new PropertyModel(name1, null)).setSetter(exe);
            } else if (isGetterMethod(element)) {
                ExecutableElement exe = (ExecutableElement) element;
                String name = executableToPropertyName(exe);
                propertiesModel.computeIfAbsent(name, name1 -> new PropertyModel(name1, null)).setGetter(exe);
            }
        }
        return propertiesModel;
    }

    private static Map<String, PropertyModel> parseSuperPropertiesModel(
        Set<String> properties, ProcessingEnvironment env, TypeElement typeElement
    ) {
        TypeMirror superclass = typeElement.getSuperclass();
        if (superclass.toString().equals(TOP_SUPER)) {
            return Collections.emptyMap();
        }
        Map<String, PropertyModel> propsMap = new LinkedHashMap<>();
        TypeElement superTyped = cast(env.getTypeUtils().asElement(superclass));
        Map<String, GenericModel> genericModelMap = GenericUtil.parse(superclass, superTyped, env);
        List<? extends Element> elements = superTyped.getEnclosedElements();
        for (Element element : elements) {
            if (isMemberField(element)) {
                VariableElement var = (VariableElement) element;
                String name = var.getSimpleName().toString();
                if (properties.contains(name)) {
                    continue;
                }
                propsMap.computeIfAbsent(name, PropertyModel::new).setProperty(var);
            } else if (isSetterMethod(element)) {
                // setter 方法
                ExecutableElement exe = (ExecutableElement) element;
                String name = executableToPropertyName(exe);
                if (properties.contains(name)) {
                    continue;
                }
                PropertyModel setter = propsMap.computeIfAbsent(name, PropertyModel::new);
                setter.setSetter(exe);
                String setterType = setter.getDefaultSetterType();
                GenericModel generic = genericModelMap.get(setterType);
                setter.setSetterTypename(generic.getSimpleValue());
            } else if (isGetterMethod(element)) {
                // getter 方法
                ExecutableElement exe = (ExecutableElement) element;
                String name = executableToPropertyName(exe);
                if (properties.contains(name)) {
                    continue;
                }
                propsMap.computeIfAbsent(name, PropertyModel::new).setGetter(exe);
            }
        }
        propsMap.putAll(parseSuperPropertiesModel(propsMap.keySet(), env, superTyped));
        return propsMap;
    }

    static Map<String, PropertyModel> toPropertiesModelMap(ProcessingEnvironment env, TypeElement typeElement) {
        Map<String, PropertyModel> propertiesMap = parseRootPropertiesMap(typeElement);
        propertiesMap.putAll(parseSuperPropertiesModel(propertiesMap.keySet(), env, typeElement));
        return propertiesMap;
    }

    static Collection<String> getThatClasses(TypeElement element) {
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

    private static boolean isMemberField(Element elem) {
        return isField(elem) && isMember(elem);
    }

    private static boolean isSetterMethod(Element elem) {
        if (isMethod(elem) && isMember(elem)) {
            ExecutableElement exe = (ExecutableElement) elem;
            String name = exe.getSimpleName().toString();
            boolean maybeSet = name.length() > 3 && name.startsWith(SET);
            maybeSet = maybeSet && exe.getParameters().size() == 1;
            maybeSet = maybeSet && exe.getReturnType().getKind() == TypeKind.VOID;
            return maybeSet && isPublic(exe);
        }
        return false;
    }

    private static boolean isGetterMethod(Element elem) {
        if (isMethod(elem) && isMember(elem)) {
            ExecutableElement exe = (ExecutableElement) elem;
            String name = exe.getSimpleName().toString();
            boolean maybeGet = exe.getParameters().isEmpty() && isPublic(exe);
            if (name.startsWith(GET)) {
                return maybeGet && !name.equals(GET_CLASS);
            } else if (name.startsWith(IS)) {
                return maybeGet && exe.getReturnType().getKind() == TypeKind.BOOLEAN;
            }
        }
        return false;
    }

    private static String executableToPropertyName(ExecutableElement element) {
        String name = element.getSimpleName().toString();
        return Introspector.decapitalize(name.substring(name.startsWith(IS) ? 2 : 3));
    }

    private static <T> T cast(Object obj) { return (T) obj; }

    static boolean isPublic(Element elem) {
        return elem != null && elem.getModifiers().contains(Modifier.PUBLIC);
    }

    static boolean isMember(Element elem) {
        return elem != null && !elem.getModifiers().contains(Modifier.STATIC);
    }

    static boolean isMethod(Element elem) {
        return elem.getKind() == ElementKind.METHOD && elem instanceof ExecutableElement;
    }

    static boolean isField(Element elem) {
        return elem.getKind() == ElementKind.FIELD && elem instanceof VariableElement;
    }
}
