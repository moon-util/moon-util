package com.moon.mapping.processing;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;

/**
 * @author moonsky
 */
final class ProcessUtils {

    private final static String CLASS_SUFFIX = ".class";
    private final static String TOP_CLASS = Object.class.getName();

    private ProcessUtils() {}

    private static PropertyDetail ensureDetail(
        DefinitionDetail definition, String name, TypeElement parsingElement, TypeElement thisElement
    ) {
        PropertyDetail detail = definition.get(name);
        if (detail == null) {
            detail = new PropertyDetail(name, thisElement, parsingElement);
            definition.put(name, detail);
        }
        return detail;
    }

    private static void handleEnclosedElem(
        Set<String> presentKeys,
        DefinitionDetail definition,
        Element element,
        Map<String, GenericModel> genericMap,
        TypeElement parsingElement,
        TypeElement thisElement
    ) {
        if (DetectUtils.isMemberField(element)) {
            String name = element.getSimpleName().toString();
            if (presentKeys.contains(name)) {
                return;
            }
            PropertyDetail detail = ensureDetail(definition, name, parsingElement, thisElement);
            detail.setVariableElem((VariableElement) element, genericMap);
        } else if (DetectUtils.isSetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            String name = ElementUtils.toPropertyName(elem);
            if (presentKeys.contains(name)) {
                return;
            }
            PropertyDetail detail = ensureDetail(definition, name, parsingElement, thisElement);
            detail.setSetter(elem, genericMap);
        } else if (DetectUtils.isGetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            String name = ElementUtils.toPropertyName(elem);
            if (presentKeys.contains(name)) {
                return;
            }
            PropertyDetail detail = ensureDetail(definition, name, parsingElement, thisElement);
            detail.setGetter(elem, genericMap);
        } else if (DetectUtils.isConstructor(element)) {
            definition.addConstructor((ExecutableElement) element);
        }
    }

    private static DefinitionDetail parseRootPropertiesMap(final TypeElement rootElement) {
        DefinitionDetail definition = new DefinitionDetail(rootElement);
        Map<String, GenericModel> thisGenericMap = GenericUtil.parse(rootElement);
        List<? extends Element> elements = rootElement.getEnclosedElements();
        Set<String> presents = new HashSet<>();
        for (Element element : CollectUtils.emptyIfNull(elements)) {
            handleEnclosedElem(presents, definition,//
                element, thisGenericMap, rootElement, rootElement);
        }
        return definition;
    }

    private static void parseSuperPropertiesMap(
        Set<String> presentKeys, DefinitionDetail definition, TypeElement thisElement, TypeElement rootElement
    ) {
        TypeMirror superclass = thisElement.getSuperclass();
        if (superclass.toString().equals(TOP_CLASS)) {
            return;
        }
        Types types = EnvironmentUtils.getTypes();
        TypeElement superElement = ElementUtils.cast(types.asElement(superclass));
        Map<String, GenericModel> genericModelMap = GenericUtil.parse(superclass, superElement);
        List<? extends Element> elements = superElement.getEnclosedElements();
        for (Element element : elements) {
            handleEnclosedElem(presentKeys, definition, element, genericModelMap, superElement, rootElement);
        }
        presentKeys = new HashSet<>(definition.keySet());
        parseSuperPropertiesMap(presentKeys, definition, superElement, rootElement);
    }

    static DefinitionDetail toPropertiesMap(TypeElement rootElement) {
        DetectUtils.assertRootElement(rootElement);
        DefinitionDetail definition = parseRootPropertiesMap(rootElement);
        parseSuperPropertiesMap(new HashSet<>(), definition, rootElement, rootElement);
        return definition.onParseCompleted();
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
}
