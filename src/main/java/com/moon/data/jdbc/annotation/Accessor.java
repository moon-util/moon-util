package com.moon.data.jdbc.annotation;

import com.moon.data.jdbc.dialect.Dialect;

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
     * 被注解接口对应的实体类
     *
     * @return 与表对应的类名
     */
    Class<?> value() default void.class;

    /**
     * 严格模式:
     * <p>
     * 严格模式中使用到的接口{@code getter}、{@code setter}
     * 对应的字段必须在对应实体类中存在，或提供默认实现{@code default}，
     * 否则将抛出异常
     * <p>
     * 非严格模式则无此限制，但是在增删改查的时候只使用实体类中存在的字段，
     * 不存在的忽略
     *
     * @return 严格模式
     */
    boolean useStrict() default true;

    /**
     * 自定义实现类类名，其中{@code "<interface>"}作为占位符，用于替换
     * 被{@link Accessor}注解接口的接口名
     *
     * @return 实现类类名
     */
    String classname() default "<interface>Impl";

    /**
     * 数据库方言
     *
     * @return
     */
    Dialect dialect() default Dialect.AUTO;
}
