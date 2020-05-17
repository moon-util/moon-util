package com.moon.more.excel.parse;

import com.moon.more.excel.RowFactory;
import com.moon.more.excel.SheetFactory;

import java.util.Iterator;
import java.util.List;

/**
 * @author benshaoye
 */
public class MarkCollectGroup extends MarkColumnGroup {

    private final MarkCollect collectAt;

    public MarkCollectGroup(
        List<MarkColumn> columns, MarkCollect collectAt, MarkColumn rootIndexer, RowRecord root, boolean indexed
    ) {
        super(columns, rootIndexer, root, indexed);
        this.collectAt = collectAt;
    }

    public MarkCollect getCollectAt() { return collectAt; }

    @Override
    public void renderBody(SheetFactory sheetFactory, Iterator iterator, Object first) {
        resetAll();
        if (first != null) {
            renderRecord(MarkTask.NONE, sheetFactory, null, first);
        }
        while (iterator.hasNext()) {
            renderRecord(MarkTask.NONE, sheetFactory, null, iterator.next());
        }
    }

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
