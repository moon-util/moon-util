package com.moon.core.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target(ElementType.FIELD)
public @interface JSONProperty {

    /**
     * 重命名
     *
     * @return
     */
    String name() default "";

    /**
     * 给 Date 和 Number 使用
     *
     * @return
     */
    String format() default "";

    boolean stringify() default true;

    boolean parse() default true;
}
