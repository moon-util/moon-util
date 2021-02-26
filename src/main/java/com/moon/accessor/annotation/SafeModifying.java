package com.moon.accessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 安全的更新语句，当 update/delete 方法只有一个参数时
 * 代表这个方法只有 set 子句而没有 where 子句，这样的操作是不安全的
 * 因而在此种情况下默认不会执行任何语句
 * <p>
 * 如确认这样的场景应该被正确执行需要添加此注解，表名应该执行全表更新/删除
 *
 * @author benshaoye
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeModifying {}
