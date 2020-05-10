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

    private static String getNotAllowed(String prop) { return NOT_ALLOWED.replace("{}", prop); }

    private SupportUtil() { ThrowUtil.noInstanceError(); }

    static <T> T dftIfNull(T v, T dft) { return v == null ? dft : v; }

    /**
     * 同一字段不能同时注解{@link TableColumn}和{@link TableColumnFlatten}
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
     * 同一个实例，不能有多个字段注解{@link TableListable}
     *
     * @param list
     * @param <T>
     */
    static <T extends Property> void requireNotDuplicatedListable(List<T> list) {
        Property property = null;
        for (T defined : list) {
            if (defined.isIterated() || defined.isChildrenIterated()) {
                if (property == null) {
                    property = defined;
                } else {
                    throw new IllegalStateException("一个类最多只能有一个字段可迭代(展开字段内部包含可迭代字段也视为可迭代)" +
                        ";\n " +
                        property.getDeclaringClass() +
                        ": [" +
                        toErrMsg(property) +
                        toErrMsg(defined) +
                        "\n ]\n");
                }
            }
        }
    }

    private static String toErrMsg(Property prop) {
        return "\n  ~ " + prop.getPropertyType().getSimpleName() + " " +

            prop.name + ": " + (prop.isIterated() ? "自身可迭代;" : "内部包含可迭代字段;");
    }
}
