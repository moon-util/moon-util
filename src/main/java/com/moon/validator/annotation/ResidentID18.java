package com.moon.validator.annotation;

import com.moon.validator.impl.ResidentID18Validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 验证中国 18 为居民身份证号
 * <p>
 * 不验证值为 null 的情况，如果要求非 null 使用{@link NotNull}
 *
 * @author moonsky
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ResidentID18Validation.class)
public @interface ResidentID18 {

    String message() default "18 位居民身份证号码错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
