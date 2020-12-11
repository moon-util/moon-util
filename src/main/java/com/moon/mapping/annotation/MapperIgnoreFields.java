package com.moon.mapping.annotation;

import java.lang.annotation.*;

/**
 * 忽略映射字段
 * <p>
 * 有时候可能无法在字段上使用{@link Mapping}，比如第三方包父类中的字段
 * <p>
 * 1. 此时可在映射类上添加这个注解，并指定要忽略的字段{@link #value()}；
 * <p>
 * 2. 当{@link #target()}指定了值时，就只忽略针对指定类中的字段映射；
 * <p>
 * 3. {@link #ignore()}可指定忽略模式，默认双向映射均忽略；
 * <p>
 * 4. 单独使用无效，需结合{@link MapperFor}使用；
 * <p>
 * 5. {@link MapperIgnoreFields}和{@link Mapping}任一注解存在均有效，如果某一字段同时被
 * {@link MapperIgnoreFields}和{@link Mapping#ignore()}注解忽略，以{@link MapperIgnoreFields}为准，例：
 *
 * <pre>
 * &#64;MapperFor({UserVO.class})
 * &#64;MapperIgnoreFields(value = "name", mode = IgnoreMode.FORWARD)
 * public class UserModel {
 *
 *     // 优先级低于{@code MapperIgnoreFields(mode = IgnoreMode.FORWARD)}
 *     // 故，最终只忽略{@code FORWARD}映射
 *     &#64;Mapping(value = "name", ignore = IgnoreMode.ALL)
 *     private String name;
 * }
 * </pre>
 * <p>
 * 6. {@link MapperIgnoreFields}只可用于忽略“本类属性”，即如果所继承的父类也有注解{@code MapperIgnoreFields}
 * “本类”不会主动忽略，除非本类也忽略了该字段，如：
 *
 * <pre>
 * &#64;MapperFor({SuperVO.class})
 * &#64;MapperIgnoreFields(value = {"id", "name"})
 * public class SuperModel {
 *
 *     private String id;
 *     private String name;
 * }
 *
 * // 父类忽略了两个字段: id、name，本类只忽略了: name
 * // 最终也只忽略 name 字段，父类忽略的不会影响本类
 * &#64;MapperFor({ChildVO.class})
 * &#64;MapperIgnoreFields(value = "name")
 * public class ChildModel extends SuperModel {
 *
 *     private String value;
 * }
 * </pre>
 * <p>
 * 7. {@link MapperIgnoreFields}的设计场景是针对第三方包的父类，由于不能直接操作，
 * 故提供了这种方式，而且这也不是唯一方式，比如重写父类的 getter、setter 方法，并在 getter、setter
 * 上通过{@link Mapping}实现，故暂时不考虑继承父类{@code MapperIgnoreFields}或抑制父类忽略的字段等需求
 *
 * @author benshaoye
 */
@Repeatable(MapperIgnoreFields.List.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperIgnoreFields {

    /**
     * 将要忽略的字段
     *
     * @return 字段列表
     */
    String[] value();

    /**
     * 忽略那个类的{@link #value()}字段
     *
     * @return 指定类
     */
    Class<?> target() default void.class;

    /**
     * 忽略模式
     *
     * @return 默认忽略双向映射
     */
    IgnoreMode ignore() default IgnoreMode.ALL;

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        MapperIgnoreFields[] value() default {};
    }
}
