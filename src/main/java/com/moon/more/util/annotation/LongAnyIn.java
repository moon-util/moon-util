package com.moon.more.util.annotation;

import com.moon.more.util.annotation.validator.LongAnyInValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = LongAnyInValidator.class)
public @interface LongAnyIn {

    boolean nullable() default true;

    String values() default "";

    String message() default MessageConst.AT_ANY;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
