package com.moon.mapping.annotation;

import java.lang.annotation.*;

/**
 * 映射忽略
 * <p>
 * 有时候可能无法在字段上使用{@link Mapping}，比如第三方包父类中的字段
 * <p>
 * 1. 此时可在映射类上添加这个注解，并指定要忽略的字段{@link #value()}；
 * <p>
 * 2. 当{@link #target()}指定了值时，就只忽略针对指定类中的字段映射；
 * <p>
 * 3. {@link #mode()}可指定忽略模式，默认双向映射均忽略；
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
 * <p>
 * 5. {@link MappingIgnoring}只可用于忽略“本类属性”，即如果所继承的父类也有注解{@code MappingIgnoring}
 * “本类”不会主动忽略，除非本类也忽略了该字段，如：
 *
 * <pre>
 * &#64;MappingFor({SuperVO.class})
 * &#64;MappingIgnoring(value = {"id", "name"})
 * public class SuperModel {
 *
 *     private String id;
 *     private String name;
 * }
 *
 * // 父类忽略了两个字段: id、name，本类只忽略了: name
 * // 最终也只忽略 name 字段，父类忽略的不会影响本类
 * &#64;MappingFor({ChildVO.class})
 * &#64;MappingIgnoring(value = "name")
 * public class ChildModel extends SuperModel {
 *
 *     private String value;
 * }
 * </pre>
 * <p>
 * 6. {@link MappingIgnoring}的设计场景是针对第三方包的父类，由于不能直接操作，
 * 故提供了这种方式，而且这也不是唯一方式，比如重写父类的 getter、setter 方法，并在 getter、setter
 * 上通过{@link Mapping}实现，故暂时不考虑继承父类{@code MappingIgnoring}或抑制父类忽略的字段等需求
 *
 * @author benshaoye
 */
@Repeatable(MappingIgnoring.List.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingIgnoring {

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
    IgnoreMode mode() default IgnoreMode.ALL;

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        MappingIgnoring[] value() default {};
    }
}
