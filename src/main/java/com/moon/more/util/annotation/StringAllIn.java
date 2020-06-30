package com.moon.more.util.annotation;

import com.moon.more.util.annotation.validator.StringAllInValidator;

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
@Constraint(validatedBy = StringAllInValidator.class)
public @interface StringAllIn {

    boolean nullable() default true;

    String values() default "";

    String delimiter() default ",";

    String message() default "所有项必须是枚举值 [{values}] 之一";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
