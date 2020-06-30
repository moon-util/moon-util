package com.moon.more.util.annotation;

import com.moon.more.util.annotation.validator.IntInValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限制输入的 int 型数字
 * <p>
 * 默认允许 null 值（当字段类型是{@link Integer}时）
 * <p>
 * 可指定{@link #nullable()}=true 要求不能为 null，或使用{@link javax.validation.constraints.NotNull}
 * <p>
 * 如要求 int 值在：1, 2, 3 中
 * <pre class="code">
 *     &#064;IntIn(values = "1,2,3")
 *     private int value;
 * </pre>
 * <p>
 * 默认允许 null 值
 *
 * @author benshaoye
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = IntInValidator.class)
public @interface IntIn {

    boolean nullable() default true;

    String values() default "";

    String message() default "必须是数值 [{values}] 之一";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
