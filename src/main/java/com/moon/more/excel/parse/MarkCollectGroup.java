package com.moon.more.excel.parse;

import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

import java.util.List;

/**
 * @author benshaoye
 */
public class MarkCollectGroup extends MarkColumnGroup {

    private final MarkCollect collectAt;

    private final IterateStrategy strategy;

    public MarkCollectGroup(
        List<MarkColumn> columns, MarkCollect collectAt, MarkColumn rootIndexer, DetailRoot root, boolean indexed
    ) {
        super(columns, rootIndexer, root, indexed);
        this.strategy = IterateFactory.getIterateStrategy(collectAt);
        this.collectAt = collectAt;
    }

    public MarkCollect getCollectAt() { return collectAt; }

    @Override
    public void renderRecord(
        MarkTask task, SheetFactory sheetFactory, RowFactory factory, Object data
    ) {
        MarkTask thisTask = new MarkTask(rowFactory -> {
            MarkRenderer[] renderers = getColumns();
            for (MarkRenderer renderer : renderers) {
                renderer.renderRecord(task, sheetFactory, rowFactory, data);
            }
        }, task);

        MarkCollect collectAt = getCollectAt();
        if (collectAt != null) {
            collectAt.renderRecord(thisTask, sheetFactory, factory, data);
        }
    }
}
