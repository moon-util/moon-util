package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 渲染实体总体描述
 *
 * @author benshaoye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableRecord {

    /**
     * 如果当前行数据是 null 是否插入一个空行
     *
     * @return true: 始终插入一个空行
     */
    boolean keepSkip() default false;

    /**
     * 偏移，
     * <p>
     * 当{@link #offset()}大于 0 时，可在当前行前插入空行
     * 小于 0 时，会“回退”覆盖已有行
     *
     * @return
     */
    int offset() default 0;

    /**
     * 优先策略，默认分组优先
     * <p>
     * 即相同顶级分组名的在一组，然后在同组下按序号先后排序
     * <p>
     * 设置为序号优先后，优先严格按照序号大小排序后再分组
     * <p>
     * <p>
     * 在不少情况下，两种情况可能并没有区别，
     * 只有在序号{@code order}设置混乱的情况下，两种策略才会有明显区别，
     * 当然，如果序号本身混乱，两种策略虽可能会有不同结果，但也不会给理解带来方便
     * <p>
     * 综上，不建议使用这个属性，而是尽量管理好{@code order}顺序
     *
     * @return 优先策略
     *
     * @see TableColumn#order()
     * @see TableColumnFlatten#order()
     */
    Priority priority() default Priority.GROUP;
}
