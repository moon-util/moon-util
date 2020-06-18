package com.moon.more.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableRecord {

    /**
     * 行高, 小于 0 默认不设置，超过表头行数的忽略；
     * <p>
     * 如有通过{@link TableColumnGroup}形成了多级关系，最终按每行最大高度设置；
     *
     * @return
     */
    short[] headerRowsHeight() default {};

    /**
     * 定义样式，这里定义的样式最终会合{@link #importStyles()}的样式合并
     * <p>
     * 这里定义的优先级更高，会覆盖{@code includeStyles}的同名样式
     *
     * @return
     */
    DefinitionStyle[] styles() default {};

    /**
     * 引入从其他类定义的样式，这样可以不用重复定义
     * <p>
     * 引入的样式包括（设引入的另一个类为{@code StyleClass}）：
     * <pre>
     * 1. 直接定义在类{@code StyleClass}上的{@code DefinitionStyle}；
     * 2. 定义在类{@code StyleClass}#{@link #styles()}里的{@code DefinitionStyle}；
     * 3. 定义在{@code StyleClass} 字段或{@code getter} 上并且主动设置{@link DefinitionStyle#classname()}的样式；
     * 4. 如果{@code StyleClass} 也定义了{@code importStyles}，那么也会按优先级关系引入；
     * 5. {@code StyleClass} 内部通过{@link TableColumnGroup} 形成的父子关系的样式不会引入；
     * </pre>
     *
     * @return
     */
    Class[] importStyles() default {};
}
