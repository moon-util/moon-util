package com.moon.data.jdbc.annotation;

import com.moon.data.IdentifierGenerator;
import com.moon.data.jdbc.identifier.IdentifierInjector;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 * @see IdentifierGenerator
 * @see IdentifierInjector
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdStrategy {

    Class<? extends IdentifierGenerator<? extends Serializable, ?>> value();
}
