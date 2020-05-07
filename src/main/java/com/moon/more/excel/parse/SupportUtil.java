package com.moon.more.excel.parse;

import com.moon.core.lang.ThrowUtil;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableListable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author benshaoye
 */
final class SupportUtil {

    private final static String COL_NAME = "@" + TableColumn.class.getSimpleName();

    private final static String FLAT_NAME = "@" + TableColumnFlatten.class.getSimpleName();
    private final static String LISTABLE = "@" + TableListable.class.getSimpleName();

    private final static String NOT_ALLOWED = COL_NAME + " & " + FLAT_NAME + " 不能用于同一字段: {}: \n\t\t[ " +

        COL_NAME + " ] 用于普通数据字段（int、double、String、BigDecimal）；\n\t\t[ " +

        FLAT_NAME + " ] 用于复合字段（里面有普通字段组成）。\n\n";

    private SupportUtil() { ThrowUtil.noInstanceError(); }

    static <T> T dftIfNull(T v, T dft) { return v == null ? dft : v; }

    static String getNotAllowed(String prop) { return NOT_ALLOWED.replace("{}", prop); }

    /**
     * 不能在同一个字段上同时注解{@link TableColumn}和{@link TableColumnFlatten}
     *
     * @param col
     * @param flat
     * @param prop
     */
    static void requireNotDuplicated(TableColumn col, TableColumnFlatten flat, String prop) {
        if (col != null && flat != null) {
            throw new IllegalStateException(getNotAllowed(prop));
        }
    }

    /**
     * 同一个实例，不能在多个字段上注解{@link TableListable}
     *
     * @param list
     * @param <T>
     */
    static <T extends Property> void requireNotDuplicatedListable(List<T> list) {
        boolean isListable = false;
        for (T defined : list) {
            if (defined.isCanListable()) {
                if (isListable) {
                    throw new IllegalStateException("一个实体最多只能有一个字段注解 " + LISTABLE);
                } else {
                    isListable = true;
                }
            }
        }
    }

    static Type getActual(Type type) { return getActual(type, 0); }

    static Type getActual(Type type, int index) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            return pType.getActualTypeArguments()[index];
        }
        return null;
    }
}
