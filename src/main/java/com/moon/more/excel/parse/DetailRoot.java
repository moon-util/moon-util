package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.Priority;
import com.moon.more.excel.annotation.DataRecord;

/**
 * @author benshaoye
 */
class DetailRoot {

    private final boolean annotatedOnClass;

    final static DetailRoot DEFAULT = new DetailRoot();

    private final Priority priority;

    private DetailRoot() { this(Priority.GROUP); }

    private DetailRoot(DataRecord entity) {
        this(entity == null ? Priority.GROUP : entity.priority(), entity != null);
    }

    private DetailRoot(Priority priority) { this(priority, false); }

    private DetailRoot(Priority priority, boolean annotatedOnClass) {
        this.priority = priority == null ? Priority.GROUP : priority;
        this.annotatedOnClass = annotatedOnClass;
    }

    static DetailRoot of(DataRecord record) { return record == null ? DEFAULT : new DetailRoot(record); }

    public Priority getPriority() { return priority; }

    public boolean isAnnotatedOnClass() { return annotatedOnClass; }

    public HeadSortEnum getSortStrategy() { return HeadSortEnum.getSortStrategy(getPriority()); }
}
