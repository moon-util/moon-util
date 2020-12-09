package com.moon.mapping.processing;

import com.moon.mapping.annotation.MappingInjector;
import com.moon.mapping.annotation.MappingProvider;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 解析基于{@link MappingInjector}、{@link MappingProvider}的自定义转换器
 * @author moonsky
 */
abstract class ConverterUtils {

    static void parseConverters(
        Map<String, GenericModel> thisGenericMap, TypeElement rootElement, BasicDefinition definition
    ) { parse(thisGenericMap, rootElement, definition); }

    private static void parse(
        Map<String, GenericModel> thisGenericMap, TypeElement rootElement, BasicDefinition definition
    ) {
        Types types = EnvUtils.getTypes();
        TypeElement element = rootElement;
        do {
            if (element == null) {
                return;
            }
            doParse(thisGenericMap, element, definition);
            for (TypeMirror anInterface : element.getInterfaces()) {
                TypeElement interElem = (TypeElement) types.asElement(anInterface);
                doParse(thisGenericMap, interElem, definition);
            }
            element = (TypeElement) types.asElement(element.getSuperclass());
        } while (true);
    }

    private static void doParse(
        Map<String, GenericModel> thisGenericMap, TypeElement classElement, BasicDefinition definition
    ) {
        if (classElement == null) {
            return;
        }
        List<? extends Element> elements = classElement.getEnclosedElements();
        if (elements == null || elements.isEmpty()) {
            return;
        }
        for (Element element : elements) {
            MappingInjector[] cs = getInjectors(element);
            if (cs != null && cs.length > 0) {
                parseMappingInjectors(thisGenericMap, element, definition, cs);
            }
            MappingProvider[] ps = getProviders(element);
            if (ps != null && ps.length > 0) {
                parseMappingProviders(thisGenericMap, element, definition, ps);
            }
        }
    }

    private static void parseMappingProviders(
        Map<String, GenericModel> thisGenericMap,
        Element element,
        BasicDefinition definition,
        MappingProvider[] providers
    ) {
        for (MappingProvider pvd : providers) {
            ExecutableElement method = (ExecutableElement) element;
            String propertyName = toPropertyName(method, pvd, MappingProvider::value, GET, PROVIDE);
            BasicProperty property = definition.get(propertyName);
            if (property == null) {
                continue;
            }
            String forClass;
            try {
                forClass = pvd.provideFor().getCanonicalName();
            } catch (MirroredTypeException mirrored) {
                forClass = mirrored.getTypeMirror().toString();
            }
            property.setProvider(forClass, method, thisGenericMap);
        }
    }

    private static void parseMappingInjectors(
        Map<String, GenericModel> thisGenericMap,
        Element element,
        BasicDefinition definition,
        MappingInjector[] converters
    ) {
        for (MappingInjector cvt : converters) {
            ExecutableElement method = (ExecutableElement) element;
            String propertyName = toPropertyName(method, cvt, MappingInjector::value, SET, WITH, INJECT);
            BasicProperty property = definition.get(propertyName);
            if (property == null) {
                continue;
            }
            String fromClass;
            try {
                fromClass = cvt.injectBy().getCanonicalName();
            } catch (MirroredTypeException mirrored) {
                fromClass = mirrored.getTypeMirror().toString();
            }
            property.setConverter(fromClass, method, thisGenericMap);
        }
    }

    private static final String SET = "set", WITH = "with", INJECT = "inject", GET = "get", PROVIDE = "provide";

    private static <T> String toPropertyName(
        ExecutableElement m, T annotation, Function<T, String> getter, String... prefixes
    ) {
        String name = getter.apply(annotation);
        if (StringUtils.isBlank(name)) {
            String propertyName = ElemUtils.getSimpleName(m);
            if (prefixes != null) {
                for (String prefix : prefixes) {
                    if (propertyName.startsWith(prefix)) {
                        propertyName = propertyName.substring(prefix.length());
                        break;
                    }
                }
            }
            return StringUtils.decapitalize(propertyName);
        }
        return name.trim();
    }

    private static MappingInjector[] getInjectors(Element elem) {
        if (DetectUtils.isPublicMemberMethod(elem)) {
            ExecutableElement method = (ExecutableElement) elem;
            int size = method.getParameters().size();
            if (size == 1 && DetectUtils.isTypeKind(method.getReturnType(), TypeKind.VOID)) {
                return elem.getAnnotationsByType(MappingInjector.class);
            }
        }
        return null;
    }

    private static MappingProvider[] getProviders(Element elem) {
        if (DetectUtils.isPublicMemberMethod(elem)) {
            ExecutableElement method = (ExecutableElement) elem;
            if (method.getParameters().size() == 0) {
                return elem.getAnnotationsByType(MappingProvider.class);
            }
        }
        return null;
    }
}
