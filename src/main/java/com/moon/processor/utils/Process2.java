package com.moon.processor.utils;

import com.moon.mapper.annotation.IgnoreMode;
import com.moon.mapper.annotation.MapperFor;
import com.moon.mapper.annotation.MapperIgnoreFields;
import com.moon.mapper.annotation.Mapping;
import com.moon.processor.manager.NameManager;
import com.moon.processor.model.DeclaredPojo;
import com.moon.processor.model.DeclareGeneric;
import com.moon.processor.model.DeclareMapping;
import com.moon.processor.model.DeclareProperty;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
public enum Process2 {
    ;

    private final static String MAPPING_FOR_CLASSNAME = MapperFor.class.getCanonicalName();
    private final static String CLASS_SUFFIX = ".class";
    private final static String TOP_CLASS = Object.class.getName();

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
                    ignore = ignored.mode;
                    if (!Objects.equals(targetCls, ignored.targetCls)) {
                        targetCls = ignored.targetCls;
                        handler.accept(new DeclareMapping(targetCls, value, format, defaultValue, ignore));
                        ignoredWasUnused = false;
                        continue;
                    }
                }
                handler.accept(new DeclareMapping(targetCls, value, format, defaultValue, ignore));
            }
        }
        if (ignoredWasUnused && ignored != null) {
            handler.accept(new DeclareMapping(ignored.targetCls, "", "", "", ignored.mode));
        }
    }

    private static DeclareProperty declareProperty(
        DeclaredPojo declared, String name, TypeElement parsingElement, TypeElement thisElement
    ) {
        DeclareProperty detail = declared.get(name);
        if (detail == null) {
            detail = new DeclareProperty(name, parsingElement, thisElement);
            declared.put(name, detail);
        }
        return detail;
    }

    private static void handleEnclosedElem(
        Set<String> presentKeys,
        DeclaredPojo declared,
        Element element,
        Map<String, DeclareGeneric> genericMap,
        Map<String, IgnoredModel> ignoring,
        TypeElement parsingElement,
        TypeElement thisElement
    ) {
        if (Test2.isMemberField(element)) {
            String name = element.getSimpleName().toString();
            if (presentKeys.contains(name)) {
                return;
            }
            DeclareProperty prop = declareProperty(declared, name, parsingElement, thisElement);
            handleMapping(ignoring.get(name), element, prop::addFieldMapping);
            prop.setField((VariableElement) element, genericMap);
        } else if (Test2.isSetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            String name = Element2.toPropertyName(elem);
            if (presentKeys.contains(name)) {
                return;
            }
            DeclareProperty prop = declareProperty(declared, name, parsingElement, thisElement);
            handleMapping(ignoring.get(name), element, prop::addSetterMapping);
            prop.setSetter(elem, genericMap);
        } else if (Test2.isGetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            String name = Element2.toPropertyName(elem);
            if (presentKeys.contains(name)) {
                return;
            }
            DeclareProperty prop = declareProperty(declared, name, parsingElement, thisElement);
            handleMapping(ignoring.get(name), element, prop::addGetterMapping);
            prop.setGetter(elem, genericMap);
        } else if (Test2.isConstructor(element)) {
            // definition.addConstructor((ExecutableElement) element);
        }
    }

    private static void parseRootPropertiesMap(
        DeclaredPojo declared, Map<String, DeclareGeneric> thisGenericMap, Map<String, IgnoredModel> ignoring
    ) {
        TypeElement rootElement = declared.getDeclareElement();
        List<? extends Element> elements = rootElement.getEnclosedElements();
        Set<String> presents = new HashSet<>();
        for (Element element : Collect2.emptyIfNull(elements)) {
            handleEnclosedElem(presents, declared, element,//
                thisGenericMap, ignoring, rootElement, rootElement);
        }
    }

    private static void parseSuperPropertiesMap(
        Map<String, DeclareGeneric> thisGenericMap,
        Map<String, IgnoredModel> ignoring,
        Set<String> presentKeys,
        DeclaredPojo declared,
        TypeElement thisElement,
        TypeElement rootElement
    ) {
        TypeMirror superclass = thisElement.getSuperclass();
        Element superElem = Environment2.getTypes().asElement(superclass);
        if (superElem == null || superclass.toString().equals(TOP_CLASS)) {
            return;
        }
        TypeElement superElement = Element2.cast(superElem);
        List<? extends Element> elements = superElement.getEnclosedElements();
        for (Element element : elements) {
            handleEnclosedElem(presentKeys, declared, element, thisGenericMap, ignoring, superElement, rootElement);
        }
        presentKeys = new HashSet<>(declared.keySet());
        parseSuperPropertiesMap(thisGenericMap, ignoring, presentKeys, declared, superElement, rootElement);
    }

    public static DeclaredPojo toPropertiesMap(TypeElement rootElement, NameManager nameManager) {
        Map<String, IgnoredModel> ignoringMap = parseMappingIgnoring(rootElement);
        Map<String, DeclareGeneric> thisGenericMap = Generic2.from(rootElement);
        DeclaredPojo declared = new DeclaredPojo(rootElement, nameManager);
        parseRootPropertiesMap(declared, thisGenericMap, ignoringMap);
        parseSuperPropertiesMap(thisGenericMap, ignoringMap, new HashSet<>(), declared, rootElement, rootElement);
        declared.onCompleted();
        return declared;
    }

    private final static class IgnoredModel {

        private final IgnoreMode mode;
        private final String targetCls;

        private IgnoredModel(String targetCls, IgnoreMode mode) {
            this.targetCls = targetCls;
            this.mode = mode;
        }
    }

    private static <T> String getTargetCls(T t, Function<T, Class<?>> classGetter) {
        String targetCls = Element2.getClassname(t, classGetter);
        return Test2.isBasicType(targetCls) ? "void" : targetCls;
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
                    ignoringMap.put(name, new IgnoredModel(targetCls, mode));
                }
            }
        }
        return ignoringMap;
    }

    public static Collection<TypeElement> getMapperForClasses(TypeElement element) {
        Types types = Environment2.getTypes();
        Elements elements = Environment2.getUtils();
        TypeMirror supportedType = elements.getTypeElement(MAPPING_FOR_CLASSNAME).asType();
        Collection<String> classes = new HashSet<>();
        for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
            DeclaredType declaredType = mirror.getAnnotationType();
            if (!types.isSameType(supportedType, declaredType)) {
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
        return classes.stream().map(elements::getTypeElement).collect(Collectors.toList());
    }
}
