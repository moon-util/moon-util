package com.moon.more.excel.annotation.defaults;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnGroup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Predicate;

/**
 * 数字字段默认值，只能和{@link TableColumn}搭配使用，对该字段可能存在不符合预期的字段值提供降级方案;
 * <p>
 * 单独注解、注解在非数字字段上或者与{@link TableColumnGroup}等搭配使用无效
 * <p>
 * 判断数字字段的方式：字段是否是{@link Number}的子类
 * 以及这些基本数据类型：byte、short、int、long、float、double
 * <p>
 * 当与{@link DefaultValue}注解于同一数字字段时，以{@link DefaultNumber}为准，
 * 因为{@link DefaultNumber}更具体
 *
 * @author moonsky
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
     * <p>
     * 用到默认值的情况通常很可能会出现 null 值，请注意空指针问题，
     * <p>
     * 这里也单独提供了{@link Strategy#NULL}用来检测空指针，并优先检测空指针
     *
     * @return 策略
     */
    Strategy[] when() default {};

    /**
     * 在什么情况下采用默认值，同{@link #when()}
     * <p>
     * 但设置了{@link #testBy()}就不会执行{@link #when()}
     *
     * @return Predicate 实现类，接收参数为读取到的字段值，如果是基本数据类型传入的是对应包装类
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

    enum Strategy implements Predicate<Number> {
        /**
         * null
         */
        NULL {
            @Override
            public boolean test(Number value) { return value == null; }
        },
        /**
         * 0
         */
        ZERO {
            @Override
            public boolean test(Number value) { return value.doubleValue() == 0; }
        },
        /**
         * 1
         */
        ONE {
            @Override
            public boolean test(Number value) { return value.doubleValue() == 1; }
        },
        /**
         * -1
         */
        MINUS_ONE {
            @Override
            public boolean test(Number value) { return value.doubleValue() == -1; }
        },
        /**
         * 负数
         */
        NEGATIVE {
            @Override
            public boolean test(Number value) { return value.doubleValue() < 0; }
        },
        /**
         * 正数
         */
        POSITIVE {
            @Override
            public boolean test(Number value) { return value.doubleValue() > 0; }
        },
        /**
         * 非负数
         */
        NOT_NEGATIVE {
            @Override
            public boolean test(Number value) { return value.doubleValue() >= 0; }
        },
        /**
         * 非正数
         */
        NOT_POSITIVE {
            @Override
            public boolean test(Number value) { return value.doubleValue() <= 0; }
        },
        /*
        由于这里用的是 DOUBLE 类型数据，故使用求余算法，可针对所有数字类型进行计算
         */
        /**
         * 奇数
         */
        ODD {
            @Override
            public boolean test(Number value) { return value.doubleValue() % 2 == 1; }
        },
        /**
         * 偶数
         */
        EVEN {
            @Override
            public boolean test(Number value) { return value.doubleValue() % 2 == 0; }
        }
    }
}
