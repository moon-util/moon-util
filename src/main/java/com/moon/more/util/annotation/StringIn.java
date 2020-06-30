package com.moon.more.util.annotation;

import com.moon.more.util.annotation.validator.StringInValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 要求字符串或者枚举值必须是指定列表中的值之一
 *
 * @author benshaoye
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = StringInValidator.class)
public @interface StringIn {

    boolean nullable() default true;

    String values() default "";

    String delimiter() default ",";

    String message() default "必须是枚举值 [{values}] 之一";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
