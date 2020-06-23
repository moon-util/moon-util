package com.moon.core.enums;

import com.moon.core.lang.StringUtil;
import com.moon.core.util.Table;
import com.moon.core.util.TableImpl;

/**
 * @author benshaoye
 */
public enum Converters {
    /**
     * to string
     */
    toString {
        @Override
        public Object convertTo(Object value) { return StringUtil.stringify(value); }
    },
    string2Boolean {
        @Override
        public Object convertTo(Object value) {
            return Boolean.valueOf(StringUtil.stringify(value));
        }
    },
    number2BooleanOfPrimitive {
        @Override
        public Object convertTo(Object value) {
            return ((Number) value).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
        }
    },
    number2Boolean {
        @Override
        public Object convertTo(Object value) {
            return value == null ? Boolean.FALSE : number2BooleanOfPrimitive.convertTo(value);
        }
    },

    ;

    private final static class Cached {

        final static Table cached = TableImpl.newHashTable();
    }

    public abstract Object convertTo(Object value);
}
