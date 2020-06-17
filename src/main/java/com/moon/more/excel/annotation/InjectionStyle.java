package com.moon.more.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author benshaoye
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectionStyle {

    Class[] value() default {};
}
