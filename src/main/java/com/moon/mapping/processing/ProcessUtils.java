package com.moon.mapping.processing;

import com.moon.mapping.annotation.IgnoreMode;
import com.moon.mapping.annotation.Mapping;
import com.moon.mapping.annotation.MapperFor;
import com.moon.mapping.annotation.MapperIgnoreFields;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author moonsky
 */
final class ProcessUtils {

    private final static String MAPPING_FOR_CLASSNAME = MapperFor.class.getCanonicalName();
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

    private static void handleMapProperty(
        IgnoredModel ignored, Element element, Consumer<PropertyAttr> handler
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
                        handler.accept(new PropertyAttr(targetCls, value, format, defaultValue, ignore));
                        ignoredWasUnused = false;
                        continue;
                    }
                }
                handler.accept(new PropertyAttr(targetCls, value, format, defaultValue, ignore));
            }
        }
        if (ignoredWasUnused && ignored != null) {
            handler.accept(new PropertyAttr(ignored.targetCls, "", "", "", ignored.mode));
        }
    }

    private static void handleEnclosedElem(
        Set<String> presentKeys,
        BasicDefinition definition,
        Element element,
        Map<String, GenericModel> genericMap,
        Map<String, IgnoredModel> ignoring,
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
            handleMapProperty(ignoring.get(name), element, attr -> definition.addFieldAttr(name, attr));
        } else if (DetectUtils.isSetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            String name = ElemUtils.toPropertyName(elem);
            if (presentKeys.contains(name)) {
                return;
            }
            BasicProperty prop = ensureDetail(definition, name, parsingElement, thisElement);
            handleMapProperty(ignoring.get(name), element, attr -> definition.addSetterAttr(name, attr));
            prop.setSetter(elem, genericMap);
        } else if (DetectUtils.isGetterMethod(element)) {
            ExecutableElement elem = (ExecutableElement) element;
            String name = ElemUtils.toPropertyName(elem);
            if (presentKeys.contains(name)) {
                return;
            }
            BasicProperty prop = ensureDetail(definition, name, parsingElement, thisElement);
            handleMapProperty(ignoring.get(name), element, attr -> definition.addGetterAttr(name, attr));
            prop.setGetter(elem, genericMap);
        } else if (DetectUtils.isConstructor(element)) {
            // definition.addConstructor((ExecutableElement) element);
        }
    }

    @SuppressWarnings("all")
    private static BasicDefinition parseRootPropertiesMap(
        final TypeElement rootElement,
        Map<String, GenericModel> thisGenericMap,
        Map<String, IgnoredModel> ignoring
    ) {
        BasicDefinition definition = new BasicDefinition(rootElement);
        List<? extends Element> elements = rootElement.getEnclosedElements();
        Set<String> presents = new HashSet<>();
        for (Element element : CollectUtils.emptyIfNull(elements)) {
            handleEnclosedElem(presents, definition, element,//
                thisGenericMap, ignoring, rootElement, rootElement);
        }
        return definition;
    }

    private static void parseSuperPropertiesMap(
        Map<String, GenericModel> thisGenericMap,
        Map<String, IgnoredModel> ignoring,
        Set<String> presentKeys,
        BasicDefinition definition,
        TypeElement thisElement,
        TypeElement rootElement
    ) {
        TypeMirror superclass = thisElement.getSuperclass();
        Element superElem = EnvUtils.getTypes().asElement(superclass);
        if (superElem == null || superclass.toString().equals(TOP_CLASS)) {
            return;
        }
        TypeElement superElement = ElemUtils.cast(superElem);
        List<? extends Element> elements = superElement.getEnclosedElements();
        for (Element element : elements) {
            handleEnclosedElem(presentKeys, definition, element, thisGenericMap, ignoring, superElement, rootElement);
        }
        presentKeys = new HashSet<>(definition.keySet());
        parseSuperPropertiesMap(thisGenericMap, ignoring, presentKeys, definition, superElement, rootElement);
    }

    private final static class IgnoredModel {

        private final IgnoreMode mode;
        private final String targetCls;

        private IgnoredModel(String targetCls, IgnoreMode mode) {
            this.targetCls = targetCls;
            this.mode = mode;
        }
    }

    /**
     * 字段名: 模式+目标类
     *
     * @param rootElement
     *
     * @return
     */
    static Map<String, IgnoredModel> parseMappingIgnoring(TypeElement rootElement) {
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

    private static <T> String getTargetCls(T t, Function<T, Class<?>> classGetter) {
        String cls = ElemUtils.getAnnotatedClass(t, classGetter);
        @SuppressWarnings("all")//
        boolean isLang = cls.split("\\.").length == 3//
            && (cls.startsWith("java.lang.") || cls.startsWith("java.util."));
        return (isLang || StringUtils.isPrimitive(cls)) ? "void" : cls;
    }

    static BasicDefinition toPropertiesMap(TypeElement rootElement, boolean converter) {
        DetectUtils.assertRootElement(rootElement, converter);
        Map<String, IgnoredModel> ignoringMap = parseMappingIgnoring(rootElement);
        Map<String, GenericModel> thisGenericMap = GenericUtils.parse(rootElement);
        BasicDefinition definition = parseRootPropertiesMap(rootElement, thisGenericMap, ignoringMap);
        parseSuperPropertiesMap(thisGenericMap, ignoringMap, new HashSet<>(), definition, rootElement, rootElement);
        ConverterUtils.parseConverters(thisGenericMap, rootElement, definition);
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
