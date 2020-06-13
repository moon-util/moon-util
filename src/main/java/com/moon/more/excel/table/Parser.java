package com.moon.more.excel.table;

import com.moon.more.excel.Renderer;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnTransformer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public class Parser<T extends Marked> {

    private final Creator creator;

    Parser(Creator creator) { this.creator = creator; }

    protected Creator getCreator() { return creator; }

    protected Renderer doParseConfiguration(Class type) {
        try {
            Map<String, T> annotatedAtM = new LinkedHashMap<>();
            Map<String, T> unAnnotatedAtM = new LinkedHashMap<>();
            Map<String, T> annotatedAtF = new LinkedHashMap<>();
            Map<String, T> unAnnotatedAtF = new LinkedHashMap<>();
            parseDescriptors(type, annotatedAtM, unAnnotatedAtM);
            parseFields(type, annotatedAtF, unAnnotatedAtF);

            Map<String, Attribute> annotated = mergeAttr(annotatedAtM, annotatedAtF);
            Map<String, Attribute> unAnnotated = mergeAttr(unAnnotatedAtM, unAnnotatedAtF);

            return toRendererResult(annotated, unAnnotated);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static TableRenderer toRendererResult(
        Map<String, Attribute> annotated, Map<String, Attribute> unAnnotated
    ) { return annotated.isEmpty() ? toResultByUnAnnotated(unAnnotated) : toResultByAnnotated(annotated); }

    private static TableRenderer toResultByAnnotated(Map<String, Attribute> annotated) {
        return mapAttrs(annotated, attr -> {
            TableColumn column = attr.getTableColumn();
            String defaultVal = column.defaultValue();
            Class transformer = column.transformBy();

            if (transformer != TableColumnTransformer.class) {
                return transformerToTableCol(attr);
            } else if (!"".equals(defaultVal)) {
                return new TableColDefaultVal(attr);
            }

            return new TableCol(attr);
        });
    }

    private static TableCol transformerToTableCol(Attribute attr) {
        TableColumn column = attr.getTableColumn();
        Class transformer = column.transformBy();

        checkValidImplClass(transformer);

        if (isExpectCached(transformer)) {
            return new TableColTransformEvery(attr, transformer);
        } else {
            return new TableColTransformCached(attr, transformer);
        }
    }

    private static boolean isExpectCached(Class type) {
        return type.isMemberClass() && !Modifier.isStatic(type.getModifiers());
    }

    private static void checkValidImplClass(Class type) {
        int modifiers = type.getModifiers();
        if (Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
            throw new IllegalStateException("指定类「" + type + "」不能使接口或抽象类");
        }
        if (!TableColumnTransformer.class.isAssignableFrom(type)) {
            throw new IllegalStateException("指定类「" + type + "」应该是「" + TableColumnTransformer.class + "」的实现类");
        }
    }

    private static TableRenderer toResultByUnAnnotated(Map<String, Attribute> unAnnotated) {
        return mapAttrs(unAnnotated, TableCol::new);
    }

    private static TableRenderer mapAttrs(
        Map<String, Attribute> attributeMap, Function<Attribute, TableCol> transformer
    ) {
        Set<Map.Entry<String, Attribute>> attrEntrySet = attributeMap.entrySet();
        TableCol[] columns = new TableCol[attrEntrySet.size()];

        int index = 0;
        for (Map.Entry<String, Attribute> attrEntry : attrEntrySet) {
            columns[index++] = transformer.apply(attrEntry.getValue());
        }
        return new TableRenderer(columns);
    }

    private void parseDescriptors(Class type, Map annotated, Map unAnnotated) throws IntrospectionException {
        Creator creator = getCreator();
        BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor descriptor : descriptors) {
            Method method = creator.getMethod(descriptor);
            if (method == null) {
                continue;
            }

            MarkMethod marked = toMarked(method, descriptor.getName(), descriptor.getPropertyType());
            putMarked(marked, annotated, unAnnotated);
        }
    }

    private static MarkMethod toMarked(Method method, String name, Class type) {
        return new MarkMethod(name, type, method);
    }

    private static MarkField toMarked(Field field) { return new MarkField(field); }

    private static void parseFields(Class type, Map annotated, Map unAnnotated) {
        while (type != null && type != Object.class) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                putMarked(toMarked(field), annotated, unAnnotated);
            }
            type = type.getSuperclass();
        }
    }

    private static <T extends Marked> Map<String, Attribute> mergeAttr(
        Map<String, T> atMethod, Map<String, T> atField
    ) {
        Map<String, Attribute> annotatedMap = new LinkedHashMap<>();
        for (Map.Entry<String, T> entry : atField.entrySet()) {
            String name = entry.getKey();
            Marked field = entry.getValue();
            Marked method = atMethod.remove(name);
            Attribute attr = new Attribute(method, field);
            annotatedMap.put(name, attr);
        }
        for (Map.Entry<String, T> entry : atMethod.entrySet()) {
            annotatedMap.put(entry.getKey(), new Attribute(entry.getValue(), null));
        }
        return annotatedMap;
    }

    private static void putMarked(Marked marked, Map annotated, Map unAnnotated) {
        Map group = marked.isAnnotatedCol() ? annotated : unAnnotated;
        group.put(marked.getName(), marked);
    }
}
