package com.moon.processor.manager;

import com.moon.accessor.annotation.TablePolicy;
import com.moon.processor.model.DefaultTablePolicy;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Environment2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class PolicyManager {

    private final Map<String, TablePolicy> policyMap = new HashMap<>();

    public PolicyManager() {
    }

    public TablePolicy with(TypeElement element) {
        String classname = Element2.getQualifiedName(element);
        TablePolicy tablePolicy = policyMap.get(classname);
        if (tablePolicy != null) {
            return tablePolicy;
        }
        Types types = Environment2.getTypes();
        do {
            tablePolicy = element.getAnnotation(TablePolicy.class);
            if (tablePolicy != null) {
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
        tablePolicy = tablePolicy == null ? DefaultTablePolicy.INSTANCE : tablePolicy;
        policyMap.put(classname, tablePolicy);
        return tablePolicy;
    }
}
