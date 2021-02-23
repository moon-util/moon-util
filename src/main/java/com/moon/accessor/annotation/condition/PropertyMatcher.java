package com.moon.accessor.annotation.condition;

import java.lang.annotation.Annotation;

/**
 * @author benshaoye
 * @see IfMatching
 */
public interface PropertyMatcher<FieldType, AnnotationType extends Annotation> {

    /**
     * 检测值是否符合条件
     *
     * @param value      字段值
     * @param annotation 注解
     *
     * @return 是否符合条件
     */
    boolean test(FieldType value, AnnotationType annotation);
}
