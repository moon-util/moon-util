package com.moon.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 【未来支持，而且除了日期格式化外其他功能需求也还不明确】
 * <p>
 * 一些全局设置，随便注解在一个类上即可，而且最多只能注解一个
 * <p>
 * 如果注解了多个，最后可能不知道使用的到底是哪个
 *
 * @author benshaoye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@interface GlobalSettings {

    String dateFormat() default "";
}
