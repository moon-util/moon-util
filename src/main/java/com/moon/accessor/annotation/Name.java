package com.moon.accessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 给 getter 方法或方法参数指定名称
 * <p>
 * 注解在 getter 方法代表映射该实体对应同名的字段
 * <p>
 * 注解在方法参数上同理
 *
 * @author benshaoye
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.SOURCE)
public @interface Name {

    String value() default "";
}
