package com.moon.mapping.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 【未实现】映射数据源对象
 * <p>
 * 结合{@link MappingComponent}使用，将一个对象的数据映射到多个对象上，
 * 此时将这个数据源对象注解{@link MappingSource}即可，注：
 * <ul>
 *     <li>1. 一个方法最多只能有一个{@link MappingSource}</li>
 *     <li>2. 多参数方法一般建议返回值是{@code void}参数列表中某一唯一类型</li>
 * </ul>
 *
 * @author benshaoye
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
@interface MappingSource {}
