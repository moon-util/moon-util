package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.Evaluator;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

/**
 * @author benshaoye
 */
public class MarkIterated extends AbstractMark<MarkIteratedGroup> implements MarkRenderer {

    final static MarkIterated[] EMPTY_ARR = new MarkIterated[0];

    private MarkIterated(int offset, Property property, MarkIteratedGroup group) {
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
    static MarkIterated of(int offset, Property property, MarkIteratedGroup group) {
        if (property.isOnlyIndexer()) {
            return new OnlyIndexedIterated(offset, property, group);
        } else if (property.hasIndexer()) {
            return new IndexedIterated(offset, property, group);
        } else {
            return new MarkIterated(offset, property, group);
        }
    }

    @Override
    public void renderRecord(MarkExecutor executor, SheetFactory sheetFactory, RowFactory factory, Object data) {
        MarkRenderer childrenGroup = getGroup();
        Evaluator evaluator = getEvaluator();
        if (childrenGroup != null) {
            childrenGroup.renderRecord(executor, sheetFactory, factory, evaluator.getPropertyValue(data));
        } else {
            RowFactory rowFactory = factory == null ? sheetFactory.row() : factory;
            // executor.setIterateData(data);
            // executor.run(factory);
            executor.run(rowFactory);
            if (isIterated()) {
                evaluator.evalOnOriginal(rowFactory.index(getOffset()), data);
            } else {
                evaluator.eval(rowFactory.index(getOffset()), data);
            }
        }
    }

    void originRenderRecord(SheetFactory sheetFactory, RowFactory factory, Object data) {
        MarkRenderer childrenGroup = getGroup();
        Evaluator evaluator = getEvaluator();
        RowFactory rowFactory = factory == null ? sheetFactory.row() : factory;
        // executor.setIterateData(data);
        // executor.run(factory);
        if (isIterated()) {
            evaluator.evalOnOriginal(rowFactory.index(getOffset()), data);
        } else {
            evaluator.eval(rowFactory.index(getOffset()), data);
        }
    }

    /*
     * ~~~~~ implements ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static class OnlyIndexedIterated extends MarkIterated {

        public OnlyIndexedIterated(int offset, Property property, MarkIteratedGroup group) {
            super(offset, property, group);
        }

        @Override
        public void renderRecord(MarkExecutor container, SheetFactory sheetFactory, RowFactory factory, Object data) {
            factory.index(getOffset()).val(nextIndex());
        }
    }

    private static class IndexedIterated extends MarkIterated {

        public IndexedIterated(int offset, Property property, MarkIteratedGroup group) {
            super(offset, property, group);
        }

        @Override
        public void renderRecord(MarkExecutor executor, SheetFactory sheetFactory, RowFactory factory, Object data) {
            final int offset = this.getOffset();
            Evaluator evaluator = getEvaluator();
            MarkRenderer childrenGroup = getGroup();
            factory.index(offset).val(nextIndex());
            if (childrenGroup != null) {
                childrenGroup.renderRecord(executor, sheetFactory, factory, evaluator.getPropertyValue(data));
            } else {
                executor.run(factory);
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

    boolean hasGroup() {
        return getGroup() != null;
    }

    boolean canIterable() {
        MarkIteratedGroup group = getGroup();
        return group != null && getGroup().canIterable();
    }
}
