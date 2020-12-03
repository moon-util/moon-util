package com.moon.implement.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AutoImplement {

    /**
     * 接口实现类后缀，不能为空
     *
     * @return
     */
    String suffix() default "Impl";
}
