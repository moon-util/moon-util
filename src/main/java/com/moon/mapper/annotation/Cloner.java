package com.moon.mapper.annotation;

import com.moon.mapper.BeanCloner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记该对象是可以按属性克隆的
 *
 * @author benshaoye
 * @see BeanCloner 被{@link Cloner}注解的类会生成一个对应的对象克隆器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cloner {

    /**
     * 是否深度克隆
     *
     * @return true; 可以定义不支持深度克隆
     */
    boolean deep() default true;
}
