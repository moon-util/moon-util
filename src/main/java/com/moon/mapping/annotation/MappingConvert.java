package com.moon.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 【待支持: 自定义转换器】
 * <p>
 * 注解在 setter 方法上，标记这个方法是一个转换器
 * <p>
 * 如果一个属性到另一个属性的映射是不同类型，可通过{@code setter}重载的方式
 * 自定义转换器，此时在重载的{@code setter}方法上注解{@link MappingConvert}即可
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
 *     // 通过重载 + 标记 的方式自定义转换器
 *     &#64;MappingConvert
 *     public void setKilometers(Integer km) {
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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@interface MappingConvert {}
