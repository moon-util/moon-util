package com.moon.validator.annotation;

import com.moon.validator.impl.AllInDoublesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只能注解在 double 数组、Double 数组，或 Double 集合字段上
 * 表示集合中的所有 double 数值必须在某个候选值范围内
 *
 * @author moonsky
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = AllInDoublesValidator.class)
public @interface AllInDoubles {

    /**
     * 数据项是否允许为 null 值，只验证集合或数组里的数据项是否可为 null
     * <p>
     * 如果要验证集合/数组字段本身是否可为 null、empty 请用{@link NotNull}、{@link NotEmpty}
     *
     * @return 等于 false 时不可包含 null 数据项
     */
    boolean nullable() default true;

    /**
     * 数值选项列表，所有候选数值只能列举出来，不能是区间
     * 多个数值之间用逗号间隔
     *
     * @return 数值列表，如："1,2.3,3.4"；间隔符之间的空格会自动忽略
     */
    String values() default "";

    /**
     * 错误消息提示模板
     *
     * @return {@link Message#AT_ALL}
     */
    String message() default Message.AT_ALL;

    /**
     * 分组验证
     *
     * @return 分组对不同场景下使用不同的验证规则
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
