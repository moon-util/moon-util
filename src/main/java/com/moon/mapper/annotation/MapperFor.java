package com.moon.mapper.annotation;

import com.moon.mapper.BeanConverter;
import com.moon.mapper.MapperUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注册映射器
 * <p>
 * 将{@code MapperFor}注册在普通 PO、BO、VO、DO、Entity、Model 上, 并指定映射目标
 * 映射目标同样是一个 PO、DO 或其他数据模型
 * <p>
 * 运行时可以通过{@link MapperUtil}获取相应映射。如下:
 *
 * <pre>
 * public class CarEntity {
 *     private String id;
 *     private String name;
 *     private Integer kilometers;
 *     // ...
 * }
 *
 * &#64;MapperFor({CarEntity.class})
 * public class CarVO {
 *    private String id;
 *    private String name;
 *    // 自动类型转换
 *    private String kilometers;
 * }
 *
 * public class DemoApplication {
 *
 *     public static void main(String[] args) {
 *         // 获取映射器 {@link MapperUtil#get(Class, Class)}
 *         BeanMapper&lt;CarVO, CarEntity&gt; mapping = MapperUtil.get(CarVO.class, CarEntity.class);
 *     }
 * }
 * </pre>
 *
 * @author moonsky
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapperFor {

    Class<?>[] value();

    /**
     * 是否支持转换器
     * <p>
     * 当{@code converter}为 true 时，转换双方都不能是接口或抽象类，因为不能实例化
     * <p>
     * 如果{@link #value()}中包含接口/抽象类，或{@link MapperFor}所注解的是接口/抽象类
     * 那么{@code converter}必须是{@code false}
     * <p>
     * 这个属性在编译时就发现{@link MapperUtil#getConverter(Class, Class)}是否可用，而不是在运行时
     * 才发现程序不能运行
     *
     * @return 是否支持转换器
     *
     * @see BeanConverter
     */
    boolean converter() default true;
}
