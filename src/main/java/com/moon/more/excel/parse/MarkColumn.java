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
import java.util.Iterator;

/**
 * @author benshaoye
 */
public class MarkColumn extends AbstractSupporter implements MarkRenderer {

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

    private final Type actualType;
    private final Class actualClass;
    private final MarkRenderer group;

    private final TableIndexer tableIndexer;
    private final IntAccessor indexer;

    private MarkColumn(int offset, Property property, MarkRenderer group) {
        this.offset = offset;
        this.group = group;

        this.name = property.getName();
        this.type = property.getPropertyType();
        this.evaluator = property.getEvaluator();
        this.tableColumn = property.getColumn();
        this.columnFlatten = property.getFlatten();
        this.tableListable = property.getListable();
        this.iterated = property.isIterated();

        Type actualType = property.getActualType();
        Class actualCls = actualType instanceof Class ? (Class) actualType : null;
        this.actualType = actualType;
        this.actualClass = actualCls;

        // init indexer
        TableIndexer tableIndexer = property.getIndexer();
        this.tableIndexer = tableIndexer;
        this.indexer = tableIndexer == null ? null : IntAccessor.of();
    }

    /**
     * @param offset
     * @param property
     * @param group
     *
     * @return
     *
     * @see CoreParser#transform(PropertiesGroup, IntAccessor)
     */
    @SuppressWarnings("all")
    static MarkColumn of(int offset, Property property, MarkRenderer group) {
        if (property.isOnlyIndexer()) {
            return new OnlyIndexedColumn(offset, property, group);
        } else if (property.hasIndexer()) {
            return new IndexedColumn(offset, property, group);
        } else {
            return new MarkColumn(offset, property, group);
        }
    }

    @Override
    public void renderRecord(MarkContainer container, SheetFactory sheetFactory, RowFactory factory, Object data) {
        MarkRenderer childrenGroup = getGroup();
        Evaluator evaluator = getEvaluator();
        if (childrenGroup != null) {
            childrenGroup.renderRecord(container, sheetFactory, factory, evaluator.getPropertyValue(data));
        } else {
            container.execute(factory);
            evaluator.eval(factory.index(getOffset()), data);
        }
    }

    /*
     * ~~~~~ implements ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static class OnlyIndexedColumn extends MarkColumn {

        public OnlyIndexedColumn(int offset, Property property, MarkRenderer group) { super(offset, property, group); }

        @Override
        public void renderRecord(MarkContainer container, SheetFactory sheetFactory, RowFactory factory, Object data) {
            factory.index(getOffset()).val(nextIndex());
        }
    }

    private static class IndexedColumn extends MarkColumn {

        public IndexedColumn(int offset, Property property, MarkRenderer group) { super(offset, property, group); }

        @Override
        public void renderRecord(MarkContainer container, SheetFactory sheetFactory, RowFactory factory, Object data) {
            final int offset = this.getOffset();
            Evaluator evaluator = getEvaluator();
            MarkRenderer childrenGroup = getGroup();
            factory.index(offset).val(nextIndex());
            if (childrenGroup != null) {
                childrenGroup.renderRecord(container, sheetFactory, factory, evaluator.getPropertyValue(data));
            } else {

                evaluator.eval(factory.index(offset + 1), data);
            }
        }
    }

    /*
     * ~~~~~ initialize ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    @Override
    public void renderHead(SheetFactory sheetFactory) {
        // TODO 表头
    }

    @Override
    public void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) {
        // TODO 数据
    }

    @Override
    public void resetAll() {
        resetIndexer();
        resetGroup();
    }

    private void resetGroup() {
        MarkRenderer group = getGroup();
        if (group != null) {
            group.resetAll();
        }
    }

    private void resetIndexer() {
        IntAccessor indexer = this.getIndexer();
        if (indexer != null) {
            indexer.set(tableIndexer.startingAt());
        }
    }

    /*
     * ~~~~~ getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public MarkRenderer getGroup() { return group; }

    protected TableIndexer getTableIndexer() { return tableIndexer; }

    protected IntAccessor getIndexer() { return indexer; }

    protected Evaluator getEvaluator() { return evaluator; }

    protected int getOffset() { return offset; }

    protected boolean isIterated() { return iterated; }

    /**
     * 这里可能会报NPE，但由于是不同实现下才调，所以没有加 null 判断
     *
     * @see #renderRecord(MarkContainer, SheetFactory, RowFactory, Object)
     */
    protected int nextIndex() { return getIndexer().getAndIncrement(getTableIndexer().step()); }
}
