package com.moon.data.jdbc.annotation;

import com.moon.data.IdentifierGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdentifierStrategy {

    Class<? extends IdentifierGenerator> value();
}
