package com.moon.office.excel.enums;

import com.moon.core.exception.NumberException;
import com.moon.core.lang.BooleanUtil;
import org.apache.poi.ss.usermodel.CellType;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public enum ValueType implements Function {
    _NONE(CellType._NONE) {
        @Override
        public Object apply(Object o) {
            return STRING.apply(o);
        }
    },

    NUMERIC(CellType.NUMERIC) {
        @Override
        public Object apply(Object o) {
            if (o instanceof Double || o instanceof BigDecimal || o instanceof Float) {
                return ((Number) o).doubleValue();
            } else if (o instanceof Number) {
                return ((Number) o).longValue();
            }
            throw new NumberException(String.valueOf(o));
        }
    },

    STRING(CellType.STRING) {
        @Override
        public Object apply(Object o) {
            return o == null ? "" : o.toString();
        }
    },

    FORMULA(CellType.FORMULA) {
        @Override
        public Object apply(Object o) {
            return null;
        }
    },

    BLANK(CellType.BLANK) {
        @Override
        public Object apply(Object o) {
            return "";
        }
    },

    BOOLEAN(CellType.BOOLEAN) {
        @Override
        public Object apply(Object o) {
            return BooleanUtil.toBoolean(o);
        }
    },

    ERROR(CellType.ERROR) {
        @Override
        public Object apply(Object o) {
            throw new UnsupportedOperationException();
        }
    };

    public final CellType TYPE;

    ValueType(CellType type) {
        TYPE = type;
    }
}
