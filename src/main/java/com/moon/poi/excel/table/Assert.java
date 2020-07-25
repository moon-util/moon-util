package com.moon.poi.excel.table;

// import com.moon.more.excel.annotation.FieldTransform;
// import com.moon.more.excel.annotation.TableColumn;
// import com.moon.more.excel.annotation.TableColumnGroup;
// import com.moon.more.excel.annotation.defaults.DefaultValue;

import com.moon.core.util.DateUtil;
import com.moon.poi.excel.annotation.TableColumn;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.Set;

/**
 * @author moonsky
 */
final class Assert {

    // private final static String DFT_VAL = "@" + DefaultValue.class.getSimpleName();

    // private final static String TRANSFORM = "@" + FieldTransform.class.getSimpleName();

    // private final static String COLUMN_NAME = "@" + TableColumn.class.getSimpleName();

    // private final static String GROUP_NAME = "@" + TableColumnGroup.class.getSimpleName();

    // private final static String NOT_ALLOWED = COLUMN_NAME +
    //     " & " +
    //     GROUP_NAME +
    //     " 不能用于同一字段: {} (包括对应的 getter | setter): \n\t\t[ " +
    //
    //     COLUMN_NAME +
    //     " ] 用于普通数据字段（int、double、String、BigDecimal 等；注解在复合字段上将视为 String 最终调用 toString() 方法）；\n\t\t[ " +
    //
    //     GROUP_NAME +
    //     " ] 用于实体字段（里面由普通字段组成）。\n\n";

    // private final static String NOT_SUPPORT = "暂不支持 " + DFT_VAL + " 和 " + TRANSFORM +
    //
    //     " \n\t\t为被 " + GROUP_NAME + " 注解的字段提供默认值或转换: {};";

    // static void notDuplicated(Descriptor descriptor) {
    //     TableColumn column = descriptor.getTableColumn();
    //     TableColumnGroup group = descriptor.getTableColumnGroup();
    //     notDuplicated(column, group, descriptor.getName());
    // }

    // static void notDuplicated(TableColumn column, TableColumnGroup group, String propertyName) {
    //     if (column != null && group != null) {
    //         throw new IllegalStateException(NOT_ALLOWED.replace("{}", propertyName));
    //     }
    // }

    final static void notDuplicated(Marked onMethod, Marked onField) {
        TableColumn mCol = onMethod == null ? null : onMethod.getTableColumn();
        TableColumn fCol = onField == null ? null : onField.getTableColumn();
        if (mCol != null && fCol != null) {
            throw new IllegalStateException("@" + TableColumn.class.getSimpleName() + " 不能同时注解在 getter/setter 方法和字段上");
        }
    }

    private final static Set<Class> NUMBER_CLASSES = new HashSet<>();

    static {
        NUMBER_CLASSES.add(byte.class);
        NUMBER_CLASSES.add(short.class);
        NUMBER_CLASSES.add(int.class);
        NUMBER_CLASSES.add(long.class);
        NUMBER_CLASSES.add(float.class);
        NUMBER_CLASSES.add(double.class);
    }

    static boolean isNumberType(Class type) {
        return Number.class.isAssignableFrom(type) || NUMBER_CLASSES.contains(type);
    }

    static boolean isImportedJodaTime() { return DateUtil.isImportedJodaTime(); }
}
