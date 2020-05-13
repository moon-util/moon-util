package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.Evaluator;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

/**
 * @author benshaoye
 */
public class MarkCollect extends MarkColumn {

    private MarkCollect(int offset, Property property, MarkCollectGroup group) {
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
    static MarkColumn of(int offset, Property property, MarkCollectGroup group) {
        if (property.isIterated()) {
            if (property.isOnlyIndexer()) {
                return new OnlyIndexedCollect(offset, property, group);
            } else if (property.hasIndexer()) {
                return new IndexedCollect(offset, property, group);
            } else {
                return new MarkCollect(offset, property, group);
            }
        } else {
            return MarkColumn.of(offset, property, group);
        }
    }

    @Override
    public void renderRecord(
        MarkExecutor container, SheetFactory sheetFactory, RowFactory factory, Object data
    ) {
        super.renderRecord(container, sheetFactory, factory, data);
    }



    /*
     * ~~~~~ implements ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static class OnlyIndexedCollect extends MarkCollect {

        public OnlyIndexedCollect(int offset, Property property, MarkCollectGroup group) {
            super(offset, property, group);
        }

        @Override
        public void renderRecord(MarkExecutor container, SheetFactory sheetFactory, RowFactory factory, Object data) {
            factory.index(getOffset()).val(nextIndex());
        }
    }

    private static class IndexedCollect extends MarkCollect {

        public IndexedCollect(int offset, Property property, MarkCollectGroup group) { super(offset, property, group); }

        @Override
        public void renderRecord(MarkExecutor container, SheetFactory sheetFactory, RowFactory factory, Object data) {
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
}
