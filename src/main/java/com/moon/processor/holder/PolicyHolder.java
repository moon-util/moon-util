package com.moon.processor.holder;

import com.moon.accessor.annotation.TableFieldPolicy;
import com.moon.accessor.annotation.TableModel;
import com.moon.accessor.annotation.TableModelPolicy;
import com.moon.processor.defaults.DefaultTableFieldPolicy;
import com.moon.processor.defaults.DefaultTableModel;
import com.moon.processor.defaults.DefaultTableModelPolicy;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Environment2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class PolicyHolder {

    private final Map<String, TableModelPolicy> policyMap = new HashMap<>();
    private final Map<String, TableFieldPolicy> columnPolicyMap = new HashMap<>();

    public PolicyHolder() { }

    private static <A extends Annotation, D extends A, M extends Map<String, A>> A find(
        TypeElement element, M map, Class<A> annotationClass, D defaultVal
    ) {
        String classname = Element2.getQualifiedName(element);
        A policy = map.get(classname);
        if (policy != null) {
            return policy;
        }
        Types types = Environment2.getTypes();
        do {
            policy = element.getAnnotation(annotationClass);
            if (policy != null) {
                break;
            }
            Element elem = types.asElement(element.getSuperclass());
            if (elem == null) {
                break;
            }
            element = (TypeElement) elem;
            if (Test2.isTypeof(Element2.getQualifiedName(element), Object.class)) {
                break;
            }
        } while (true);

        policy = policy == null ? defaultVal : policy;
        map.put(classname, policy);
        return policy;
    }

    public TableModel withTableModel(TypeElement element) {
        TableModel model = element.getAnnotation(TableModel.class);
        return model == null ? DefaultTableModel.INSTANCE : model;
    }

    public TableFieldPolicy withColumnPolicy(TypeElement element) {
        return find(element, columnPolicyMap, TableFieldPolicy.class, DefaultTableFieldPolicy.INSTANCE);
    }

    public TableModelPolicy with(TypeElement element) {
        return find(element, policyMap, TableModelPolicy.class, DefaultTableModelPolicy.INSTANCE);
    }
}
