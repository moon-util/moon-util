package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;
import com.moon.more.excel.annotation.TableRecord;

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
abstract class Parser<T extends Property> extends AbstractSupporter {

    private final Creator creator;

    private Creator getCreator() { return creator; }

    protected Parser(Creator creator) { this.creator = creator; }

    protected PropertiesGroup doParse(Class type) {
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

    protected MarkedColumnGroup doParseAsCol(Class type) {
        return transform(doParse(type), 0);
    }

    private MarkedColumnGroup transform(PropertiesGroup group, final int offset) {
        if (group == null) {
            return null;
        }
        int index = 0;
        List<MarkedColumn> columns = new ArrayList<>();
        for (Object column : group.getColumns()) {
            Property prop = (Property) column;
            int columnOffset = (index++) + offset;
            columns.add(new MarkedColumn(columnOffset, prop.getName(), prop.getPropertyType(),

                prop.getActualType(), prop.getColumn(), prop.getFlatten(), prop.getListable(),

                prop.getIndexer(), transform(prop.getGroup(), index - 1)));
        }
        return new MarkedColumnGroup(columns);
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
            PropertiesGroup children = getGroupParsed(method, genericType, propType, name);
            Annotated<Method> onMethod = Annotated.of(name, propType, genericType, method, children);

            Property info = creator.info(name, onMethod);
            if (onMethod.isAnnotated()) {
                annotated.put(name, info);
            } else {
                unAnnotated.put(name, info);
            }
        }
    }

    private void parseFields(Class type, Map annotated, Map unAnnotated) {
        while (type != null && type != Object.class) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                transform(field, annotated, unAnnotated);
            }
            type = type.getSuperclass();
        }
    }

    private void transform(
        Field field, Map<String, Property> annotated, Map<String, Property> unAnnotated
    ) {
        String name = field.getName();
        Annotated<Field> onField = Annotated.of(field, getGroupParsed(field));

        Property info = annotated.get(name);
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

    PropertiesGroup getGroupParsed(Field field) {
        return getGroupParsed(field, field.getGenericType(), field.getType(), field.getName());
    }

    PropertiesGroup getGroupParsed(
        AnnotatedElement elem, Type paramType, Class actualType, String propName
    ) {
        TableColumnFlatten flatten = obtainFlatten(elem);
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

    private static <T extends Property> PropertiesGroup toParsedResult(
        Creator creator, Class type, Map<String, T> annotated, Map<String, T> unAnnotated
    ) {
        List columns;
        Property ending = null;
        Property starting = null;

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

        TableIndexer idx = obtainIndexer(type);
        if (idx != null) {
            Property info = creator.info(type.getName(), idx);
            if (idx.ending()) {
                ending = info;
            } else {
                starting = info;
            }
        }

        SupportUtil.requireNotDuplicatedListable(columns);
        DetailRoot root = DetailRoot.of(obtain(type, TableRecord.class));
        return creator.parsed(columns, root, starting, ending);
    }
}
