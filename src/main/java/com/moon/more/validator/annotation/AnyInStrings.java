package com.moon.more.validator.annotation;

import com.moon.more.validator.impl.AnyInStringsValidator;

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
@Constraint(validatedBy = AnyInStringsValidator.class)
public @interface AnyInStrings {

    /**
     * 字段是否允许为 null 值
     *
     * @return 等于 false 时，字段不能为 null
     */
    boolean nullable() default false;

    /**
     * String 值列表，多个值用逗号隔开
     *
     * @return double 值，如："aaa,bbb,ccc,ddd,eee"；注意，在 其他数值验证中，这里会忽略空格，
     * 但在这字符串里不会自动忽略分割符之间的空格
     * @see #trimmed() trimmed == treu 时，就忽略单项的首尾空格
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
     * 是否忽略候选项首尾空格
     *
     * @return
     *
     * @see String#trim()
     */
    boolean trimmed() default false;

    /**
     * 错误消息提示模板
     *
     * @return {@link Message#AT_ALL}
     */
    String message() default "至少一项是枚举值 [{values}] 之一";

    /**
     * 分组验证
     *
     * @return 分组对不同场景下使用不同的验证规则
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
