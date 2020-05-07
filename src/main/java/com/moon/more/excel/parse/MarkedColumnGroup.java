package com.moon.more.excel.parse;

import java.util.List;

/**
 * @author benshaoye
 */
public class MarkedColumnGroup {

    private final List<MarkedColumn> columns;

    public MarkedColumnGroup(List<MarkedColumn> columns) { this.columns = columns; }

    public List<MarkedColumn> getColumns() { return columns; }
}
