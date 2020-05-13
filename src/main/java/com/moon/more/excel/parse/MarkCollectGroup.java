package com.moon.more.excel.parse;

import java.util.List;

/**
 * @author benshaoye
 */
public class MarkCollectGroup extends MarkColumnGroup {

    private final MarkCollect collectAt;

    public MarkCollectGroup(
        List<MarkColumn> columns, MarkCollect collectAt, MarkColumn rootIndexer, DetailRoot root, boolean indexed
    ) {
        super(columns, rootIndexer, root, indexed);
        this.collectAt = collectAt;
    }

    public MarkCollect getCollectAt() { return collectAt; }
}
