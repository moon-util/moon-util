package com.moon.more.util.annotation;

import com.moon.more.util.annotation.validator.ShortInValidator;

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
@Constraint(validatedBy = ShortInValidator.class)
public @interface ShortIn {

    boolean nullable() default true;

    String values() default "";

    String message() default "必须是数值 [{values}] 之一";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
