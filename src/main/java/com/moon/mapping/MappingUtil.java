package com.moon.mapping;

import com.moon.mapping.annotation.MappingFor;

import static com.moon.mapping.Mappings.classAs;
import static java.lang.Thread.currentThread;

/**
 * 获取两个类之间的映射，获取不到时会抛出异常{@link NoSuchMappingException}
 * <p>
 * 推荐获取后用静态变量的方式保存，因为没必要获取多次
 * <p>
 * 如果两个了之间没有用{@link MappingFor}关联映射关系，就会获取不到，并抛出异常，
 * 此时可能中断启动过程
 *
 * @author moonsky
 */
@SuppressWarnings("all")
public abstract class MappingUtil {

    private MappingUtil() { }

    /**
     * 获取当前类注解{@link MappingFor#value()}的第一个映射，如：
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
     * &#64;MappingFor({Bus.class, Car.class})
     * public class Auto {
     *
     *     // thisXxx 方法只能在注解了 {@link MappingFor}的类使用
     *     // thisPrimary 获取第一个映射
     *     final static BeanMapping&lt;Auto, Bus&gt; = MappingUtil.thisPrimary();
     *     // 不是第一个映射这样获取: {@link #thisMappingFor(Class)}
     *     final static BeanMapping&lt;Auto, Car&gt; = MappingUtil.thisMappingFor(Car.class);
     *     // 所有声明的映射都可以这样获取: {@link #resolve(Class, Class)}
     *     // 这个方法不像{@code thisXxx}方法, 可以在任意位置使用
     *     final static BeanMapping&lt;Auto, Car&gt; = MappingUtil.get(Auto.class, Car.class);
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
     * @throws NoSuchMappingException 不存在对应的映射器是抛出异常
     */
    public static <THIS, THAT> BeanMapping<THIS, THAT> thisPrimary() {
        Class<THIS> thisClass = classAs(currentThread().getStackTrace()[2].getClassName());
        MappingFor mappingFor = thisClass.getAnnotation(MappingFor.class);
        return get(thisClass, (Class<THAT>) mappingFor.value()[0]);
    }

    /**
     * 当前类{@code THIS}到目标类{@code thatClass}的映射器
     * <p>
     * 要求{@code thatClass}在当前类注解{@link MappingFor#value()}中
     *
     * @param thatClass 目标类
     * @param <THIS>    当前类
     * @param <THAT>    目标类
     *
     * @return 映射器
     *
     * @throws NoSuchMappingException 不存在对应的映射器时抛出异常
     */
    public static <THIS, THAT> BeanMapping<THIS, THAT> thisMappingFor(Class<THAT> thatClass) {
        return get(classAs(currentThread().getStackTrace()[2].getClassName()), thatClass);
    }

    /**
     * 获取从类{@code fromClass}到{@code toClass}的映射器;
     * <p>
     * {@code toClass}必须存在于{@code fromClass}的注解{@link MappingFor#value()}声明中
     *
     * @param fromClass 数据源类
     * @param toClass   应声明在{@code fromClass}注解的{@link MappingFor#value()}中
     *
     * @return 当存在时返回对应映射器
     *
     * @throws NoSuchMappingException 不存在对应的映射器时抛出异常
     * @see MappingFor#value()
     */
    public static <F, T> BeanMapping<F, T> get(Class<F> fromClass, Class<T> toClass) {
        return Mappings.resolve(fromClass, toClass);
    }
}
