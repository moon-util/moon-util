package com.moon.more.util.annotation;

import com.moon.more.util.annotation.validator.LongInValidator;

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
@Constraint(validatedBy = LongInValidator.class)
public @interface LongIn {

    /**
     * 字段是否允许为 null 值
     *
     * @return 等于 false 且字段类型是{@link Long}时，字段不能为 null
     */
    boolean nullable() default true;

    String values() default "";
    /**
     * 错误消息提示模板
     *
     * @return {@link MessageConst#AT_ALL}
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
