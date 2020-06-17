package com.moon.more.excel.table;

import com.moon.more.excel.annotation.DefaultNumber;
import com.moon.more.excel.annotation.DefaultValue;
import com.moon.more.excel.annotation.DefinitionStyle;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author benshaoye
 */
public class Parser<T extends Marked> {

    private final Creator creator;

    private final static Set<Class> NUMBER_CLASSES = new HashSet<>();

    static {
        NUMBER_CLASSES.add(byte.class);
        NUMBER_CLASSES.add(short.class);
        NUMBER_CLASSES.add(int.class);
        NUMBER_CLASSES.add(long.class);
        NUMBER_CLASSES.add(float.class);
        NUMBER_CLASSES.add(double.class);
    }

    private final static boolean isNumberType(Class type) {
        return Number.class.isAssignableFrom(type) || NUMBER_CLASSES.contains(type);
    }

    Parser(Creator creator) { this.creator = creator; }

    protected Creator getCreator() { return creator; }

    protected TableRenderer doParseConfiguration(Class type) {
        try {
            Map<String, T> annotatedAtM = new LinkedHashMap<>();
            Map<String, T> unAnnotatedAtM = new LinkedHashMap<>();
            Map<String, T> annotatedAtF = new LinkedHashMap<>();
            Map<String, T> unAnnotatedAtF = new LinkedHashMap<>();
            parseDescriptors(type, annotatedAtM, unAnnotatedAtM);
            parseFields(type, annotatedAtF, unAnnotatedAtF);

            Map<String, Attribute> annotated = ParserUtil.merge2Attr(annotatedAtM, annotatedAtF);
            Map<String, Attribute> unAnnotated = ParserUtil.merge2Attr(unAnnotatedAtM, unAnnotatedAtF);

            return toRendererResult(type, annotated, unAnnotated);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private TableRenderer toRendererResult(
        Class type, Map<String, Attribute> annotated, Map<String, Attribute> unAnnotated
    ) { return annotated.isEmpty() ? toResultByUnAnnotated(type, unAnnotated) : toResultByAnnotated(type, annotated); }

    private TableRenderer toResultByAnnotated(Class type, Map<String, Attribute> annotated) {
        return ParserUtil.mapAttrs(type, annotated, config -> {
            StyleUtil.parsePropertyStyle(config);
            Attribute attr = config.getAttribute();
            System.out.println(attr.getName());
            System.out.println("List: " + attr.getAnnotation(DefinitionStyle.List.class));
            System.out.println("CSS : " + attr.getAnnotation(DefinitionStyle.class));
            Class targetClass = attr.getPropertyType();

            if (attr.isAnnotatedGroup()) {
                TableRenderer renderer = doParseConfiguration(targetClass);
                return new TableColGroup(config, renderer);
            }

            DefaultNumber atNumber = attr.getAnnotation(DefaultNumber.class);
            if (atNumber != null && isNumberType(targetClass)) {
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

            MarkMethod marked = toMarked(method, descriptor.getName(), descriptor.getPropertyType());
            ParserUtil.putMarked(marked, annotated, unAnnotated);
        }
    }

    private static void parseFields(Class type, Map annotated, Map unAnnotated) {
        while (type != null && type != Object.class) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                ParserUtil.putMarked(toMarked(field), annotated, unAnnotated);
            }
            type = type.getSuperclass();
        }
    }

    private static MarkMethod toMarked(Method method, String name, Class type) {
        return new MarkMethod(name, type, method);
    }

    private static MarkField toMarked(Field field) { return new MarkField(field); }
}
