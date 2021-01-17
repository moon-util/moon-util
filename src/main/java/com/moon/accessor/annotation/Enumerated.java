package com.moon.accessor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Enumerated {

    EnumType value() default EnumType.ORDINAL;

    enum EnumType {
        /**
         * {@link Enum#name()}, {@link javax.persistence.EnumType#STRING}
         */
        NAME,
        /**
         * {@link Enum#ordinal()}, {@link javax.persistence.EnumType#ORDINAL}
         */
        ORDINAL,
        /**
         * {@link Enum#toString()}
         */
        STRING
    }
}
