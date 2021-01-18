package com.moon.accessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface TableFieldPolicy {

    /**
     * 参考: {@link TableModelPolicy#pattern()}文档
     *
     * @return 表名格式
     */
    String pattern() default "{}";

    /**
     * 参考: {@link TableModelPolicy#casePolicy()}文档
     *
     * @return 命名策略
     */
    CasePolicy casePolicy() default CasePolicy.UNDERSCORE_LOWERCASE;
}
