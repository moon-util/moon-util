package com.moon.mapper;

import com.moon.mapper.annotation.MapperFor;

import static com.moon.mapper.Mappers.classAs;
import static java.lang.Thread.currentThread;

/**
 * 获取两个类之间的映射，获取不到时会抛出异常{@link NoSuchMapperException}
 * <p>
 * 推荐获取后用静态变量的方式保存，没必要获取多次
 * <p>
 * 如果两个类之间没有用{@link MapperFor}关联映射关系，就会获取不到，并抛出异常
 *
 * @author moonsky
 */
@SuppressWarnings("all")
public abstract class MapperUtil {

    private MapperUtil() { }

    /**
     * 获取当前类注解{@link MapperFor#value()}的第一个映射，如：
     * <pre>
     * public class Car {
     *     private String name;
     *     // other fields & getter & setter
     * }
     *
     * public class Bus {
     *     private String name;
     *     // other fields & getter & setter
     * }
     *
     * &#64;MapperFor({Bus.class, Car.class})
     * public class Auto {
     *
     *     // thisXxx 方法只能在注解了 {@link MapperFor}的类使用
     *     // thisPrimary 获取第一个映射
     *     final static BeanMapper&lt;Auto, Bus&gt; = MapperUtil.thisPrimary();
     *
     *     // 不是第一个映射这样获取: {@link #thisMapperFor(Class)}
     *     final static BeanMapper&lt;Auto, Car&gt; = MapperUtil.thisMappingFor(Car.class);
     *
     *     // 所有声明的映射都可以这样获取: {@link #get(Class, Class)}
     *     // 这个方法不像{@code thisXxx}方法, 可以在任意位置使用
     *     final static BeanMapper&lt;Auto, Car&gt; = MapperUtil.get(Auto.class, Car.class);
     *
     *     private String name;
     *     // other fields & getter & setter
     * }
     * </pre>
     *
     * @param <THIS> 转换源类型
     * @param <THAT> 转换目标类型
     *
     * @return 映射器
     *
     * @throws NoSuchMapperException 不存在对应的映射器时抛出异常
     */
    public static <THIS, THAT> BeanMapper<THIS, THAT> thisPrimary() {
        Class<THIS> thisClass = classAs(currentThread().getStackTrace()[2].getClassName());
        MapperFor mapperFor = thisClass.getAnnotation(MapperFor.class);
        return get(thisClass, (Class<THAT>) mapperFor.value()[0]);
    }

    /**
     * 当前类{@code THIS}到目标类{@code thatClass}的映射器
     * <p>
     * 要求{@code thatClass}在当前类注解{@link MapperFor#value()}中
     *
     * @param thatClass 目标类
     * @param <THIS>    当前类
     * @param <THAT>    目标类
     *
     * @return 映射器
     *
     * @throws NoSuchMapperException 不存在对应的映射器时抛出异常
     */
    public static <THIS, THAT> BeanMapper<THIS, THAT> thisMapperFor(Class<THAT> thatClass) {
        return get(classAs(currentThread().getStackTrace()[2].getClassName()), thatClass);
    }

    /**
     * 获取从类{@code fromClass}到{@code toClass}的映射器;
     * <p>
     * {@code toClass}必须存在于{@code fromClass}的注解{@link MapperFor#value()}声明中
     * <p>
     * 注意:
     * <p>
     * 1. 本类虽是通过{@link #getConverter(Class, Class)}获取的，但如果强转为{@link BeanCopier}
     * 可能会抛异常，如果要使其可用需指定: {@link MapperFor#converter()}为{@code true}
     * <p>
     * 2. 如果指定了{@link MapperFor#converter()}为{@code true}，那么转换双方都可能是接口或抽象类
     *
     * @param fromClass 数据源类
     * @param toClass   应声明在{@code fromClass}注解的{@link MapperFor#value()}中
     * @param <F>       数据源类
     * @param <T>       目标类
     *
     * @return 当存在时返回对应映射器
     *
     * @throws NoSuchMapperException 不存在对应的映射器时抛出异常
     * @see MapperFor#value()
     */
    public static <F, T> BeanMapper<F, T> get(Class<F> fromClass, Class<T> toClass) {
        return getConverter(fromClass, toClass);
    }

    /**
     * 获取转换器
     * <p>
     * 只有{@code fromClass}的{@link MapperFor#converter()}为{@code true}时, 返回的
     * {@link BeanCopier#doForward(Object)}、{@link BeanCopier#doBackward(Object)}是可用的
     *
     * @param fromClass 数据源类
     * @param toClass   应声明在{@code fromClass}注解的{@link MapperFor#value()}中
     * @param <F>       数据源类
     * @param <T>       目标类
     *
     * @return 当存在时返回对应映射器
     *
     * @throws NoSuchMapperException 不存在对应的映射器时抛出异常
     * @see MapperFor#value()
     * @see MapperFor#converter()
     */
    public static <F, T> BeanMapper<F, T> getConverter(Class<F> fromClass, Class<T> toClass) {
        return Mappers.resolve(fromClass, toClass);
    }
}
