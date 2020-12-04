package com.moon.mapping.processing;

import com.moon.mapping.annotation.MappingConverter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;

/**
 * @author moonsky
 */
abstract class ConverterUtils {

    static void parseMappingConverts(
        Map<String, GenericModel> thisGenericMap, TypeElement rootElement, BasicDefinition definition
    ) {
        Types types = EnvUtils.getTypes();
        TypeElement element = rootElement;
        do {
            if (element == null) {
                return;
            }
            parseConverters(thisGenericMap, element, definition);
            for (TypeMirror anInterface : element.getInterfaces()) {
                TypeElement interElem = (TypeElement) types.asElement(anInterface);
                parseConverters(thisGenericMap, interElem, definition);
            }
            element = (TypeElement) types.asElement(element.getSuperclass());
        } while (true);
    }

    private static void parseConverters(
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
            MappingConverter[] converters = getConverters(element);
            if (converters == null || converters.length == 0) {
                continue;
            }
            parseMappingConverters(thisGenericMap, element, definition, converters);
        }
    }

    private static void parseMappingConverters(
        Map<String, GenericModel> thisGenericMap,
        Element element,
        BasicDefinition definition,
        MappingConverter[] converters
    ) {
        for (MappingConverter cvt : converters) {
            ExecutableElement method = (ExecutableElement) element;
            String propertyName = toPropertyName(method, cvt);
            BasicProperty property = definition.get(propertyName);
            if (property == null) {
                continue;
            }
            String fromClass;
            try {
                fromClass = cvt.fromClass().getCanonicalName();
            } catch (MirroredTypeException mirrored) {
                fromClass = mirrored.getTypeMirror().toString();
            }
            property.setConvert(fromClass, method, thisGenericMap);
        }
    }

    private static final String SET = "set", WITH = "with";

    private static String toPropertyName(ExecutableElement method, MappingConverter convert) {
        String set = convert.set();
        if (StringUtils.isBlank(set)) {
            String simpleName = ElemUtils.getSimpleName(method);
            String propertyName=simpleName;
            if (simpleName.startsWith(SET)) {
                propertyName = simpleName.substring(3);
            } else if (simpleName.startsWith(WITH)) {
                propertyName = simpleName.substring(4);
            }
            return StringUtils.decapitalize(propertyName);
        }
        return set;
    }

    private static MappingConverter[] getConverters(Element elem) {
        if (DetectUtils.isPublicMemberMethod(elem)) {
            ExecutableElement method = (ExecutableElement) elem;
            int size = method.getParameters().size();
            if (size == 1 && DetectUtils.isTypeKind(method.getReturnType(), TypeKind.VOID)) {
                return elem.getAnnotationsByType(MappingConverter.class);
            }
        }
        return null;
    }
}
