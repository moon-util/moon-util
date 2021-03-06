package com.moon.validator.annotation;

import com.moon.validator.impl.AllInStringsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = AllInStringsValidator.class)
public @interface AllInStrings {

    /**
     * 数据项是否允许为 null 值，只验证集合或数组里的数据项是否可为 null
     * <p>
     * 如果要验证集合/数组字段本身是否可为 null、empty 请用{@link NotNull}、{@link NotEmpty}
     *
     * @return 等于 false 时不可包含 null 数据项
     */
    boolean nullable() default true;

    /**
     * 字段数据项可选值枚举
     *
     * @return 字段所有允许的值列举在这里，用逗号隔开；间隔符之间的空格不会忽略
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
    String message() default "所有项必须是枚举值 [{values}] 之一";

    /**
     * 分组验证
     *
     * @return 分组对不同场景下使用不同的验证规则
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
