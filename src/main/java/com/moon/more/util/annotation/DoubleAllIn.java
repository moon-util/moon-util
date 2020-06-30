package com.moon.more.util.annotation;

import com.moon.more.util.annotation.validator.DoubleAllInValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
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
@Constraint(validatedBy = DoubleAllInValidator.class)
public @interface DoubleAllIn {

    /**
     * 集合或数组中是否允许 null 值
     *
     * @return true 表示允许 null 值
     */
    boolean nullable() default true;

    /**
     * 数值选项列表，所有候选数值只能列举出来，不能是区间
     * 多个数值之间用逗号间隔
     *
     * @return 数值列表，如："1,2.3,3.4"
     */
    String values() default "";

    /**
     * 错误消息提示模板
     *
     * @return {@link MessageConst#AT_ALL}
     */
    String message() default MessageConst.AT_ALL;

    /**
     * 分组验证
     *
     * @return 分组对不同场景下使用不同的验证规则
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
