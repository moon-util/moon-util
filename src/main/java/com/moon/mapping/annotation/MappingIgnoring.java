package com.moon.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 映射忽略
 * <p>
 * 有时候可能无法在字段上使用{@link Mapping}，比如第三方包父类中的字段
 * <p>
 * 1. 此时可在映射类上添加这个注解，并指定要忽略的字段{@link #value()}
 * <p>
 * 2. 当{@link #target()}指定了值时，就只忽略针对指定类中的字段映射
 * <p>
 * 3. {@link #mode()}可指定忽略模式，默认双向映射均忽略
 * <p>
 * 4. {@link MappingIgnoring}和{@link Mapping}任一注解存在均有效，如果某一字段同时被
 * {@link MappingIgnoring}和{@link Mapping#ignore()}注解忽略，以{@link MappingIgnoring}为准，例：
 *
 * <pre>
 * &#64;MappingFor({UserVO.class})
 * &#64;MappingIgnoring(value = "name", mode = IgnoreMode.FORWARD)
 * public class UserModel {
 *
 *     // 优先级低于{@code MappingIgnoring(mode = IgnoreMode.FORWARD)}
 *     // 故，最终只忽略{@code FORWARD}映射
 *     &#64;Mapping(value = "name", ignore = IgnoreMode.ALL)
 *     private String name;
 * }
 * </pre>
 *
 * @author benshaoye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingIgnoring {

    String[] value();

    Class<?> target() default void.class;

    IgnoreMode mode() default IgnoreMode.ALL;
}
