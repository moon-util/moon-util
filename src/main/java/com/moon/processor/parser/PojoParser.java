package com.moon.processor.parser;

import com.moon.mapper.annotation.IgnoreMode;
import com.moon.mapper.annotation.MapperIgnoreFields;
import com.moon.mapper.annotation.Mapping;
import com.moon.processor.manager.NameManager;
import com.moon.processor.model.*;
import com.moon.processor.utils.*;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public class PojoParser {

    private final static String TOP_CLASS = Object.class.getName();

    /**
     * 声明的目标类
     */
    private final TypeElement thisElement;
    /**
     * 容纳已解析完成元素的容器，也是最终返回对象
     */
    private final DeclaredPojo declaredPojo;
    /**
     * 泛型声明
     */
    private final Map<String, DeclareGeneric> thisGenericMap;
    /**
     * 忽略容器
     */
    private final Map<String, IgnoredModel> ignoringMap;
    /**
     * 已解析的属性
     */
    private final Set<String> parsedKeys = new HashSet<>();
    /**
     * 已解析
     */
    private final Set<String> parsedExpandMethods = new HashSet<>();

    private final Types types = Environment2.getTypes();


    private PojoParser(TypeElement thisElement, NameManager nameManager) {
        this.declaredPojo = new DeclaredPojo(thisElement, nameManager);
        this.thisGenericMap = Generic2.from(thisElement);
        this.ignoringMap = parseMappingIgnoring(thisElement);
        this.thisElement = thisElement;
    }

    private boolean isParsedProperty(String name) { return parsedKeys.contains(name); }

    private boolean isParsedSetter(String name, String actualType) {
        if (isParsedProperty(name)) {
            return declaredPojo.get(name).getSetters().get(actualType) != null;
        }
        return false;
    }

    private boolean isParsedGetter(String name) {
        if (isParsedProperty(name)) {
            return !declaredPojo.get(name).getGetters().isEmpty();
        }
        return false;
    }

    private final static String TEMPLATE = "{} {} {}({})";

    /**
     * 处理扩展方法：
     * <p>
     * 1. 抽象方法要求之类必须实现；
     * 2. 非抽象方法没特殊要求
     *
     * @param method
     * @param parsingElem
     */
    private void handleExpandMethod(ExecutableElement method, String parsingElem) {
        if (Test2.isAny(method, Modifier.ABSTRACT)) {

        } else {
            String simpleName = Element2.getSimpleName(method);
            String returnType = Generic2.mappingToActual(thisGenericMap,
                parsingElem,
                method.getReturnType().toString());
            String parameters = method.getParameters()
                .stream()
                .map(p -> Generic2.mappingToActual(thisGenericMap, parsingElem, p.asType().toString()))
                .collect(Collectors.joining(","));
            if (Test2.isAny(method, Modifier.PUBLIC)) {
                String.format(TEMPLATE, Const2.PUBLIC, returnType, simpleName, parameters);
            } else if (Test2.isAny(method, Modifier.PROTECTED)) {
                String.format(TEMPLATE, Const2.PROTECTED, returnType, simpleName, parameters);
                String.format(TEMPLATE, Const2.PUBLIC, returnType, simpleName, parameters);
            }
        }
    }

    private String findActualType(String parsingClass, String declaredType) {
        return Generic2.mappingToActual(thisGenericMap, parsingClass, declaredType);
    }

    private void handleEnclosedElem(Element element, TypeElement parsingElem) {
        String parsingClass = Element2.getQualifiedName(parsingElem);
        String declaredType, actualType;
        if (Test2.isMemberField(element)) {
            String name = element.getSimpleName().toString();
            if (isParsedProperty(name)) {
                return;
            }
            DeclareProperty prop = declareProperty(name, parsingElem);
            handleMapping(ignoringMap.get(name), element, prop::addFieldMapping);
            prop.setField((VariableElement) element, thisGenericMap);
        } else if (Test2.isSetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            declaredType = Element2.getSetterDeclareType(elem);
            actualType = findActualType(parsingClass, declaredType);
            String name = Element2.toPropertyName(elem);
            if (isParsedSetter(name, actualType)) {
                return;
            }
            DeclareProperty prop = declareProperty(name, parsingElem);
            handleMapping(ignoringMap.get(name), element, prop::addSetterMapping);
            prop.addSetter(DeclareMethod.ofDeclared(elem, declaredType, actualType));
        } else if (Test2.isGetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            declaredType = Element2.getGetterDeclareType(elem);
            actualType = findActualType(parsingClass, declaredType);
            String name = Element2.toPropertyName(elem);
            if (isParsedGetter(name)) {
                return;
            }
            DeclareProperty prop = declareProperty(name, parsingElem);
            handleMapping(ignoringMap.get(name), element, prop::addGetterMapping);
            prop.addGetter(DeclareMethod.ofDeclared(elem, declaredType, actualType));
        } else if (Test2.isConstructor(element)) {
            // definition.addConstructor((ExecutableElement) element);
        } else if (Test2.isMethod(element)) {
            // 如果根类是接口或抽象类，那么这里的方法不能是抽象方法
            // 但由于这里可能是父接口或父类，子类可能已实现相应方法，故这里虽然是抽象的
            // 但不能报错
            // 实际子类没有实现的，这里就应该报错
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
        parsedKeys.addAll(declaredPojo.keySet());
        parseSuperElements(superElement);
    }

    private void parseRootElements(){
        parseElements(thisElement.getEnclosedElements(), thisElement);
        parseInterfaces(thisElement);
        parsedKeys.addAll(declaredPojo.keySet());
    }

    private DeclaredPojo doParse() {
        parseRootElements();
        parseSuperElements(thisElement);
        Convert2.parseConverters(thisGenericMap, thisElement, declaredPojo);
        declaredPojo.onCompleted();
        return declaredPojo;
    }

    public static DeclaredPojo parse(TypeElement thisElement, NameManager nameManager) {
        return new PojoParser(thisElement, nameManager).doParse();
    }


    /**
     * 字段名: 模式+目标类
     *
     * @param rootElement
     *
     * @return
     */
    private static Map<String, IgnoredModel> parseMappingIgnoring(TypeElement rootElement) {
        Map<String, IgnoredModel> ignoringMap = new HashMap<>(8);
        MapperIgnoreFields[] ignoringAll = rootElement.getAnnotationsByType(MapperIgnoreFields.class);
        if (ignoringAll != null) {
            for (MapperIgnoreFields ignoring : ignoringAll) {
                IgnoreMode mode = ignoring.ignore();
                String targetCls = getTargetCls(ignoring, MapperIgnoreFields::target);
                for (String name : ignoring.value()) {
                    ignoringMap.put(name, new IgnoredModel(mode, targetCls));
                }
            }
        }
        return ignoringMap;
    }

    private static void handleMapping(
        IgnoredModel ignored, Element element, Consumer<DeclareMapping> handler
    ) {
        boolean ignoredWasUnused = true;
        Mapping[] properties = element.getAnnotationsByType(Mapping.class);
        if (properties != null && properties.length > 0) {
            for (Mapping property : properties) {
                IgnoreMode ignore = property.ignore();
                String value = property.value();
                String format = property.format();
                String defaultValue = property.defaultValue();
                String targetCls = getTargetCls(property, Mapping::target);
                if (ignored != null) {
                    ignore = ignored.getMode();
                    if (!Objects.equals(targetCls, ignored.getTargetCls())) {
                        targetCls = ignored.getTargetCls();
                        handler.accept(new DeclareMapping(targetCls, value, format, defaultValue, ignore));
                        ignoredWasUnused = false;
                        continue;
                    }
                }
                handler.accept(new DeclareMapping(targetCls, value, format, defaultValue, ignore));
            }
        }
        if (ignoredWasUnused && ignored != null) {
            handler.accept(new DeclareMapping(ignored.getTargetCls(), "", "", "", ignored.getMode()));
        }
    }

    private DeclareProperty declareProperty(
        String name, TypeElement parsingElement
    ) {
        DeclareProperty detail = declaredPojo.get(name);
        if (detail == null) {
            detail = new DeclareProperty(name, parsingElement, thisElement);
            declaredPojo.put(name, detail);
        }
        return detail;
    }

    private static boolean isTopElement(Element element, TypeMirror superclass) {
        return element == null || superclass.toString().equals(TOP_CLASS);
    }

    private static <T> String getTargetCls(T t, Function<T, Class<?>> classGetter) {
        String targetCls = Element2.getClassname(t, classGetter);
        return Test2.isBasicType(targetCls) ? "void" : targetCls;
    }
}
