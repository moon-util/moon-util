package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.Renderer;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

import java.util.Iterator;
import java.util.List;

import static com.moon.core.util.CollectUtil.toArrayOfEmpty;

/**
 * @author benshaoye
 */
public class MarkIteratedGroup implements MarkRenderer, Renderer {

    private final MarkIterated[] columns;

    private final MarkColumn rootIndexer;

    private final MarkIterated iterateAt;

    private final DetailRoot root;

    /**
     * 标记{@link #columns}中是否有索引字段
     * <p>
     * 用于提升性能，避免每次都循环
     *
     * @see CoreParser#transform(PropertiesGroup, IntAccessor)
     */
    @SuppressWarnings("all")
    private final boolean indexed;

    private final IterateStrategy strategy;

    /**
     * @param columns
     * @param iterateAt   如果不为 null，它是 columns 中的一项
     * @param rootIndexer null 或者索引，不属于 columns 中
     * @param root
     * @param indexed     columns 中是否包含一项或多项“索引”
     *
     * @see CoreParser#transform(PropertiesGroup, IntAccessor)
     */
    @SuppressWarnings("all")
    public MarkIteratedGroup(
        List<MarkIterated> columns, MarkIterated iterateAt, MarkColumn rootIndexer, DetailRoot root, boolean indexed
    ) {
        this.strategy = IterateFactory.getIterateStrategy(iterateAt);
        this.columns = toArrayOfEmpty(columns, MarkIterated[]::new, MarkIterated.EMPTY_ARR);
        this.rootIndexer = rootIndexer;
        this.iterateAt = iterateAt;
        this.indexed = indexed;
        this.root = root;
    }

    @Override
    public final void renderHead(SheetFactory sheetFactory) {
        // todo 列表头部渲染，供外部调用，不在内部递归调用
    }

    /**
     * @param sheetFactory
     * @param iterator
     * @param first        如果不为 null，则 firstItem 是 iterator 的第一项
     */
    @Override
    @SuppressWarnings({"rawtypes"})
    public final void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) {
        resetAll();

        renderRecord(new MarkExecutor(), sheetFactory, null, first);
        while (iterator.hasNext()) {
            renderRecord(new MarkExecutor(), sheetFactory, null, iterator.next());
        }
    }

    private Object columnsData;

    @Override
    public final void renderRecord(
        MarkExecutor executor, SheetFactory sheetFactory, RowFactory factory, Object data
    ) {
        if (data == null) {
            return;
        }
        if (canIterable()) {
            executor.add(rowFactory -> {
                for (MarkIterated column : columns) {
                    column.renderRecord(executor, sheetFactory, rowFactory, data);
                }
            });
            MarkIterated at = getIterateAt();
            Object iterableObj = at.getEvaluator().getPropertyValue(data);
            if (iterableObj == null) {
                executor.run(sheetFactory.row());
                return;
            } else {
                int index = 0;
                Iterator iterator = strategy.iterator(iterableObj);
                while (iterator.hasNext()) {
                    Object iteratedObject = iterator.next();
                    at.renderRecord(executor, sheetFactory, factory, iteratedObject);
                    index++;
                }
                if (index < 1) {
                    executor.run(sheetFactory.row());
                }
                executor.removeFirst();
            }
        } else {
            RowFactory rowFactory = factory == null ? sheetFactory.row() : factory;
            executor.run(rowFactory);
            for (MarkIterated column : columns) {
                column.renderRecord(executor, sheetFactory, rowFactory, data);
            }
        }
    }

    /*
     * ~~~~~ initialize ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    @Override
    public void resetAll() {
        if (isIndexed()) {
            if (rootIndexer != null) {
                rootIndexer.resetAll();
            }
            for (MarkRenderer column : getColumns()) {
                column.resetAll();
            }
        }
    }

    /*
     * ~~~~~ getters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    boolean canIterable() { return getIterateAt() != null; }

    public MarkIterated getIterateAt() { return iterateAt; }

    MarkIterated[] getColumns() { return columns; }

    public boolean isIndexed() { return indexed; }

    MarkColumn getRootIndexer() { return rootIndexer; }

    private Object getColumnsData() { return columnsData; }

    private void setColumnsData(Object columnsData) { this.columnsData = columnsData; }
}
