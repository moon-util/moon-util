package com.moon.processing.file;

import com.moon.processor.utils.String2;

/**
 * @author benshaoye
 */
public enum Type2 {
    ;

    public static String format(String typeTemplate, Object... types) {
        if (types == null) {
            return typeTemplate;
        }
        String[] typesStringify = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            Object type = types[i];
            if (type instanceof Class<?>) {
                typesStringify[i] = ((Class<?>) type).getCanonicalName();
            } else {
                typesStringify[i] = String.valueOf(type);
            }
        }
        return String2.format(typeTemplate, typesStringify);
    }
}
