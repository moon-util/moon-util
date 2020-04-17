package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.Priority;
import com.moon.more.excel.annotation.DataRecord;

/**
 * @author benshaoye
 */
class RootDetail {

    private final boolean annotatedOnClass;

    final static RootDetail DEFAULT = new RootDetail();

    private final Priority priority;

    private RootDetail() { this(Priority.GROUP); }

    private RootDetail(DataRecord entity) {
        this(entity == null ? Priority.GROUP : entity.priority(), entity != null);
    }

    private RootDetail(Priority priority) { this(priority, false); }

    private RootDetail(Priority priority, boolean annotatedOnClass) {
        this.priority = priority == null ? Priority.GROUP : priority;
        this.annotatedOnClass = annotatedOnClass;
    }

    static RootDetail of(DataRecord record) {
        return record == null ? DEFAULT : new RootDetail(record);
    }

    public Priority getPriority() { return priority; }

    public boolean isAnnotatedOnClass() { return annotatedOnClass; }

    public HeadSortEnum getSortStrategy() {
        return HeadSortEnum.getSortStrategy(getPriority());
    }
}
