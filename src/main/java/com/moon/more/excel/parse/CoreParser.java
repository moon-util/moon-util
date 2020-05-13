package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.core.util.Assert;
import com.moon.core.util.FilterUtil;
import com.moon.more.excel.Renderer;
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
abstract class CoreParser<T extends Property> extends AbstractSupporter {

    @SuppressWarnings({"rawtypes"})
    private final Creator creator;

    @SuppressWarnings({"rawtypes"})
    private Creator getCreator() { return creator; }

    @SuppressWarnings({"rawtypes"})
    protected CoreParser(Creator creator) { this.creator = creator; }

    @SuppressWarnings({"rawtypes"})
    protected PropertiesGroup doParse(Class type) {
        try {
            Map<String, T> annotated = new LinkedHashMap<>();
            Map<String, T> unAnnotated = new LinkedHashMap<>();
            parseDescriptors(type, annotated, unAnnotated);
            parseFields(type, annotated, unAnnotated);
            return toParsedResult(getCreator(), type, annotated, unAnnotated);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }


    @SuppressWarnings({"rawtypes"})
    protected Renderer doParseAsCol(Class type) {
        Renderer renderer = transform(doParse(type), IntAccessor.of());
        return renderer;
    }


    @SuppressWarnings({"rawtypes"})
    private Renderer transform(PropertiesGroup group, final IntAccessor accessor) {
        if (group.isIterated()) {
            return transformIterated(group, accessor);
        } else {
            return transformDefault(group, accessor);
        }
    }

    @SuppressWarnings("all")
    private MarkCollectGroup transformCollect(PropertiesGroup group, final IntAccessor accessor) {
        if (group == null) {
            return null;
        }
        boolean indexed = false;
        MarkColumn rootIndexer = null;
        if (group.rootProperty != null) {
            rootIndexer = MarkColumn.of(accessor.get(), group.rootProperty, null);
            accessor.increment();
            indexed = true;
        }
        MarkCollect collectAt = null;
        MarkColumn current;
        List<MarkColumn> columns = new ArrayList<>();
        for (Object column : group.getColumns()) {
            Property prop = (Property) column;
            final int offset = accessor.get();
            if (prop.hasIndexer()) {
                accessor.increment();
                indexed = true;
            }
            final int referenceOffset = accessor.get();
            current = MarkCollect.of(offset, prop, transformCollect(prop.getGroup(), accessor));
            if (prop.isIterated()) {
                collectAt = (MarkCollect) current;
            } else {
                columns.add(current);
            }
            if (accessor.isEq(referenceOffset)) {
                accessor.increment();
            }
        }
        return new MarkCollectGroup(columns, collectAt, rootIndexer, group.root, indexed);
    }

    @SuppressWarnings({"rawtypes"})
    private MarkIteratedGroup transformIterated(PropertiesGroup group, final IntAccessor accessor) {
        if (group == null) {
            return null;
        }
        boolean indexed = false;
        MarkColumn rootIndexer = null;
        if (group.rootProperty != null) {
            rootIndexer = MarkColumn.of(accessor.get(), group.rootProperty, null);
            accessor.increment();
            indexed = true;
        }
        MarkIterated iteratedAt = null, current;
        List<MarkIterated> columns = new ArrayList<>();
        for (Object column : group.getColumns()) {
            Property prop = (Property) column;
            final int offset = accessor.get();
            if (prop.hasIndexer()) {
                accessor.increment();
                indexed = true;
            }
            final int referenceOffset = accessor.get();
            current = MarkIterated.of(offset, prop, transformIterated(prop.getGroup(), accessor));
            if (prop.isIterated()) {
                iteratedAt = current;
            } else {
                columns.add(current);
            }
            if (accessor.isEq(referenceOffset)) {
                accessor.increment();
            }
        }
        return new MarkIteratedGroup(columns, iteratedAt, rootIndexer, group.root, indexed);
    }

    @SuppressWarnings({"rawtypes"})
    private MarkColumnGroup transformDefault(PropertiesGroup group, final IntAccessor accessor) {
        if (group == null) {
            return null;
        }
        boolean indexed = false;
        MarkColumn rootIndexer = null;
        if (group.rootProperty != null) {
            rootIndexer = MarkColumn.of(accessor.get(), group.rootProperty, null);
            accessor.increment();
            indexed = true;
        }
        List<MarkColumn> columns = new ArrayList<>();
        for (Object column : group.getColumns()) {
            Property prop = (Property) column;
            final int offset = accessor.get();
            if (prop.hasIndexer()) {
                accessor.increment();
                indexed = true;
            }
            final int referenceOffset = accessor.get();
            columns.add(MarkColumn.of(offset, prop, transformDefault(prop.getGroup(), accessor)));
            if (accessor.isEq(referenceOffset)) {
                accessor.increment();
            }
        }
        return new MarkColumnGroup(columns, rootIndexer, group.root, indexed);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
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
            Class actualClass = getActualClass(method, genericType, propType, name);
            PropertiesGroup children = getGroupParsed(method, actualClass);
            Annotated<Method> onMethod = Annotated.of(name, propType, genericType, actualClass, method, children);

            (onMethod.isAnnotated() ? annotated : unAnnotated).put(name, creator.info(name, onMethod));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
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
        @SuppressWarnings({"rawtypes"}) Class actualClass = getActualClass(field);
        Annotated<Field> onField = Annotated.of(field, actualClass, getGroupParsed(field, actualClass));

        Property info = annotated.get(name);
        if (info == null) {
            info = unAnnotated.remove(name);
            if (info == null) {
                info = getCreator().info(name);
            }
            info.setAboutField(onField);
            (onField.isAnnotated() ? annotated : unAnnotated).put(name, info);
        } else {
            info.setAboutField(onField);
        }
    }

    @SuppressWarnings({"rawtypes"})
    static Class getActualClass(AnnotatedElement elem, Type genericType, Class propertyCls, String propName) {
        TableColumnFlatten flatten = obtainFlatten(elem);
        if (flatten != null) {
            Class actualTpe = flatten.targetClass();
            if (actualTpe == Void.class) {
                actualTpe = getActual(genericType, propertyCls);
            }
            if (actualTpe == null || isSetColumn(actualTpe)) {
                throw new IllegalStateException("未知集合目标(泛型)类型: [" + propName + "] " + elem);
            }
            return actualTpe;
        } else if (isSetColumn(propertyCls)) {
            Class actualTpe = getActual(genericType, propertyCls);
            return actualTpe == propertyCls ? null : actualTpe;
        }
        return null;
    }

    @SuppressWarnings({"rawtypes"})
    Class getActualClass(Field field) {
        return getActualClass(field, field.getGenericType(), field.getType(), field.getName());
    }

    @SuppressWarnings({"rawtypes"})
    PropertiesGroup getGroupParsed(AnnotatedElement elem, Class actualType) {
        TableColumnFlatten flatten = obtainFlatten(elem);
        return flatten == null ? null : doParse(actualType);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T extends Property> PropertiesGroup toParsedResult(
        Creator creator, Class type, Map<String, T> annotated, Map<String, T> unAnnotated
    ) {
        List columns;
        Property rootIndexer = null;

        boolean hasAnnotated = !annotated.isEmpty();
        if (hasAnnotated) {
            Collection<T> collection = annotated.values();
            columns = new ArrayList(collection.size());
            for (T info : collection) {
                if (info.isOnlyIndexer()) {
                    rootIndexer = info;
                } else if (info.isDefined()) {
                    columns.add(info);
                }
            }
        } else {
            columns = unAnnotated.values()
                .stream()
                .filter(info -> isBasic(info.getPropertyType()))
                .collect(Collectors.toList());
        }

        TableIndexer idx = obtainIndexer(type);
        if (idx != null) {
            rootIndexer = creator.info(type.getName(), idx);
        }

        SupportUtil.requireNotDuplicatedListable(columns);
        DetailRoot root = DetailRoot.of(obtain(type, TableRecord.class));
        return creator.parsed(columns, root, rootIndexer);
    }
}
