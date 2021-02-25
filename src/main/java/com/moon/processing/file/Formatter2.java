package com.moon.processing.file;

import com.moon.processing.util.Element2;
import com.moon.processing.util.String2;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;

/**
 * @author benshaoye
 */
public enum Formatter2 {
    ;

    public static String with(String template, Object... types) {
        return String2.format(template, Arrays.stream(types).map(type -> {
            if (type instanceof Class<?>) {
                return ((Class<?>) type).getCanonicalName();
            }
            if (type instanceof TypeElement) {
                return Element2.getQualifiedName((TypeElement) type);
            }
            return String.valueOf(type);
        }).toArray());
    }
}
