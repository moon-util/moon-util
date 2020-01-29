package com.moon.core.util.console;

import com.moon.core.util.Console;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Console 配置
 *
 * @author benshaoye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsoleControl {

    /**
     * 与{@link #fromImplement()}等效，详细注释见{@link #fromImplement()}
     * 优先于 fromImplement()
     *
     * @return 目标设置提供器
     *
     * @see #fromImplement()
     */
    Class<? extends ConsoleSettingsSupplier> value() default ConsoleSettingsSupplier.class;

    /*
     * ------------------------------------------------------------------
     * 输出内容控制：一旦设置了便不可改变
     * ------------------------------------------------------------------
     */

    /**
     * 用 html 格式输出
     *
     * @return 是否 html 格式
     */
    boolean html() default false;

    /**
     * 是否异步打印
     *
     * @return 是否异步打印
     */
    boolean async() default true;

    /**
     * 当前类最低输出级别
     *
     * @return 最低输出级别
     */
    Console.Level lowestLevel() default Console.Level.PRINT;

    /**
     * 输出内容格式
     * level 是可选择，同{@link #filenameFormat()}类似，手动设置 level 只是用来改变位置的
     *
     * @return 输出内容格式
     */
    String pattern() default "HH:mm:ss,SSS level";

    /**
     * 当前配置是否只是默认参数，
     * 如果为 true，则当前配置至少默认，检查会继续沿着调用栈先前查找直到为 false 或 最后一个为止
     * 主要用于覆盖默认设置，只能通过注解设置此属性，不能通过自定义类或 JSON 文件设置
     *
     * @return 当前配置是否只是默认参数
     */
    boolean isDefault() default false;
    /*
     * ------------------------------------------------------------------
     * 输出位置控制：设置后可以被改变
     * ------------------------------------------------------------------
     */

    /**
     * 目录分类依据，根据设置的顺序分类
     *
     * @return 目录分类依据，根据设置的顺序分类
     */
    @Deprecated Classify[] classifies() default {};

    /**
     * 文件名格式
     * level 是可选项，如果{@link #classifies()}存在{@link Classify#LEVEL}
     * 无论是否配置 level 选项，都会自动在文件名末加上，否则则不加
     * 即，以下设置是等效的，但是可以通过手动设置 level 改变它的位置
     * - yyyy-MM-dd
     * - yyyy-MM-dd-level
     * - yyyy-MM-dd-?level
     * <p>
     * 设置的时候注意防止文件名冲突
     *
     * @return 文件名格式
     */
    @Deprecated String filenameFormat() default "yyyy-MM-dd-?level";

    /*
     * ------------------------------------------------------------------
     * 其他配置方式，更灵活的控制输出设置
     * ------------------------------------------------------------------
     */

    /**
     * 从一个实现{@link ConsoleSettingsSupplier}的类解析
     * 单例，引用同一个类得到的是同一个对象
     * 与{@link #value()}等效，但 value() 优先于 fromImplement()
     *
     * @return 实现类
     *
     * @see IllegalArgumentException 当如果指定类没有实现接口{@link ConsoleSettingsSupplier}或没有空构造器时
     * @see #value()
     */
    @Deprecated Class fromImplement() default ConsoleSettingsSupplier.class;

    /**
     * 从 JSON 文件解析
     * 单例，引用同一个 json 文件的设置得到的是同一个对象
     * {
     * "html": false, // true or false
     * "lowestLevel": "DEBUG", // value in {@link Console.Level}
     * "basePath": "target/path",
     * "classifies": "NON",// an array and items in {@link Classify} or ont item in {@link Classify}
     * "filenameFormat": "",
     * "pattern": "",
     * }
     *
     * @return JSON 文件
     */
    @Deprecated String fromJson() default "";

    /*
     * ------------------------------------------------------------------
     * other
     * ------------------------------------------------------------------
     */

    enum Classify {
        /**
         * 按{@link Console.Level}输出级别分类
         */
        LEVEL,
        /**
         * 按季度分类
         */
        QUARTER,
        /**
         * 按月分类
         */
        MONTH,
        /**
         * 按周分类，每周从星期日为第一天
         */
        WEEK
    }
}
