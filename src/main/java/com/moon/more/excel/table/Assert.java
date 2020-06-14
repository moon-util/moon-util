package com.moon.more.excel.table;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnGroup;

/**
 * @author benshaoye
 */
final class Assert {

    private final static String COLUMN_NAME = "@" + TableColumn.class.getSimpleName();

    private final static String GROUP_NAME = "@" + TableColumnGroup.class.getSimpleName();

    private final static String NOT_ALLOWED = COLUMN_NAME + " & " + GROUP_NAME + " 不能用于同一字段: {} (包括对应的 getter | setter): \n\t\t[ " +

        COLUMN_NAME + " ] 用于普通数据字段（int、double、String、BigDecimal 等；注解在复合字段上将视为 String 最终调用 toString() 方法）；\n\t\t[ " +

        GROUP_NAME + " ] 用于实体字段（里面由普通字段组成）。\n\n";


    private static String getNotAllowed(String prop) { return NOT_ALLOWED.replace("{}", prop); }

    static void notDuplicated(Descriptor descriptor) {
        TableColumn column = descriptor.getTableColumn();
        TableColumnGroup group = descriptor.getTableColumnGroup();
        notDuplicated(column, group, descriptor.getName());
    }

    static void notDuplicated(TableColumn column, TableColumnGroup group, String propertyName) {
        if (column != null && group != null) {
            throw new IllegalStateException(getNotAllowed(propertyName));
        }
    }
}
