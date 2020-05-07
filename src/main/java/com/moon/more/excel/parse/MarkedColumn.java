package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;
import com.moon.more.excel.annotation.TableListable;

import java.lang.reflect.Type;

/**
 * @author benshaoye
 */
public class MarkedColumn {

    private final int offset;
    /** property name */
    private final String name;
    /** property type */
    private final Class type;
    private final TableColumn tableColumn;
    private final TableColumnFlatten columnFlatten;
    private final TableListable tableListable;
    private final TableIndexer tableIndexer;

    private final Type actualType;
    private final Class actualClass;
    private final MarkedColumnGroup group;

    public MarkedColumn(
        int offset,
        String name,
        Class type,
        Type actualType,
        TableColumn tableColumn,
        TableColumnFlatten columnFlatten,
        TableListable tableListable,
        TableIndexer tableIndexer,
        MarkedColumnGroup group
    ) {
        Class actualCls = actualType instanceof Class ? (Class) actualType : null;
        this.offset = offset;
        this.name = name;
        this.type = type;
        this.actualType = actualType;
        this.actualClass = actualCls;
        this.tableColumn = tableColumn;
        this.columnFlatten = columnFlatten;
        this.tableListable = tableListable;
        this.tableIndexer = tableIndexer;
        this.group = group;
    }

    public int getOffset() { return offset; }

    public String getName() { return name; }

    public Class getType() { return type; }

    public TableColumn getTableColumn() { return tableColumn; }

    public TableColumnFlatten getColumnFlatten() { return columnFlatten; }

    public TableListable getTableListable() { return tableListable; }

    public TableIndexer getTableIndexer() { return tableIndexer; }

    public Type getActualType() { return actualType; }

    public Class getActualClass() { return actualClass; }

    public MarkedColumnGroup getGroup() { return group; }
}
