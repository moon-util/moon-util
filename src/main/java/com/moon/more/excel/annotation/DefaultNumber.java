package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;

/**
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultNumber {

    /**
     * 默认值
     *
     * @return default value
     */
    double value() default 0;

    /**
     * 在什么情况下采用默认值
     *
     * @return 策略
     */
    Strategy defaultFor() default Strategy.NONE;

    /**
     * 在什么情况下采用默认值，同{@link #defaultFor()}
     * <p>
     * 但设置了{@link #testBy()}就不会执行{@link #defaultFor()}
     *
     * @return Predicate 实现类，接收参数为读取到的字段值
     */
    Class<? extends Predicate> testBy() default Predicate.class;

    enum Strategy implements DoublePredicate {
        NONE {
            @Override
            public boolean test(double value) { return false; }
        },
        /**
         * 0
         */
        ZERO {
            @Override
            public boolean test(double value) { return value == 0; }
        },
        /**
         * 1
         */
        ONE {
            @Override
            public boolean test(double value) { return value == 1; }
        },
        /**
         * -1
         */
        MINUS_ONE {
            @Override
            public boolean test(double value) { return value == -1; }
        },
        /**
         * 负数
         */
        NEGATIVE {
            @Override
            public boolean test(double value) { return value < 0; }
        },
        /**
         * 正数
         */
        POSITIVE {
            @Override
            public boolean test(double value) { return value > 0; }
        },
        /**
         * 非负数
         */
        NOT_NEGATIVE {
            @Override
            public boolean test(double value) { return value >= 0; }
        },
        /**
         * 非正数
         */
        NOT_POSITIVE {
            @Override
            public boolean test(double value) { return value <= 0; }
        }
    }
}
