package com.moon.processor.utils;

import com.moon.mapper.annotation.MappingInjector;
import com.moon.mapper.annotation.MappingProvider;
import com.moon.processor.model.DeclareGeneric;
import com.moon.processor.model.DeclareProperty;
import com.moon.processor.model.DeclaredPojo;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public enum Convert2 {
    ;

    public static void parseConverters(
        Map<String, DeclareGeneric> thisGenericMap, TypeElement rootElement, DeclaredPojo definition
    ) { parse(thisGenericMap, rootElement, definition); }

    private static void parse(
        Map<String, DeclareGeneric> thisGenericMap, TypeElement rootElement, DeclaredPojo definition
    ) {
        Types types = Environment2.getTypes();
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
        Map<String, DeclareGeneric> thisGenericMap, TypeElement classElement, DeclaredPojo definition
    ) {
        if (classElement == null) {
            return;
        }
        List<? extends Element> elements = classElement.getEnclosedElements();
        if (elements == null || elements.isEmpty()) {
            return;
        }
        for (Element element : elements) {
            MappingInjector[] is = getInjectors(element);
            if (is != null && is.length > 0) {
                Assert2.assertNotAbstract((ExecutableElement) element);
                parseMappingInjectors(thisGenericMap, element, classElement, definition, is);
            }
            MappingProvider[] ps = getProviders(element);
            if (ps != null && ps.length > 0) {
                Assert2.assertNotAbstract((ExecutableElement) element);
                parseMappingProviders(thisGenericMap, element, classElement, definition, ps);
            }
        }
    }

    private static void parseMappingProviders(
        Map<String, DeclareGeneric> thisGenericMap,
        Element element,
        TypeElement enclosingElement,
        DeclaredPojo definition,
        MappingProvider[] providers
    ) {
        String enclosingClassname = Element2.getQualifiedName(enclosingElement);
        for (MappingProvider pvd : providers) {
            ExecutableElement method = (ExecutableElement) element;
            String propertyName = toPropertyName(method, pvd, MappingProvider::value, GET, PROVIDE);
            DeclareProperty property = definition.get(propertyName);
            if (property == null) {
                continue;
            }
            String forClass = Element2.getClassname(pvd, MappingProvider::provideFor);
            String returnType = method.getReturnType().toString();
            String actualType = Generic2.mappingToActual(thisGenericMap, enclosingClassname, returnType);
            property.addProvider(forClass, actualType, Element2.getSimpleName(method));
        }
    }

    private static void parseMappingInjectors(
        Map<String, DeclareGeneric> thisGenericMap,
        Element element,
        TypeElement enclosingElement,
        DeclaredPojo definition,
        MappingInjector[] injectors
    ) {
        String enclosingClassname = Element2.getQualifiedName(enclosingElement);
        for (MappingInjector ijt : injectors) {
            ExecutableElement method = (ExecutableElement) element;
            String propertyName = toPropertyName(method, ijt, MappingInjector::value, SET, WITH, INJECT);
            DeclareProperty property = definition.get(propertyName);
            if (property == null) {
                continue;
            }
            String fromClass = Element2.getClassname(ijt, MappingInjector::injectBy);
            String returnType = method.getParameters().get(0).asType().toString();
            String actualType = Generic2.mappingToActual(thisGenericMap, enclosingClassname, returnType);
            property.addInjector(fromClass, actualType, Element2.getSimpleName(method));
        }
    }

    private static final String SET = "set", WITH = "with", INJECT = "inject", GET = "get", PROVIDE = "provide";

    private static <T> String toPropertyName(
        ExecutableElement m, T annotation, Function<T, String> getter, String... prefixes
    ) {
        String name = getter.apply(annotation);
        if (String2.isBlank(name)) {
            String propertyName = Element2.getSimpleName(m);
            if (prefixes != null) {
                for (String prefix : prefixes) {
                    if (propertyName.startsWith(prefix)) {
                        propertyName = propertyName.substring(prefix.length());
                        break;
                    }
                }
            }
            return String2.decapitalize(propertyName);
        }
        return name.trim();
    }

    private static MappingInjector[] getInjectors(Element elem) {
        if (Test2.isPublicMemberMethod(elem)) {
            ExecutableElement method = (ExecutableElement) elem;
            int size = method.getParameters().size();
            if (size == 1 && Test2.isTypeKind(method.getReturnType(), TypeKind.VOID)) {
                return elem.getAnnotationsByType(MappingInjector.class);
            }
        }
        return null;
    }

    private static MappingProvider[] getProviders(Element elem) {
        if (Test2.isPublicMemberMethod(elem)) {
            ExecutableElement method = (ExecutableElement) elem;
            if (method.getParameters().size() == 0) {
                return elem.getAnnotationsByType(MappingProvider.class);
            }
        }
        return null;
    }
}
