package com.moon.accessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在 ID 字段上标记 ID 策略，通常多主键表将主键策略注解在类（或父类）上
 * <p>
 * 单主键表（多数都是单主键）可定义在主键字段或类上，子类上主键策略覆盖父类的主键策略
 *
 * @author benshaoye
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableId {

    /**
     * 多主键表中用于指定主键值索引
     *
     * @return
     */
    int value() default 0;
}
