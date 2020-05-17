package com.moon.more.excel.parse;

import com.moon.core.lang.ref.IntAccessor;
import com.moon.more.excel.Renderer;
import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

import java.util.Iterator;
import java.util.List;

/**
 * @author benshaoye
 */
public class MarkColumnGroup<T extends MarkColumn> extends AbstractMarkGroup implements MarkRenderer, Renderer {

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
        List<T> columns, T rootIndexer, RowRecord root, boolean indexed
    ) { super(columns, rootIndexer, root, indexed); }

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
    public void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) {
        resetAll();
        int offsetRow = getRoot().getOffset();
        if (first != null) {
            renderRecord(MarkTask.NONE, sheetFactory, sheetFactory.row(offsetRow), first);
        }
        while (iterator.hasNext()) {
            renderRecord(MarkTask.NONE, sheetFactory, sheetFactory.row(offsetRow), iterator.next());
        }
    }

    /**
     * 实际执行渲染一条记录，单项和 group 均实现这个方法，可递归调用
     *
     * @param task
     * @param sheetFactory
     * @param factory
     * @param data
     */
    @Override
    public void renderRecord(MarkTask task, SheetFactory sheetFactory, RowFactory factory, Object data) {
        renderRootCol(task, sheetFactory, factory);
        renderColumn(task, sheetFactory, factory, data);
    }

    /**
     * 渲染所有列
     *
     * @param task
     * @param sheetFactory
     * @param factory
     * @param data
     */
    private void renderColumn(MarkTask task, SheetFactory sheetFactory, RowFactory factory, Object data) {
        for (MarkRenderer column : getColumns()) {
            column.renderRecord(task, sheetFactory, factory, data);
        }
    }

    /**
     * 如果存在索引，首先渲染索引
     *
     * @param task
     * @param sheetFactory
     * @param factory
     */
    private void renderRootCol(MarkTask task, SheetFactory sheetFactory, RowFactory factory) {
        MarkColumn root = getRootIndexer();
        if (root != null) {
            root.renderRecord(task, sheetFactory, factory, null);
        }
    }

    /*
     * ~~~~~ initialize ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    @Override
    public void resetAll() {
        if (isIndexed()) {
            MarkColumn rootIndexer = getRootIndexer();
            if (rootIndexer != null) {
                rootIndexer.resetAll();
            }
            for (MarkRenderer column : getColumns()) {
                column.resetAll();
            }
        }
    }
}
