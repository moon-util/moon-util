package com.moon.more.util.annotation;

import com.moon.more.util.annotation.validator.ShortAllInValidator;

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
@Constraint(validatedBy = ShortAllInValidator.class)
public @interface ShortAllIn {

    boolean nullable() default true;

    String values() default "";

    String message() default MessageConst.AT_ALL;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
