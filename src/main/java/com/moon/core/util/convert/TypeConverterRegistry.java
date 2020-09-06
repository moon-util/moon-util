package com.moon.core.util.convert;

import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;
import com.moon.core.util.converter.Converter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public class TypeConverterRegistry {

    private final Table<Class, Class, Converter> converters = new TableImpl<>();
    private final Map<Class, Object> zeroValue = new HashMap<>();

    public TypeConverterRegistry() {
        zeroValue.put(char.class, (char) 0);
        zeroValue.put(boolean.class, false);
        zeroValue.put(byte.class, (byte) 0);
        zeroValue.put(short.class, (short) 0);
        zeroValue.put(int.class, 0);
        zeroValue.put(long.class, 0L);
        zeroValue.put(float.class, 0F);
        zeroValue.put(double.class, 0D);
    }

    public Converter getConverter(Class fromClass, Class toClass) {
        return converters.get(fromClass, toClass);
    }

    public <T> T convertTo(Object data, Class fromClass, Class toClass) {
        return (T) getConverter(fromClass, toClass).convertTo(data);
    }

    public <T> T convertTo(Object data, Class toClass) {
        if (data == null) {
            return toClass.isPrimitive() ? (T) zeroValue.get(toClass) : null;
        } else {
            return convertTo(data, data.getClass(), toClass);
        }
    }
}
