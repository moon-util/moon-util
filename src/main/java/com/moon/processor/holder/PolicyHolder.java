package com.moon.processor.holder;

import com.moon.accessor.annotation.TableColumnPolicy;
import com.moon.accessor.annotation.TablePolicy;
import com.moon.processor.model.DefaultTableColumnPolicy;
import com.moon.processor.model.DefaultTablePolicy;
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

    private final Map<String, TablePolicy> policyMap = new HashMap<>();
    private final Map<String, TableColumnPolicy> columnPolicyMap = new HashMap<>();

    public PolicyHolder() {
    }

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

    public TableColumnPolicy withColumnPolicy(TypeElement element) {
        return find(element, columnPolicyMap, TableColumnPolicy.class, DefaultTableColumnPolicy.INSTANCE);
    }

    public TablePolicy with(TypeElement element) {
        return find(element, policyMap, TablePolicy.class, DefaultTablePolicy.INSTANCE);
    }
}
