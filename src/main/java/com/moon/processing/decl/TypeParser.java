package com.moon.processing.decl;

import com.moon.processing.util.Processing2;
import com.moon.processor.utils.Collect2;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.function.Function;

/**
 * @author benshaoye
 */
final class TypeParser {

    private final static String TOP_CLASS = Object.class.getName();
    /**
     * typeElement.getQualifiedName().toString()
     *
     * @see TypeElement#getQualifiedName()
     */
    private final String typeClassname;
    private final TypeElement typeElement;
    private final TypeDeclared typeDeclared;
    private final Map<String, GenericDeclared> thisGenericMap;
    private final Map<String, PropertyDeclared> properties = new LinkedHashMap<>();
    private final Map<String, FieldDeclared> staticFieldsMap = new LinkedHashMap<>();
    private final List<MethodDeclared> methods = new ArrayList<>();
    private final Set<String> parsedKeys;

    private final Types types = Processing2.getTypes();
    private final Elements elements = Processing2.getUtils();

    TypeParser(TypeDeclared typeDeclared) {
        this.typeElement = typeDeclared.getTypeElement();
        this.typeClassname = typeDeclared.getTypeClassname();
        this.thisGenericMap = typeDeclared.getGenericDeclaredMap();
        this.typeDeclared = typeDeclared;
        this.parsedKeys = new HashSet<>();
    }

    private boolean isParsedProperty(String name) { return parsedKeys.contains(name); }

    private boolean isParsedSetter(String name, String setterActualType) {
        return isParsedProperty(name) && properties.get(name).isWriteable(setterActualType);
    }

    private boolean isParsedGetter(String name) {
        return isParsedProperty(name) && properties.get(name).isReadable();
    }

    private String getActualType(String parsingClass, String declaredType) {
        return Generic2.mapToActual(thisGenericMap, parsingClass, declaredType);
    }

    private PropertyDeclared withPropertyDeclared(String name) {
        PropertyDeclared declared = properties.get(name);
        if (declared == null) {
            declared = new PropertyDeclared(typeElement, name, thisGenericMap);
        }
        return declared;
    }

    private void handleEnclosedElem(Element element, TypeElement parsingElem) {
        String parsingClass = Element2.getQualifiedName(parsingElem);
        String declaredType, actualType;
        if (Test2.isMemberField(element)) {
            String name = element.getSimpleName().toString();
            if (isParsedProperty(name)) {
                return;
            }
            withPropertyDeclared(name).withFieldDeclared((VariableElement) element);
        } else if (Test2.isSetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            declaredType = Element2.getSetterDeclareType(elem);
            actualType = getActualType(parsingClass, declaredType);
            String name = Element2.toPropertyName(elem);
            if (isParsedSetter(name, actualType)) {
                return;
            }
            withPropertyDeclared(name).withSetterMethodDeclared(elem, actualType);
        } else if (Test2.isGetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            String name = Element2.toPropertyName(elem);
            if (isParsedGetter(name)) {
                return;
            }
            withPropertyDeclared(name).withGetterMethodDeclared(elem);
        } else if (Test2.isConstructor(element)) {
            // definition.addConstructor((ExecutableElement) element);
        } else if (Test2.isMethod(element)) {
            // 如果根类是接口或抽象类，那么这里的方法不能是抽象方法
            // 但由于这里可能是父接口或父类，子类可能已实现相应方法，故这里虽然是抽象的
            // 但不能报错
            // 实际子类没有实现的，这里就应该报错
        } else if (Test2.isField(element)) {
            FieldDeclared fieldDeclared = new FieldDeclared(typeElement,
                parsingElem,
                (VariableElement) element,
                thisGenericMap);
            staticFieldsMap.put(fieldDeclared.getName(), fieldDeclared);
        }
    }

    private void parseElements(List<? extends Element> elements, TypeElement parsingElem) {
        for (Element element : Collect2.emptyIfNull(elements)) {
            handleEnclosedElem(element, parsingElem);
        }
    }

    private void parseInterfaces(TypeElement parsingElem) {
        List<? extends TypeMirror> mirrors = parsingElem.getInterfaces();
        if (Collect2.isEmpty(mirrors)) {
            return;
        }
        for (TypeMirror mirror : mirrors) {
            Element element = types.asElement(mirror);
            if (isTopElement(element, mirror)) {
                continue;
            }
            TypeElement interElem = Element2.cast(element);
            parseElements(interElem.getEnclosedElements(), interElem);
            parseSuperElements(interElem);
        }
    }

    private void parseSuperElements(TypeElement justParsedElement) {
        TypeMirror superclass = justParsedElement.getSuperclass();
        Element superElem = types.asElement(superclass);
        if (isTopElement(superElem, superclass)) {
            return;
        }
        TypeElement superElement = Element2.cast(superElem);
        parseElements(superElement.getEnclosedElements(), superElement);
        parseInterfaces(superElement);
        parsedKeys.addAll(properties.keySet());
        parseSuperElements(superElement);
    }

    private void parseRootElements() {
        parseElements(typeElement.getEnclosedElements(), typeElement);
        parseInterfaces(typeElement);
        parsedKeys.addAll(properties.keySet());
    }

    public TypeDeclared doParseTypeDeclared() {
        parseRootElements();
        parseSuperElements(typeElement);
        typeDeclared.setProperties(properties);
        typeDeclared.setStaticFieldsMap(this.staticFieldsMap);

        return this.typeDeclared;
    }

    private static boolean isTopElement(Element element, TypeMirror superclass) {
        return element == null || superclass.toString().equals(TOP_CLASS);
    }

    private static <T> String getTargetCls(T t, Function<T, Class<?>> classFunction) {
        String classname = Element2.getClassname(t, classFunction);
        return Test2.isBasicType(classname) ? "void" : classname;
    }
}
