package com.moon.mapping;

import com.moon.mapping.annotation.MappingFor;

import static com.moon.mapping.Mappings.classAs;
import static java.lang.Thread.currentThread;

/**
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
     *     // thisPrimary 获取第一个映射
     *     final static BeanMapping&lt;Auto, Bus&gt; = MappingUtil.thisPrimary();
     *     // 不是第一个映射这样获取
     *     final static BeanMapping&lt;Auto, Car&gt; = MappingUtil.thisMappingFor(Car.class);
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
        return resolve(thisClass, (Class<THAT>) mappingFor.value()[0]);
    }

    /**
     * 当前类{@code THIS}到目标类{@code thatClass}的映射器
     * <p>
     * 要求{@code thisClass}在当前类注解{@link MappingFor#value()}中
     *
     * @param thatClass 目标类
     * @param <THIS>    当前类
     * @param <THAT>    目标类
     *
     * @return 映射器
     *
     * @throws NoSuchMappingException 不存在对应的映射器是抛出异常
     */
    public static <THIS, THAT> BeanMapping<THIS, THAT> thisMappingFor(Class<THAT> thatClass) {
        return resolve(classAs(currentThread().getStackTrace()[2].getClassName()), thatClass);
    }

    /**
     * 获取从类{@code fromClass}到{@code toClass}的映射器;
     * <p>
     * {@code toClass}必须存在于{@code fromClass}的注解{@link MappingFor#value()}声明中或手动注册
     *
     * @param fromClass 数据源类
     * @param toClass   应声明在{@code fromClass}注解的{@link MappingFor#value()}中
     *
     * @return 当存在时返回对应映射器
     *
     * @throws NoSuchMappingException 不存在对应的映射器是抛出异常
     * @see MappingFor#value()
     */
    public static <F, T> BeanMapping<F, T> resolve(Class<F> fromClass, Class<T> toClass) {
        return Mappings.resolve(fromClass, toClass);
    }
}
