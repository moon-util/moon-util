package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.Evaluator;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;
import com.moon.more.excel.annotation.TableColumn;
import com.moon.more.excel.annotation.TableColumnFlatten;
import com.moon.more.excel.annotation.TableIndexer;
import com.moon.more.excel.annotation.TableListable;

import java.lang.reflect.Type;

/**
 * @author benshaoye
 */
abstract class AbstractMark<G extends MarkRenderer> extends AbstractSupporter {

    private final int offset;
    /** property name */
    private final String name;
    /** property type */
    private final Class type;
    private final boolean iterated;

    private final TableColumn tableColumn;
    private final TableColumnFlatten columnFlatten;
    private final TableListable tableListable;
    private final Evaluator evaluator;

    private final Type genericType;
    private final Class actualClass;
    private final G group;

    private final TableIndexer tableIndexer;
    private final IntAccessor indexer;

    protected AbstractMark(int offset, Property property, G group) {
        this.offset = offset;
        this.group = group;

        this.name = property.getName();
        this.type = property.getPropertyType();
        this.evaluator = property.getEvaluator();
        this.tableColumn = property.getColumn();
        this.columnFlatten = property.getFlatten();
        this.tableListable = property.getListable();
        this.iterated = property.isIterated();

        Type typ = property.getGenericType();
        Class cls = property.getActualClass();
        cls = cls == null ? (typ instanceof Class ? (Class) typ : null) : cls;
        this.genericType = typ;
        this.actualClass = cls;

        // init indexer
        TableIndexer tableIndexer = property.getIndexer();
        this.tableIndexer = tableIndexer;
        this.indexer = tableIndexer == null ? null : IntAccessor.of();
    }

    protected void resetGroup() {
        MarkRenderer group = getGroup();
        if (group != null) {
            group.resetAll();
        }
    }

    protected void resetIndexer() {
        IntAccessor indexer = this.getIndexer();
        if (indexer != null) {
            indexer.set(getTableIndexer().startingAt());
        }
    }

    /*
     * ~~~~~ getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public G getGroup() { return group; }

    protected TableIndexer getTableIndexer() { return tableIndexer; }

    protected IntAccessor getIndexer() { return indexer; }

    protected Evaluator getEvaluator() { return evaluator; }

    protected int getOffset() { return offset; }

    protected boolean isIterated() { return iterated; }

    @SuppressWarnings({"rawtypes"})
    protected Class getPropertyType() { return type; }

    protected String getPropertyName() { return name; }

    /**
     * 这里可能会报NPE，但由于是不同实现下才调，所以没有加 null 判断
     *
     * @see #renderRecord(MarkTask, SheetFactory, RowFactory, Object)
     */
    @SuppressWarnings("all")
    protected int nextIndex() { return getIndexer().getAndIncrement(getTableIndexer().step()); }
}
