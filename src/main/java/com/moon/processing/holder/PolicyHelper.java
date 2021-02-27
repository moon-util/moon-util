package com.moon.processing.holder;

import com.moon.accessor.annotation.domain.TableFieldPolicy;
import com.moon.accessor.annotation.domain.TableModel;
import com.moon.accessor.annotation.domain.TableModelPolicy;
import com.moon.accessor.annotation.domain.Tables;
import com.moon.processing.defaults.TableFieldPolicyEnum;
import com.moon.processing.defaults.TableModelEnum;
import com.moon.processing.defaults.TableModelPolicyEnum;
import com.moon.processing.defaults.TablesEnum;
import com.moon.processing.util.Processing2;
import com.moon.processing.util.Element2;
import com.moon.processing.util.Test2;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class PolicyHelper extends BaseHolder {

    private final Map<String, Tables> tablesMap = new HashMap<>();
    private final Map<String, TableModelPolicy> modelPolicyMap = new HashMap<>();
    private final Map<String, TableFieldPolicy> fieldPolicyMap = new HashMap<>();

    public PolicyHelper(Holders holders) { super(holders); }

    public TableModel withTableModel(TypeElement element) {
        TableModel model = element.getAnnotation(TableModel.class);
        return model == null ? TableModelEnum.INSTANCE : model;
    }

    public Tables withTables(TypeElement element) {
        return find(element, tablesMap, Tables.class, TablesEnum.INSTANCE);
    }

    public TableFieldPolicy withFieldPolicy(TypeElement element) {
        return find(element, fieldPolicyMap, TableFieldPolicy.class, TableFieldPolicyEnum.INSTANCE);
    }

    public TableModelPolicy withModelPolicy(TypeElement element) {
        return find(element, modelPolicyMap, TableModelPolicy.class, TableModelPolicyEnum.INSTANCE);
    }

    private static <A extends Annotation, D extends A, M extends Map<String, A>> A find(
        TypeElement element, M map, Class<A> annotationClass, D defaultVal
    ) {
        String classname = Element2.getQualifiedName(element);
        A policy = map.get(classname);
        if (policy != null) {
            return policy;
        }
        Types types = Processing2.getTypes();
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
}
