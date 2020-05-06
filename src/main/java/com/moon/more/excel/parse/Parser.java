package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.DataColumnFlatten;
import com.moon.more.excel.annotation.DataIndexer;
import com.moon.more.excel.annotation.DataRecord;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
abstract class Parser<T extends Defined> extends BaseParser {

    private final Creator creator;

    private Creator getCreator() { return creator; }

    protected Parser(Creator creator) { this.creator = creator; }

    protected ParsedDetail doParse(Class type) {
        try {
            Map<String, T> annotated = new LinkedHashMap<>();
            Map<String, T> unAnnotated = new LinkedHashMap<>();
            parseDescriptors(type, annotated, unAnnotated);
            parseFields(type, annotated, unAnnotated);
            return toParsedResult(getCreator(), type, annotated, unAnnotated);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @SuppressWarnings("all")
    private void parseDescriptors(
        Class type, Map annotated, Map unAnnotated
    ) throws Exception {
        Creator creator = getCreator();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : descriptors) {
            Method method = creator.get(descriptor);
            if (method == null) {
                continue;
            }
            String name = descriptor.getName();
            Class propType = descriptor.getPropertyType();
            Type genericType = creator.getGenericType(method);
            ParsedDetail children = getGroupParsed(method, genericType, propType, name);
            Marked<Method> onMethod = Marked.of(name, propType, genericType, method, children);

            Defined info = creator.info(name, onMethod);
            if (onMethod.isAnnotated()) {
                annotated.put(name, info);
            } else {
                unAnnotated.put(name, info);
            }
        }
    }

    private void parseFields(Class type, Map annotated, Map unAnnotated) {
        while (type != null) {
            Field[] fields = type.getDeclaredFields();
            doParseFields(fields, annotated, unAnnotated);
            type = type.getSuperclass();
        }
    }

    @SuppressWarnings("all")
    private void doParseFields(Field[] fields, Map annotated, Map unAnnotated) {
        for (Field field : fields) {
            transform(field, annotated, unAnnotated);
        }
    }

    private void transform(
        Field field, Map<String, Defined> annotated, Map<String, Defined> unAnnotated
    ) {
        String name = field.getName();
        Marked<Field> onField = Marked.of(field, getGroupParsed(field));

        Defined info = annotated.get(name);
        if (info == null) {
            info = unAnnotated.remove(name);
            if (info == null) {
                info = getCreator().info(name);
            }
            info.setAboutField(onField);
            if (onField.isAnnotated()) {
                annotated.put(name, info);
            } else {
                unAnnotated.put(name, info);
            }
        } else {
            info.setAboutField(onField);
        }
    }

    ParsedDetail getGroupParsed(Field field) {
        return getGroupParsed(field, field.getGenericType(), field.getType(), field.getName());
    }

    ParsedDetail getGroupParsed(
        AnnotatedElement elem, Type paramType, Class actualType, String propName
    ) {
        DataColumnFlatten flatten = obtainFlatten(elem);
        if (flatten != null) {
            Class actualTpe = flatten.targetClass();
            if (actualTpe == Void.class) {
                actualTpe = getActual(paramType, actualType);
            }
            if (actualTpe == null) {
                throw new IllegalStateException("[ " + propName + " ]未知集合目标类型");
            }
            if (isBasic(actualType)) {
                // return strategy.create(propName, true);
            }
            return doParse(actualTpe);
        }
        return null;
    }

    private static <T extends Defined> ParsedDetail toParsedResult(
        Creator creator, Class type, Map<String, T> annotated, Map<String, T> unAnnotated
    ) {
        List columns;
        Defined ending = null;
        Defined starting = null;

        boolean hasAnnotated = !annotated.isEmpty();
        if (hasAnnotated) {
            Collection<T> collection = annotated.values();
            columns = new ArrayList(collection.size());
            for (T info : collection) {
                if (info.isOnlyIndexer()) {
                    if (info.getIndexer().ending()) {
                        ending = info;
                    } else {
                        starting = info;
                    }
                } else if (info.isDefined()) {
                    columns.add(info);
                }
            }
        } else {
            columns = unAnnotated.values().stream()

                .filter(info -> isBasic(info.getPropertyType()))

                .collect(Collectors.toList());
            // throw new UnsupportedOperationException();
        }

        DataIndexer idx = obtainIndexer(type);
        if (idx != null) {
            Defined info = creator.info(type.getName(), idx);
            if (idx.ending()) {
                ending = info;
            } else {
                starting = info;
            }
        }

        RootDetail root = RootDetail.of(obtain(type, DataRecord.class));
        return creator.parsed(columns, root, starting, ending);
    }
}
