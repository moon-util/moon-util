package com.moon.validator.annotation;

import com.moon.validator.impl.InLongsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只能使用在{@link Long}或{@code long}字段上
 *
 * @author moonsky
 * @see InInts
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = InLongsValidator.class)
public @interface InLongs {

    /**
     * 字段是否允许为 null 值
     *
     * @return 等于 false 且字段类型是{@link Long}时，字段不能为 null
     */
    boolean nullable() default true;

    /**
     * long 值列表，多个值用逗号隔开
     *
     * @return long 值，如："1, 2, 3, 4, 5"；间隔符之间的空格会自动忽略
     */
    String values() default "";

    /**
     * 错误消息提示模板
     *
     * @return {@link Message#AT_ALL}
     */
    String message() default "必须是数值 [{values}] 之一";

    /**
     * 分组验证
     *
     * @return 分组对不同场景下使用不同的验证规则
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
