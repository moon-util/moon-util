package com.moon.mapping.processing;

import com.moon.mapping.annotation.MapProperty;
import com.moon.mapping.annotation.MappingFor;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;

/**
 * @author moonsky
 */
final class ProcessUtils {

    private final static String MAPPING_FOR_CLASSNAME = MappingFor.class.getCanonicalName();
    private final static String CLASS_SUFFIX = ".class";
    private final static String TOP_CLASS = Object.class.getName();

    private ProcessUtils() {}

    private static BasicProperty ensureDetail(
        BasicDefinition definition, String name, TypeElement parsingElement, TypeElement thisElement
    ) {
        BasicProperty detail = definition.get(name);
        if (detail == null) {
            detail = new BasicProperty(name, parsingElement, thisElement);
            definition.put(name, detail);
        }
        return detail;
    }

    private static void handleMapProperty(BasicDefinition definition, Element element, String name) {
        MapProperty[] properties = element.getAnnotationsByType(MapProperty.class);
        for (MapProperty property : properties) {
            boolean ignore = property.ignore();
            String value = property.value();
            String format = property.format();
            String defaultValue = property.defaultValue();
            String targetCls;
            try {
                targetCls = property.target().getCanonicalName();
            } catch (MirroredTypeException mirrored) {
                targetCls = mirrored.getTypeMirror().toString();
            }
            PropertyAttr attr = new PropertyAttr(targetCls, value, format, defaultValue, ignore);
            definition.addPropertyAttr(attr.getTargetCls(), name, attr);
        }
    }

    private static void handleEnclosedElem(
        Set<String> presentKeys,
        BasicDefinition definition,
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
            BasicProperty prop = ensureDetail(definition, name, parsingElement, thisElement);
            prop.setField((VariableElement) element, genericMap);
            handleMapProperty(definition, element, name);
        } else if (DetectUtils.isSetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            String name = ElementUtils.toPropertyName(elem);
            if (presentKeys.contains(name)) {
                return;
            }
            BasicProperty prop = ensureDetail(definition, name, parsingElement, thisElement);
            handleMapProperty(definition, element, name);
            prop.setSetter(elem, genericMap);
        } else if (DetectUtils.isGetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            String name = ElementUtils.toPropertyName(elem);
            if (presentKeys.contains(name)) {
                return;
            }
            BasicProperty prop = ensureDetail(definition, name, parsingElement, thisElement);
            handleMapProperty(definition, element, name);
            prop.setGetter(elem, genericMap);
        } else if (DetectUtils.isConstructor(element)) {
            // definition.addConstructor((ExecutableElement) element);
        }
    }

    @SuppressWarnings("all")
    private static BasicDefinition parseRootPropertiesMap(final TypeElement rootElement) {
        BasicDefinition definition = new BasicDefinition(rootElement);
        Map<String, GenericModel> thisGenericMap = GenericUtils.parse(rootElement);
        List<? extends Element> elements = rootElement.getEnclosedElements();
        Set<String> presents = new HashSet<>();
        for (Element element : CollectUtils.emptyIfNull(elements)) {
            handleEnclosedElem(presents, definition,//
                element, thisGenericMap, rootElement, rootElement);
        }
        return definition;
    }

    private static void parseSuperPropertiesMap(
        Set<String> presentKeys, BasicDefinition definition, TypeElement thisElement, TypeElement rootElement
    ) {
        TypeMirror superclass = thisElement.getSuperclass();
        if (superclass.toString().equals(TOP_CLASS)) {
            return;
        }
        Types types = EnvUtils.getTypes();
        TypeElement superElement = ElementUtils.cast(types.asElement(superclass));
        if (superElement == null) {
            return;
        }
        Map<String, GenericModel> genericModelMap = GenericUtils.parse(superclass, superElement);
        List<? extends Element> elements = superElement.getEnclosedElements();
        for (Element element : elements) {
            handleEnclosedElem(presentKeys, definition, element, genericModelMap, superElement, rootElement);
        }
        presentKeys = new HashSet<>(definition.keySet());
        parseSuperPropertiesMap(presentKeys, definition, superElement, rootElement);
    }

    static BasicDefinition toPropertiesMap(TypeElement rootElement) {
        DetectUtils.assertRootElement(rootElement);
        BasicDefinition definition = parseRootPropertiesMap(rootElement);
        parseSuperPropertiesMap(new HashSet<>(), definition, rootElement, rootElement);
        definition.onCompleted();
        return definition;
    }

    static Collection<String> getMappingForClasses(TypeElement element) {
        TypeMirror supportedType = EnvUtils.getUtils().getTypeElement(MAPPING_FOR_CLASSNAME).asType();
        Collection<String> classes = new HashSet<>();
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            DeclaredType declaredType = mirror.getAnnotationType();
            if (!EnvUtils.getTypes().isSameType(supportedType, declaredType)) {
                continue;
            }
            for (AnnotationValue value : mirror.getElementValues().values()) {
                String[] classnames = value.getValue().toString().split(",");
                for (String classname : classnames) {
                    if (classname.endsWith(CLASS_SUFFIX)) {
                        classname = classname.substring(0, classname.length() - 6);
                    }
                    classes.add(classname);
                }
            }
        }
        return classes;
    }
}
