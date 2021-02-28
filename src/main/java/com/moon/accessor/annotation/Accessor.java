package com.moon.accessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Accessor {

    /**
     * 要访问的实体/数据表
     *
     * @return 实体类
     */
    Class<?> value();

    /**
     * 严格模式
     * <p>
     * 当使用严格模式时，从方法中声明上解析 SQL 语句时，
     * 与实际字段类型关联的参数名必须一致，参数类型必须兼容（本类或子类）
     * 否则将解析失败；
     * <p>
     * 当使用非严格模式时，若不存在对应字段或字段类型不一致就直接忽略；
     *
     * @return
     */
    boolean useStrict() default true;
}
