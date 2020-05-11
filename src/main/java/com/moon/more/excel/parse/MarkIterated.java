package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.Evaluator;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

/**
 * @author benshaoye
 */
public class MarkIterated extends AbstractMark implements MarkRenderer {

    private MarkIterated(int offset, Property property, MarkRenderer group) {
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
    static MarkIterated of(int offset, Property property, MarkRenderer group) {
        if (property.isOnlyIndexer()) {
            return new OnlyIndexedIterated(offset, property, group);
        } else if (property.hasIndexer()) {
            return new IndexedIterated(offset, property, group);
        } else {
            return new MarkIterated(offset, property, group);
        }
    }

    @Override
    public void renderRecord(MarkExecutor container, SheetFactory sheetFactory, RowFactory factory, Object data) {
        if (this.isIterated()) {
            getEvaluator().evalOnOriginal(factory.index(getOffset()), data);
        }else{
            getEvaluator().eval(factory.index(getOffset()), data);
        }
    }

    /*
     * ~~~~~ implements ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static class OnlyIndexedIterated extends MarkIterated {

        public OnlyIndexedIterated(int offset, Property property, MarkRenderer group) { super(offset, property, group); }

        @Override
        public void renderRecord(MarkExecutor container, SheetFactory sheetFactory, RowFactory factory, Object data) {
            factory.index(getOffset()).val(nextIndex());
        }
    }

    private static class IndexedIterated extends MarkIterated {

        public IndexedIterated(int offset, Property property, MarkRenderer group) { super(offset, property, group); }

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

    /*
     * ~~~~~ initialize ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    @Override
    public void resetAll() {
        // resetIndexer();
        // resetGroup();
    }
}
