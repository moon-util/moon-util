package com.moon.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 【未来支持，而且除了日期格式化外功能也还不明确】
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

    /**
     * 解析从字符串中日期、时间、数字时的判断方式
     *
     * @return
     */
    Strategy parseStringOn() default Strategy.NOT_EMPTY;

    enum Strategy {
        /** 不为 null */
        NOT_NULL,
        /** 不为 null 或空字符串时符合解析条件，此时才会解析字符串为日期、时间、数字等 */
        NOT_EMPTY,
        /** 不为 null、空字符串或只包含空白字符的字符串 */
        NOT_BLANK,
        /** 不为 null、空字符串或 "null" */
        NOT_WEB_NULL,
        /** 不为 null、空字符串、 "null"、 "undefined" */
        NOT_WEB_UNDEFINED,
    }
}
