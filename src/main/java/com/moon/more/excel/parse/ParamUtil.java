package com.moon.more.excel.parse;

import com.moon.core.lang.ThrowUtil;
import com.moon.more.excel.annotation.DataColumn;
import com.moon.more.excel.annotation.DataColumnFlatten;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author benshaoye
 */
final class ParamUtil {

    private final static String COL_NAME = "@" + DataColumn.class.getSimpleName();

    private final static String FLAT_NAME = "@" + DataColumnFlatten.class.getSimpleName();

    private final static String NOT_ALLOWED = COL_NAME + " & " + FLAT_NAME + " 不能用于同一字段: {}。 \n\t[ " +

        COL_NAME + " ]用于普通数据字段（int、double、String、BiGDecimal）；\n\t[ " +

        FLAT_NAME + " ]用于复合数据字段，如集合、数组、实体等。";

    private ParamUtil() { ThrowUtil.noInstanceError(); }

    static <T> T dftIfNull(T v, T dft) {
        return v == null ? dft : v;
    }

    static String getNotAllowed(String prop) {
        return NOT_ALLOWED.replace("{}", prop);
    }

    static void requireNotDuplicated(DataColumn col, DataColumnFlatten flat, String prop) {
        if (col != null && flat != null) {
            throw new IllegalStateException(getNotAllowed(prop));
        }
    }

    static Type getActual(Type type) {
        return getActual(type, 0);
    }

    static Type getActual(Type type, int index) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return pType.getActualTypeArguments()[index];
        }
        return null;
    }
}
