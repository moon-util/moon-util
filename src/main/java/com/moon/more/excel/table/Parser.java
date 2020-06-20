package com.moon.more.excel.table;

import com.moon.more.excel.annotation.defaults.DefaultNumber;
import com.moon.more.excel.annotation.defaults.DefaultValue;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author benshaoye
 */
final class Parser<T extends Marked> {

    private final Creator creator;

    private final static Map<Class, TableRenderer> parsed = new WeakHashMap<>();

    final static TableRenderer cache(Class type, TableRenderer renderer) {
        parsed.put(type, renderer);
        return renderer;
    }

    private final static synchronized TableRenderer getCached(Class type) { return parsed.get(type); }

    Parser(Creator creator) { this.creator = creator; }

    protected Creator getCreator() { return creator; }

    protected TableRenderer doParseConfiguration(Class type) {
        TableRenderer renderer = getCached(type);
        if (renderer != null) {
            return renderer;
        }

        try {
            Map<String, T> annotatedAtM = new LinkedHashMap<>();
            Map<String, T> unAnnotatedAtM = new LinkedHashMap<>();
            Map<String, T> annotatedAtF = new LinkedHashMap<>();
            Map<String, T> unAnnotatedAtF = new LinkedHashMap<>();
            parseDescriptors(type, annotatedAtM, unAnnotatedAtM);
            parseFields(type, annotatedAtF, unAnnotatedAtF);

            Map<String, Attribute> annotated = ParserUtil.merge2Attr(annotatedAtM, annotatedAtF);
            Map<String, Attribute> unAnnotated = ParserUtil.merge2Attr(unAnnotatedAtM, unAnnotatedAtF);

            return toRendererResultAndCache(type, annotated, unAnnotated);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private TableRenderer toRendererResultAndCache(
        Class type, Map<String, Attribute> annotated, Map<String, Attribute> unAnnotated
    ) {
        TableRenderer renderer;
        if (annotated.isEmpty()) {
            renderer = toResultByUnAnnotated(type, unAnnotated);
        } else {
            renderer = toResultByAnnotated(type, annotated);
        }
        return cache(type, renderer);
    }

    private TableRenderer toResultByAnnotated(Class type, Map<String, Attribute> annotated) {
        return ParserUtil.mapAttrs(type, annotated, config -> {
            Attribute attr = config.getAttribute();
            Class targetClass = attr.getPropertyType();

            if (attr.isAnnotatedGroup()) {
                TableRenderer renderer = doParseConfiguration(targetClass);
                return new TableColGroup(config, renderer);
            }

            DefaultNumber atNumber = attr.getAnnotation(DefaultNumber.class);
            if (atNumber != null && Assert.isNumberType(targetClass)) {
                return TableDft.of(config, atNumber);
            }

            DefaultValue defaultVal = attr.getAnnotation(DefaultValue.class);
            if (defaultVal != null) {
                return TableDft.of(config, defaultVal);
            }

            return new TableCol(config);
        });
    }

    private static TableRenderer toResultByUnAnnotated(Class type, Map<String, Attribute> unAnnotated) {
        return ParserUtil.mapAttrs(type, unAnnotated, TableCol::new);
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

            Marked marked = Marked.of(descriptor, method);
            ParserUtil.putMarked(marked, annotated, unAnnotated);
        }
    }

    private static void parseFields(Class type, Map annotated, Map unAnnotated) {
        while (type != null && type != Object.class) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                ParserUtil.putMarked(Marked.of(field), annotated, unAnnotated);
            }
            type = type.getSuperclass();
        }
    }
}
