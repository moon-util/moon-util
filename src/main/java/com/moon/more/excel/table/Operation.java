package com.moon.more.excel.table;

import com.moon.more.excel.CellFactory;

/**
 * @author benshaoye
 */
public interface Operation {

    void operate(CellFactory factory, Object data);
}
