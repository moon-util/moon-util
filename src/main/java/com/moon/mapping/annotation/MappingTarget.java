package com.moon.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 【未实现】映射目标
 * <p>
 * 结合{@link MappingComponent}使用，映射目标对象可以是方法参数的某一对象，
 * 此时将这个对象注解{@link MappingTarget}即可，注：
 * <ul>
 *     <li>1. 一个方法最多只能有一个{@link MappingTarget}</li>
 *     <li>2. 多参数方法一般建议返回值是{@code void}参数列表中某一唯一类型</li>
 * </ul>
 * 例：
 * <pre>
 * &#64;MappingComponent
 * public interface MappingMapper {
 *
 *     // 正确。没有返回值
 *     void personalEntity2VO(PersonalEntity entity, &#64;MappingTarget PersonalVO vo);
 *
 *     // 正确。返回{@link MappingTarget}注解的对象
 *     UserVO userEntity2VO(UserEntity entity, &#64;MappingTarget UserVO vo);
 *
 *     // 正确。返回参数列表中某唯一类型对应的对象
 *     EmployeeEntity emp2VO(EmployeeEntity entity, &#64;MappingTarget EmployeeVO vo);
 *
 *     // 错误。参数列表中有多个{@code CompanyEntity}
 *     CompanyEntity company2VO(CompanyEntity company1, CompanyEntity company2, &#64;MappingTarget CompanyVO vo);
 * }
 * </pre>
 * @author benshaoye
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
@interface MappingTarget {}
