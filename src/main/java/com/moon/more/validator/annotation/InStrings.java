package com.moon.more.validator.annotation;

import com.moon.more.validator.impl.InStringsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 要求字符串或者枚举值必须是指定列表中的值之一
 * 通过字段值的{@link Object#toString()}后的字符串在指定区间匹配
 * <p>
 * 比如：枚举包括：ENUM1,ENUM2,ENUM3,ENUM4；
 * 但是字段要求只能是 ENUM1 或 ENUM2，那么可以这样用：
 * <pre>
 *     public enum EnumType {
 *         ENUM1, ENUM2, ENUM3, ENUM4
 *     }
 *
 *     @ InStrings(values = "ENUM1,ENUM2") // 注意这里不能有多余的空格
 *     private EnumType value;
 *
 *     // 声明 trimmed = true 后会进行一次 String.trim() 操作，这样就可以有多余的空格
 *     @ InStrings(values = "ENUM1,ENUM2", trimmed = true)
 *     private EnumType type;
 *
 *     // 自定义分隔符
 *     @ InStrings(values = "str1;str2;str3", delimiter = ";")
 *     private String str;
 * </pre>
 *
 * @author benshaoye
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = InStringsValidator.class)
public @interface InStrings {

    /**
     * 字段是否允许为 null 值
     *
     * @return 等于 false 时，字段不能为 null
     */
    boolean nullable() default true;

    /**
     * 字段值可选项枚举
     *
     * @return 字段所有允许的值列举在这里，用逗号隔开，间隔符之间的空格不会忽略
     *
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
    String message() default "必须是枚举值 [{values}] 之一";

    /**
     * 分组验证
     *
     * @return 分组对不同场景下使用不同的验证规则
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
