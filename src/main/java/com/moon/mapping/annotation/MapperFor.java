package com.moon.mapping.annotation;

import com.moon.mapping.MapperUtil;

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
 *         BeanMapping&lt;CarVO, CarEntity&gt; mapping = MappingUtil.get(CarVO.class, CarEntity.class);
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
}
