package com.moon.mapping.annotation;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 聚合{@code Mapping}组件
 * <p>
 * 将多个无关的映射类通过同一个接口达到访问目的，如：
 * <pre>
 * &#64;BeanMapper
 * public interface MappingMapper {
 *
 *     // 将多个不同类型的转换器声明在同一个接口里
 *     // 避免通过{@link MappingUtil#get(Class, Class)}获取每一个映射器
 *     // 一定程度简化代码
 *     // 通过这种方式会为每个关联的{@code POJO}实现一个映射器接口{@link BeanMapping}
 *     PersonalInfoVO personalEntity2VO(PersonalEntity entity);
 *
 *     EmployeeVO employeeEntity2VO(EmployeeEntity emp);
 *
 *     AccountVO accountEntity2VO(AccountEntity user);
 *
 *     // ...
 * }
 * </pre>
 *
 * @author benshaoye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface MappingComponent {

    /**
     * 实现类后缀
     *
     * @return 接口实现类后缀
     */
    String suffix() default "Impl";
}
