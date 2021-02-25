package com.moon.poi.excel.annotation.value;

import com.moon.poi.excel.annotation.SheetColumn;
import com.moon.poi.excel.annotation.SheetColumnGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

/**
 * 字段默认值，只能和{@link SheetColumn}搭配使用，对该字段可能存在不符合预期的字段值提供降级方案；
 * <p>
 * 单独注解或者与{@link SheetColumnGroup}等搭配使用无效
 *
 * @author moonsky
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
    Strategy when() default Strategy.NULL;

    /**
     * 在什么情况下采用默认值，同{@link #when()}
     * <p>
     * 但设置了{@link #testBy()}就不会执行{@link #when()}
     *
     * @return Predicate 实现类，接收参数为读取到的字段值
     */
    Class<? extends Predicate> testBy() default Predicate.class;

    /**
     * 当注解字段所在的对象为 null 时，是否填充默认值
     * <p>
     * 注意与{@link #when()}的区别，{@code when}是对字段值进行判断的，
     * <p>
     * 而这个是判断所在的对象是否为 null
     *
     * @return true: 总是填充默认值
     */
    boolean defaultForNullObj() default false;

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
