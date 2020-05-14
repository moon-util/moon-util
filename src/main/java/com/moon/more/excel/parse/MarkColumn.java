package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.Evaluator;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

/**
 * @author benshaoye
 */
public class MarkColumn extends AbstractMark<MarkColumnGroup> implements MarkRenderer {

    protected MarkColumn(int offset, Property property, MarkColumnGroup group) {
        super(offset, property, group);
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
    static MarkColumn column(int offset, Property property, MarkColumnGroup group) {
        if (property.isOnlyIndexer()) {
            return new OnlyIndexedColumn(offset, property, group);
        } else if (property.hasIndexer()) {
            return new IndexedColumn(offset, property, group);
        } else {
            return new MarkColumn(offset, property, group);
        }
    }

    @Override
    public void renderRecord(MarkTask task, SheetFactory sheetFactory, RowFactory factory, Object data) {
        MarkRenderer childrenGroup = getGroup();
        Evaluator evaluator = getEvaluator();
        if (childrenGroup != null) {
            childrenGroup.renderRecord(task, sheetFactory, factory, evaluator.getPropertyValue(data));
        } else {
            evaluator.eval(factory.index(getOffset()), data);
        }
    }

    @Override
    public void resetAll() {
        resetIndexer();
        resetGroup();
    }

    /*
     * ~~~~~ implements ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static class OnlyIndexedColumn extends MarkColumn {

        public OnlyIndexedColumn(int offset, Property property, MarkColumnGroup group) { super(offset, property, group); }

        @Override
        public void renderRecord(MarkTask container, SheetFactory sheetFactory, RowFactory factory, Object data) {
            factory.index(getOffset()).val(nextIndex());
        }
    }

    private static class IndexedColumn extends MarkColumn {

        public IndexedColumn(int offset, Property property, MarkColumnGroup group) { super(offset, property, group); }

        @Override
        public void renderRecord(MarkTask container, SheetFactory sheetFactory, RowFactory factory, Object data) {
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
}
