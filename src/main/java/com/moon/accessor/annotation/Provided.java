package com.moon.accessor.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 注解在 getter 方法上，在 spring 环境中提供对应对象，如：
 * <p>
 * {@link Provided}是为接口默认方法{@code default}从 spring 容器中获取 bean 的途径
 * <p>
 * 如果不是在接口中获取的话，也就没必要使用{@link Provided}了，因为类中有多种方法可以获取 bean
 *
 * <pre>
 * &#64;Accessor
 * public interface UserAccessor {
 *
 *     &#64;Provided // userService
 *     UserService getUserService();
 *
 *     &#64;Provided("companyService")
 *     CompanyService getService();
 *
 *     &#64;Provided("jdbcTemplate")
 *     JdbcTemplate getJdbcTemplate();
 *
 *     default List update() {
 *         getJdbcTemplate().queryForList() ...
 *         return ...
 *     }
 * }
 * </pre>
 *
 * @author benshaoye
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Provided {

    /**
     * spring bean name
     * <p>
     * 默认是从 getter 方法中解析，如：
     *
     * <pre>
     * &#64;Provided // userService
     * UserService getUserService();
     * </pre>
     *
     * @return bean name
     *
     * @see Qualifier#value()
     */
    String value() default "";

    /**
     * 是否必须
     *
     * @return true/false
     *
     * @see Autowired#required()
     */
    boolean required() default true;
}
