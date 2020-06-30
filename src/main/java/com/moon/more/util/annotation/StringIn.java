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
 * 通过字段值的{@link Object#toString()}后的字符串在指定区间匹配
 *
 * @author benshaoye
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = StringInValidator.class)
public @interface StringIn {

    /**
     * 字段是否允许为 null 值
     *
     * @return 等于 false 时，字段不能为 null
     */
    boolean nullable() default true;

    /**
     * 字段值可选项枚举
     *
     * @return 字段所有允许的值列举在这里，用逗号隔开
     */
    String values() default "";

    /**
     * 不同字段枚举值的默认分隔符是英文逗号，
     * 不同于数字的验证，这里可以自定义分隔符
     *
     * @return {@link #values()}中枚举值的分隔符
     */
    String delimiter() default ",";

    /**
     * 错误消息提示模板
     *
     * @return {@link MessageConst#AT_ALL}
     */
    String message() default "必须是枚举值 [{values}] 之一";

    /**
     * 分组验证
     *
     * @return 分组对不同场景下使用不同的验证规则
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
