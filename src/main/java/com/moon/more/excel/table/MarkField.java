package com.moon.more.excel.table;

import java.lang.reflect.Field;

/**
 * @author benshaoye
 */
class MarkField extends Marked<Field> {

    protected MarkField(Field field) {
        super(field.getName(), field.getType(), field);
    }
}
