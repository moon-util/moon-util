package com.moon.accessor.annotation.link;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author benshaoye
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface OR {}
