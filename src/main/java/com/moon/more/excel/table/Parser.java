package com.moon.more.excel.table;

import com.moon.more.excel.annotation.DefaultValue;
import com.moon.more.excel.annotation.FieldTransform;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class Parser<T extends Marked> {

    private final Creator creator;

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

            return toRendererResult(annotated, unAnnotated);
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private TableRenderer toRendererResult(
        Map<String, Attribute> annotated, Map<String, Attribute> unAnnotated
    ) { return annotated.isEmpty() ? toResultByUnAnnotated(unAnnotated) : toResultByAnnotated(annotated); }

    private TableRenderer toResultByAnnotated(Map<String, Attribute> annotated) {
        return ParserUtil.mapAttrs(annotated, config -> {
            Attribute attr = config.getAttribute();
            DefaultValue defaulter = attr.getAnnotation(DefaultValue.class);
            FieldTransform transformer = attr.getAnnotation(FieldTransform.class);
            if (transformer != null) {
                Assert.notAllowedColumnGroup(attr.isAnnotatedGroup(), attr.getName());
                return transformerToTableCol(config, transformer);
            } else if (defaulter != null) {
                Assert.notAllowedColumnGroup(attr.isAnnotatedGroup(), attr.getName());
                return new TableColDefaultVal(config, defaulter);
            }

            if (attr.isAnnotatedGroup()) {
                Class targetClass = attr.getPropertyType();
                TableRenderer renderer = doParseConfiguration(targetClass);
                return new TableColGroup(config, renderer);
            }
            return new TableCol(config);
        });
    }

    private static TableCol transformerToTableCol(AttrConfig config, FieldTransform transformer) {
        Class transformerCls = transformer.value();

        ParserUtil.checkValidImplClass(transformerCls);

        if (isExpectCached(transformerCls)) {
            return new TableColTransformEvery(config, transformerCls);
        } else {
            return new TableColTransformCached(config, transformerCls);
        }
    }

    private static boolean isExpectCached(Class type) {
        return type.isMemberClass() && !Modifier.isStatic(type.getModifiers());
    }

    private static TableRenderer toResultByUnAnnotated(Map<String, Attribute> unAnnotated) {
        return ParserUtil.mapAttrs(unAnnotated, TableCol::new);
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
