package com.moon.processing.decl;

import com.moon.accessor.annotation.condition.*;
import com.moon.processing.holder.BaseHolder;
import com.moon.processing.holder.Holders;
import com.moon.processing.util.Element2;

import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author benshaoye
 */
public abstract class AnnotatedDeclared extends BaseHolder {

    private final static Set<String> IF_CONDITION_TYPES = new HashSet<>();

    static {
        addIfType(IfEq.class);
        addIfType(IfGe.class);
        addIfType(IfGt.class);
        addIfType(IfLe.class);
        addIfType(IfLt.class);
        addIfType(IfNe.class);
        addIfType(IfNotBlank.class);
        addIfType(IfNotEmpty.class);
        addIfType(IfNotNull.class);
    }

    private static void addIfType(Class<?> annotationType) {
        IF_CONDITION_TYPES.add(annotationType.getCanonicalName());
    }

    protected final boolean isIfConditionType(String type) {
        return IF_CONDITION_TYPES.contains(type);
    }

    protected final boolean isIfConditionType(TypeElement element) {
        return IF_CONDITION_TYPES.contains(getQualifiedName(element));
    }

    protected AnnotatedDeclared(Holders holders) {
        super(holders);
    }
}
