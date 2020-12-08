package com.moon.mapping.annotation;

import java.beans.Introspector;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义转换器
 * <p>
 * 注解在 setter 方法上，标记这个方法是一个转换器，以为：
 * <pre>
 * 为{@link #fromClass()}映射到本类{@link #value()}属性提供一个指定数据类型转换器。
 * </pre>
 * <p>
 * 如果一个属性到另一个属性的映射是不同类型，可通过{@code setter}重载的方式
 * 自定义转换器，此时在重载的{@code setter}方法上注解{@link MappingConverter}即可
 * 标记这个方法将被用作转换器。如:
 * <pre>
 * public class Car {
 *     private Integer kilometers;
 *     // ...
 * }
 *
 * &#64;MappingFor(Car.class)
 * public class CarVO {
 *     private String kilometers;
 *
 *     // 自定义转换器
 *     &#64;MappingConverter
 *     public void setKilometers(Integer km) {
 *         this.setKilometers(km == null ? null : km.toString());
 *     }
 *
 *     // 也可以这样自定义转换器
 *     &#64;MappingConverter
 *     public void withKilometers(Long km) {
 *         this.setKilometers(km == null ? null : km.toString());
 *     }
 *
 *     // 或者这样从指定类转换时使用的转换器
 *     &#64;MappingConverter(fromClass = Transportation.class)
 *     public void kilometers(Long km) {
 *         this.setKilometers(km == null ? null : km.toString());
 *     }
 *
 *     public void setKilometers(String kilometers) {
 *         this.kilometers = kilometers;
 *     }
 * }
 * </pre>
 * <p>
 * 被注解的方法应该是公共成员方法接受一个参数切返回值是{@code void}
 *
 * @author moonsky
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface MappingConverter {

    /**
     * 注解{@link MappingConverter}的方法将被用作哪个字段的转换器
     * <p>
     * 默认是{@code setter}方法重载，或{@code withXxx}方法，
     * 从方法名中去掉{@code set/with}前缀后执行{@link Introspector#decapitalize(String)}
     * 得到的结果就是将要设置的属性名，如:
     * <pre>
     * &#64;MappingConverter
     * public void setUsername(String username) {} // = {@code username}
     *
     * &#64;MappingConverter
     * public void withAge(String username) {} // = {@code age}
     *
     * &#64;MappingConverter
     * public void address(String username) {} // = {@code address}
     *
     * // 或者直接指定属性名，如:
     * &#64;MappingConverter(value = "username")
     * public void anyMethodName(String username) {} // = {@code username}
     * </pre>
     *
     * @return 转换器用于设置的属性名
     */
    String value() default "";

    /**
     * 从哪个类向当前属性映射时执行的转换，默认任意类
     *
     * @return 类名
     */
    Class<?> fromClass() default void.class;
}
