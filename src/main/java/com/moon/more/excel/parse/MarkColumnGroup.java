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
public class MarkColumnGroup implements MarkRenderer, Renderer {

    private final MarkRenderer[] columns;

    private final MarkColumn rootIndexer;

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

    /**
     * @param columns
     * @param iterableAt  如果不为 null，它是 columns 中的一项
     * @param rootIndexer null，或者索引，不属于 columns 中
     * @param root
     * @param indexed     columns 中是否包含一项或多项“索引”
     *
     * @see CoreParser#transform(PropertiesGroup, IntAccessor)
     */
    @SuppressWarnings("all")
    public MarkColumnGroup(
        List<MarkColumn> columns, MarkColumn rootIndexer, DetailRoot root, boolean indexed
    ) {
        this.columns = toArrayOfEmpty(columns, MarkColumn[]::new, EMPTY);
        this.rootIndexer = rootIndexer;
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
     * @param first        如果不为 null，则 firstItem 是 iterator 迭代出的第一项
     */
    @Override
    public final void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) {
        resetAll();
        if (first != null) {
            renderRecord(MarkExecutor.NULL, sheetFactory, sheetFactory.row(), first);
        }
        while (iterator.hasNext()) {
            renderRecord(MarkExecutor.NULL, sheetFactory, sheetFactory.row(), iterator.next());
        }
    }

    /**
     * 实际执行渲染一条记录，单项和 group 均实现这个方法，可递归调用
     *
     * @param container
     * @param sheetFactory
     * @param factory
     * @param data
     */
    @Override
    public final void renderRecord(MarkExecutor container, SheetFactory sheetFactory, RowFactory factory, Object data) {
        renderRootCol(container, sheetFactory, factory);
        renderColumn(container, sheetFactory, factory, data);
    }

    /**
     * 渲染所有列
     *
     * @param container
     * @param sheetFactory
     * @param factory
     * @param data
     */
    private void renderColumn(MarkExecutor container, SheetFactory sheetFactory, RowFactory factory, Object data) {
        for (MarkRenderer column : getColumns()) {
            column.renderRecord(container, sheetFactory, factory, data);
        }
    }

    /**
     * 如果存在索引，首先渲染索引
     *
     * @param container
     * @param sheetFactory
     * @param factory
     */
    private void renderRootCol(MarkExecutor container, SheetFactory sheetFactory, RowFactory factory) {
        MarkColumn root = getRootIndexer();
        if (root != null) {
            root.renderRecord(container, sheetFactory, factory, null);
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

    MarkRenderer[] getColumns() { return columns; }

    public boolean isIndexed() { return indexed; }

    MarkColumn getRootIndexer() { return rootIndexer; }
}
