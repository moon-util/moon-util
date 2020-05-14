package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.Evaluator;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

import java.util.Iterator;

/**
 * @author benshaoye
 */
public class MarkCollect extends MarkColumn {

    private final IterateStrategy strategy;

    private MarkCollect(int offset, Property property, MarkCollectGroup group) {
        super(offset, property, group);
        this.strategy = IterateFactory.getIterateStrategy(this);
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
    static MarkColumn collect(int offset, Property property, MarkColumnGroup group) {
        if (property.isIterated()) {
            MarkCollectGroup collectGroup = (MarkCollectGroup) group;
            if (property.isOnlyIndexer()) {
                return new OnlyIndexedCollect(offset, property, collectGroup);
            } else if (property.hasIndexer()) {
                return new IndexedCollect(offset, property, collectGroup);
            } else {
                return new MarkCollect(offset, property, collectGroup);
            }
        } else {
            return MarkColumn.column(offset, property, group);
        }
    }

    @Override
    public void renderRecord(
        MarkTask task, SheetFactory sheetFactory, RowFactory factory, Object data
    ) {
        IterateStrategy strategy = getStrategy();
        final Evaluator evaluator = getEvaluator();
        Object propertyObject = evaluator.getPropertyValue(data);
        if (propertyObject == null) {
            task.run(sheetFactory.row());
        } else {
            Iterator iterator = strategy.iterator(propertyObject);
            MarkCollectGroup group = (MarkCollectGroup) getGroup();
            int index = 0;
            int cellIndex = getOffset();
            if (group == null) {
                while (iterator.hasNext()) {
                    RowFactory rowFactory = sheetFactory.row();
                    Object iteratedObject = iterator.next();
                    task.run(rowFactory);
                    evaluator.evalOnOriginal(rowFactory.index(cellIndex), iteratedObject);
                    index++;
                }
            } else {
                while (iterator.hasNext()) {
                    Object iteratedObject = iterator.next();
                    group.renderRecord(task, sheetFactory, factory, iteratedObject);
                    index++;
                }
            }
            if (index < 1) {
                task.run(sheetFactory.row());
            }
        }
    }

    public IterateStrategy getStrategy() { return strategy; }

    /*
     * ~~~~~ implements ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static class OnlyIndexedCollect extends MarkCollect {

        public OnlyIndexedCollect(int offset, Property property, MarkCollectGroup group) {
            super(offset, property, group);
        }

        @Override
        public void renderRecord(MarkTask container, SheetFactory sheetFactory, RowFactory factory, Object data) {
            factory.index(getOffset()).val(nextIndex());
        }
    }

    private static class IndexedCollect extends MarkCollect {

        public IndexedCollect(int offset, Property property, MarkCollectGroup group) { super(offset, property, group); }

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
}
