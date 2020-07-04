package com.moon.more.validator.annotation;

import com.moon.more.validator.impl.AnyInIntsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 集合或数组至少有一项的值在 int 值列表中
 *
 * @author moonsky
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = AnyInIntsValidator.class)
public @interface AnyInInts {

    /**
     * 数据项是否允许为 null 值，只验证集合或数组里的数据项是否可为 null
     * <p>
     * 如果要验证集合/数组字段本身是否可为 null、empty 请用{@link NotNull}、{@link NotEmpty}
     *
     * @return 等于 false 时不可包含 null 数据项
     */
    boolean nullable() default false;

    /**
     * int 值列表，多个值用逗号隔开
     *
     * @return int 值，如："1, 2, 3, 4, 5"；间隔符之间的空格会自动忽略
     */
    String values() default "";

    /**
     * 错误消息提示模板
     *
     * @return {@link Message#AT_ALL}
     */
    String message() default Message.AT_ANY;

    /**
     * 分组验证
     *
     * @return 分组对不同场景下使用不同的验证规则
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
