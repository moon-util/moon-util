package com.moon.more.excel.table;

import com.moon.core.lang.StringUtil;
import com.moon.more.excel.CellFactory;
import com.moon.more.excel.PropertyControl;

/**
 * @author benshaoye
 */
final class TableCol {

    private final String[] titles;
    private final PropertyControl control;

    TableCol(String[] titles, PropertyControl control) {
        this.control = control;
        this.titles = titles;
    }

    void render(CellFactory factory, Object data) {
        Object value = control.control(data);
        String str = StringUtil.stringify(value);
        factory.val(str);
    }
}
