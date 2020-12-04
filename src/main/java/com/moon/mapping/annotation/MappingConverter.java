package com.moon.mapping.annotation;

import java.beans.Introspector;
import java.lang.annotation.*;

/**
 * 自定义转换器
 * <p>
 * 注解在 setter 方法上，标记这个方法是一个转换器
 * <p>
 * 如果一个属性到另一个属性的映射是不同类型，可通过{@code setter}重载的方式
 * 自定义转换器，此时在重载的{@code setter}方法上注解{@link MappingConverter}即可
 * 标记这个方法将被用作转换器。如:
 *
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
 *     &#64;MappingConverter(prefix = "with")
 *     public void withKilometers(Long km) {
 *         this.setKilometers(km == null ? null : km.toString());
 *     }
 *
 *     // 或者这样从指定类转换时使用的转换器
 *     &#64;MappingConverter(prefix = "use", fromClass = Transportation.class)
 *     public void useKilometers(Long km) {
 *         this.setKilometers(km == null ? null : km.toString());
 *     }
 *
 *     public void setKilometers(String kilometers) {
 *         this.kilometers = kilometers;
 *     }
 * }
 * </pre>
 *
 * @author moonsky
 */
@Repeatable(MappingConverter.List.class)
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface MappingConverter {

    /**
     * 注解{@link MappingConverter}的方法将被用作哪个字段的转换器
     * <p>
     * 默认是{@code setter}方法重载，或{@code withXxxProperty}方法，
     * 从方法名中去掉{@code set/with}前缀后执行{@link Introspector#decapitalize(String)}
     * 得到的结果就是将要设置的属性名，如:
     * <pre>
     * &#64;MappingConverter
     * public void setUsername(String username) {} // = {@code username}
     * </pre>
     * 或者直接指定属性名，如:
     * <pre>
     * &#64;MappingConverter(set = "username")
     * public void anyMethodName(String username) {} // = {@code username}
     * </pre>
     *
     * @return 将要截取的前缀，默认是{@code setter}重载，也可自定义任意前缀
     */
    String set() default "";

    /**
     * 从哪个类向当前属性映射时执行的转换，默认任意类
     *
     * @return 类名
     */
    Class<?> fromClass() default void.class;

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    @interface List {

        MappingConverter[] value() default {};
    }
}
