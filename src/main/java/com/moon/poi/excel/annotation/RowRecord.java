package com.moon.poi.excel.annotation;

import com.moon.poi.excel.annotation.style.DefinitionStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RowRecord {

    /**
     * 引入从其他类定义的样式，这样可以不用重复定义
     * <p>
     * 引入的样式包括（设引入的另一个类为{@code StyleClass}）：
     * <pre>
     * 1. 直接定义在类上的{@link DefinitionStyle}；
     * 3. 定义在字段或{@code getter} 上并且主动设置{@link DefinitionStyle#classname()}的样式；
     * 4. 如果也定义了{@code importStyles}，那么也会按优先级关系递归引入；
     * 5. 内部通过{@link SheetColumnGroup} 形成的多级关系的样式不会引入；
     * </pre>
     *
     * @return 引入在其他类定义的样式定义到自己的作用域
     */
    Class[] importStyles() default {};
}
