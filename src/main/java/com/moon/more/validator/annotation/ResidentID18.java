package com.moon.more.validator.annotation;

import com.moon.more.validator.impl.ResidentID18Validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 中国 18 为居民身份证号验证
 *
 * @author moonsky
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ResidentID18Validation.class)
public @interface ResidentID18 {

    String message() default "18 位身份证号码错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
