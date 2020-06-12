package com.moon.more.excel.table;

import com.moon.more.excel.Renderer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

    private TableRenderer toRendererResult(Map<String, Attribute> annotated, Map<String, Attribute> unAnnotated) {
        if (annotated.isEmpty()) {
            return toResultByUnAnnotated(unAnnotated);
        } else {
            return toResultByAnnotated(annotated);
        }
    }

    private TableRenderer toResultByAnnotated(Map<String, Attribute> annotated) {
        throw new UnsupportedOperationException();
    }

    private TableRenderer toResultByUnAnnotated(Map<String, Attribute> unAnnotated) {
        Set<Map.Entry<String, Attribute>> attrEntrySet = unAnnotated.entrySet();
        for (Map.Entry<String, Attribute> attrEntry : attrEntrySet) {
            Attribute attr = attrEntry.getValue();
            String name = attrEntry.getKey();

        }
        return null;
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

    private MarkMethod toMarked(Method method, String name, Class type) {
        return new MarkMethod(name, type, method, getCreator());
    }

    private MarkField toMarked(Field field) {
        return new MarkField(field, getCreator());
    }

    private void parseFields(Class type, Map annotated, Map unAnnotated) {
        while (type != null && type != Object.class) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                putMarked(toMarked(field), annotated, unAnnotated);
            }
            type = type.getSuperclass();
        }
    }

    private Map<String, Attribute> mergeAttr(Map<String, T> annotatedAtM, Map<String, T> annotatedAtF) {
        Map<String, Attribute> annotatedMap = new LinkedHashMap<>();
        for (Map.Entry<String, T> entry : annotatedAtM.entrySet()) {
            String name = entry.getKey();
            Marked method = entry.getValue();
            Marked field = annotatedAtF.get(name);
            Attribute attr = new Attribute(method, field);
            annotatedMap.put(name, attr);
        }
        return annotatedMap;
    }

    private void putMarked(Marked marked, Map annotated, Map unAnnotated) {
        Map group = marked.isAnnotatedCol() ? annotated : unAnnotated;
        group.put(marked.getName(), marked);
    }
}
