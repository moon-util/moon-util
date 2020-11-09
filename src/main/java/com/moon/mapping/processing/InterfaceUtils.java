package com.moon.mapping.processing;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.moon.mapping.processing.DetectUtils.*;
import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;

/**
 * @author moonsky
 */
abstract class InterfaceUtils {

    private InterfaceUtils() { }

    public static InterDefinition toPropertiesMap(TypeElement interElement) {
        DetectUtils.assertInterface(interElement);
        Map<String, GenericModel> generics = GenericUtils.parse(interElement);
        InterDefinition definition = new InterDefinition(interElement);
        List<Element> defaults = new ArrayList<>();
        for (Element element : interElement.getEnclosedElements()) {
            if (isAny(element, STATIC)) {
                continue;
            }
            if (isAny(element, DEFAULT)) {
                defaults.add(element);
                continue;
            }
            if (isSetterMethod(element)) {
                ExecutableElement elem = (ExecutableElement) element;
                InterMethod setter = toSetterMethod(elem, generics);
                ensureProperty(definition, elem).addSetter(setter);
            } else if (isGetterMethod(element)) {
                ExecutableElement elem = (ExecutableElement) element;
                InterMethod getter = toGetterMethod(elem, generics);
                ensureProperty(definition, elem).addGetter(getter);
            } else {
                throwNamedException(interElement, element);
            }
        }
        definition.onCompleted();
        return definition;
    }

    private static InterProperty ensureProperty(InterDefinition properties, ExecutableElement elem) {
        String propertyName = ElementUtils.toPropertyName(elem);
        InterProperty property = properties.get(propertyName);
        if (property == null) {
            TypeElement thisElement = (TypeElement) elem.getEnclosingElement();
            property = new InterProperty(propertyName, thisElement);
            properties.put(propertyName, property);
        }
        return property;
    }

    private static InterMethod toSetterMethod(ExecutableElement elem, Map<String, GenericModel> genericMap) {
        TypeMirror paramType = elem.getParameters().get(0).asType();
        return new InterMethod(elem, paramType.toString(), genericMap);
    }

    private static InterMethod toGetterMethod(ExecutableElement elem, Map<String, GenericModel> genericMap) {
        TypeMirror paramType = elem.getReturnType();
        return new InterMethod(elem, paramType.toString(), genericMap);
    }

    private static void throwNamedException(TypeElement interElement, Element element) {
        String elemName = ElementUtils.getQualifiedName(interElement);
        String methodName = elemName + "#" + element.toString();
        throw new IllegalStateException("接口方法: " + methodName + " 只能是 getter, setter 方法，或者请提供默认实现");
    }
}
