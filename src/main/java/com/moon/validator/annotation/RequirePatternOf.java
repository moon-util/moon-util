package com.moon.validator.annotation;

import com.moon.core.enums.Patterns;
import com.moon.validator.impl.RequirePatternValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = RequirePatternValidator.class)
public @interface RequirePatternOf {

    /**
     * 指定验证器
     *
     * @return
     */
    Patterns value();

    /**
     * 是否要求不匹配
     *
     * @return true | false
     */
    boolean not() default false;

    /**
     * 错误消息提示模板
     *
     * @return {@link Message#AT_ALL}
     */
    String message() default "数据格式不正确";

    /**
     * 分组验证
     *
     * @return 分组对不同场景下使用不同的验证规则
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
