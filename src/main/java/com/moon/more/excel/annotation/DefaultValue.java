package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

/**
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {

    /**
     * 默认值
     *
     * @return 默认值
     */
    String value() default "";

    /**
     * 在什么情况下采用默认值
     *
     * @return 策略
     */
    Strategy defaultFor() default Strategy.NULL;

    /**
     * 在什么情况下采用默认值，同{@link #defaultFor()}
     * <p>
     * 但设置了{@link #testBy()}就不会执行{@link #defaultFor()}
     *
     * @return Predicate 实现类，接收参数为读取到的字段值
     */
    Class<? extends Predicate> testBy() default Predicate.class;

    enum Strategy implements Predicate {
        /**
         * null
         */
        NULL {
            @Override
            public boolean test(Object o) { return o == null; }
        },
        /**
         * 用字符串的方式检查对象是否是空字符串
         */
        EMPTY {
            @Override
            public boolean test(Object o) { return o == null || o.toString().length() == 0; }
        },
        /**
         * 用字符串的方式检查对象是否是空白字符串
         */
        BLANK {
            @Override
            public boolean test(Object o) {
                if (o == null) { return true; }
                String s = o.toString();
                return s.length() == 0 || s.trim().length() == 0;
            }
        }
    }
}
