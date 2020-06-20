package com.moon.more.excel.annotation.format;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * 只能注解在数字字段
 *
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberPattern {

    /**
     * 数字格式
     *
     * @return
     */
    String value() default "";

    /**
     * 是否分组
     *
     * @return true: 返回结果用逗号没三围一组
     */
    boolean grouped() default true;

    /**
     * 取整方式
     *
     * @return 默认四舍五入
     */
    RoundingMode roundingMode() default RoundingMode.HALF_UP;

    /**
     * 语言
     *
     * @return 根据系统自动获取
     */
    LocaleStrategy locale() default LocaleStrategy.DEFAULT;

    /**
     * 最大整数位数
     *
     * @return return 默认 -1（小于{@code 0}为不设置）
     *
     * @see NumberFormat#setMaximumIntegerDigits(int)
     */
    int maxIntDigit() default -1;


    /**
     * 最小整数位数
     *
     * @return return 默认 -1（小于{@code 0}为不设置）
     *
     * @see NumberFormat#setMinimumIntegerDigits(int)
     */
    int minIntDigit() default -1;

    /**
     * 最大小数位数
     *
     * @return 默认 -1（小于{@code 0}为不设置）
     *
     * @see NumberFormat#setMaximumFractionDigits(int)
     */
    int maxFractionDigit() default -1;

    /**
     * 最小小数位数
     *
     * @return 默认 -1（小于{@code 0}为不设置）
     *
     * @see NumberFormat#setMinimumFractionDigits(int)
     */
    int minFractionDigit() default -1;
}
