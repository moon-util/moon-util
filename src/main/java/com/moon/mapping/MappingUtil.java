package com.moon.mapping;

import com.moon.mapping.annotation.MappingFor;

import static java.lang.Thread.currentThread;

/**
 * @author moonsky
 */
public abstract class MappingUtil {

    private MappingUtil() { }

    /**
     * 获取当前类的{@link MapMapping}
     *
     * @param <THIS> 当前类数据类型
     *
     * @return MapMapping
     */
    public static <THIS> MapMapping<THIS> thisMapping() {
        return Mappings.resolve(currentThread().getStackTrace()[2].getClassName());
    }

    public static <THIS, THAT> BeanMapping<THIS, THAT> thisPrimary() {
        Class<THIS> thisClass = Mappings.classAs(currentThread().getStackTrace()[2].getClassName());
        MappingFor mappingFor = thisClass.getAnnotation(MappingFor.class);
        return resolve(thisClass, (Class<THAT>) mappingFor.value()[0]);
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
     * @throws NoSuchMappingException 不存在对应的映射器是抛出异常，
     *                                    必须通过{@link MappingFor}或手动注册方可获取到
     * @see MappingFor#value()
     */
    public static <F, T> BeanMapping<F, T> resolve(Class<F> fromClass, Class<T> toClass) {
        return Mappings.resolve(fromClass, toClass);
    }

    public static <T> MapMapping<T> resolve(Class<T> fromClass) {
        return Mappings.resolve(fromClass);
    }
}
