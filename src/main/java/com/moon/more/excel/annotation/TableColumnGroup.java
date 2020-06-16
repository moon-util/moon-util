package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableColumnGroup {

    /**
     * 列标题，列标题默认为一个数组，相邻单元格相同的话会自动合并单元格
     * <p>
     * 但如果设置了偏移，会影响合并的最终结果，参考：{@link #offset()}, {@link #offsetAll4Head()}
     * <p>
     * 默认字段名首字母大写，如：name -&gt; Name；age -&gt; Age
     *
     * @return 列标题
     */
    String[] value() default {};

    /**
     * 表头行高，
     * <p>
     * 数量和位置与表头标题{@link #value()}对应，
     * <p>
     * 不足的自动用<code>-1（不设置）</code>补上，
     * <p>
     * 超出部分将被忽略；
     * <p>
     * 只有设置了标题的位置才能设置行高，否则就忽略
     *
     * @return 表头行高，
     */
    short[] rowsHeight4Head() default {};

    /**
     * 排序顺序，顺序号会影响列所渲染的位置，相同顺序号按字段声明顺序排序
     *
     * @return 顺序号
     */
    int order() default 0;

    /**
     * 偏移
     *
     * @return 偏移量
     */
    int offset() default 0;

    /**
     * 表头是否“通列偏移”，而不只是最后一级偏移
     *
     * @return true: 通列偏移; false: 只偏移最后一级
     */
    boolean offsetAll4Head() default false;
}
