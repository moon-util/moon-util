package com.moon.mapping.annotation;

import com.moon.mapping.MappingUtil;
import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author moonsky
 */
@ComponentScan(basePackageClasses = MappingUtil.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface EnableBeanMapping {}
