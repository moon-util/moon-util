package com.moon.mapping.processing;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.beans.Introspector;
import java.util.*;

import static com.moon.mapping.processing.DetectUtils.*;

/**
 * @author benshaoye
 */
final class ProcessingUtil {

    private final static String TOP_SUPER = Object.class.getName();
    private final static String CLASS_SUFFIX = ".class";

    private ProcessingUtil() { }

    private static PropertyModel ensurePropertyModel(
        final Map<String, PropertyModel> propertiesModel,
        final String name,
        final TypeElement thisType,
        final TypeElement declareType
    ) {
        PropertyModel model = propertiesModel.get(name);
        if (model == null) {
            model = new PropertyModel(name, thisType, declareType);
            propertiesModel.put(name, model);
        }
        return model;
    }

    private static String extractGenericFinalType(
        final Map<String, GenericModel> thisGenericMap, String propertyType
    ) {
        GenericModel generic = thisGenericMap.get(propertyType);
        return generic == null ? null : generic.getSimpleFinalType();
    }

    private static void handleEnclosedElem(
        final Set<String> presentsProps,
        final Map<String, PropertyModel> propertiesModel,
        final Map<String, GenericModel> thisGenericMap,
        final Element element,
        final TypeElement thisType,
        final TypeElement declareType
    ) {
        if (isMemberField(element)) {
            VariableElement var = (VariableElement) element;
            String name = var.getSimpleName().toString();
            if (presentsProps.contains(name)) {
                return;
            }
            PropertyModel model = ensurePropertyModel(propertiesModel, name, thisType, declareType);
            model.setProperty(var);
            // 推测泛型实际类型
            String propertyType = model.getPropertyDeclareType();
            String finalType = extractGenericFinalType(thisGenericMap, propertyType);
            model.setPropertyTypename(finalType);
        } else if (isSetterMethod(element)) {
            ExecutableElement exe = (ExecutableElement) element;
            String name = toPropertyName(exe);
            if (presentsProps.contains(name)) {
                return;
            }
            PropertyModel model = ensurePropertyModel(propertiesModel, name, thisType, declareType);
            model.setSetter(exe);
            // 推测泛型实际类型
            String setterType = model.getSetterDeclareType();
            String finalType = extractGenericFinalType(thisGenericMap, setterType);
            model.setSetterTypename(finalType);
        } else if (isGetterMethod(element)) {
            ExecutableElement exe = (ExecutableElement) element;
            String name = toPropertyName(exe);
            if (presentsProps.contains(name)) {
                return;
            }
            PropertyModel model = ensurePropertyModel(propertiesModel, name, thisType, declareType);
            model.setGetter(exe);
            // 推测泛型实际类型
            String getterType = model.getGetterDeclareType();
            String finalType = extractGenericFinalType(thisGenericMap, getterType);
            model.setGetterTypename(finalType);
        }
    }

    private static MappedPropsMap parseRootPropertiesMap(
        ProcessingEnvironment env, final TypeElement rootElement
    ) {
        MappedPropsMap propsMap = new MappedPropsMap(rootElement, rootElement);
        Map<String, GenericModel> thisGenericMap = GenericUtil.parse(rootElement, env);
        List<? extends Element> elements = rootElement.getEnclosedElements();
        Set<String> presents = new HashSet<>();
        for (Element element : CollectUtils.emptyIfNull(elements)) {
            handleEnclosedElem(presents, propsMap,//
                thisGenericMap, element, rootElement, rootElement);
        }
        return propsMap;
    }

    private static Map<String, PropertyModel> parseSuperPropertiesModel(
        Set<String> properties, ProcessingEnvironment env, final TypeElement thisElement, final TypeElement rootElement
    ) {
        TypeMirror superclass = thisElement.getSuperclass();
        if (superclass.toString().equals(TOP_SUPER)) {
            return Collections.emptyMap();
        }
        Map<String, PropertyModel> propsMap = new LinkedHashMap<>();
        TypeElement superElement = cast(env.getTypeUtils().asElement(superclass));
        Map<String, GenericModel> genericModelMap = GenericUtil.parse(superclass, superElement, env);
        List<? extends Element> elements = superElement.getEnclosedElements();
        for (Element element : elements) {
            handleEnclosedElem(properties, propsMap, genericModelMap, element, rootElement, superElement);
        }
        properties.addAll(propsMap.keySet());
        propsMap.putAll(parseSuperPropertiesModel(properties, env, superElement, rootElement));
        return propsMap;
    }

    static MappedPropsMap toPropertiesModelMap(ProcessingEnvironment env, TypeElement rootElement) {
        DetectUtils.assertRootElement(rootElement);
        final MappedPropsMap propsMap = parseRootPropertiesMap(env, rootElement);
        final Set<String> presentsProps = new HashSet<>(propsMap.keySet());
        propsMap.putAll(parseSuperPropertiesModel(presentsProps, env, rootElement, rootElement));
        return propsMap.reversed();
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

    private static String toPropertyName(ExecutableElement element) {
        String name = element.getSimpleName().toString();
        return Introspector.decapitalize(name.substring(name.startsWith("is") ? 2 : 3));
    }

    private static <T> T cast(Object obj) { return (T) obj; }
}
