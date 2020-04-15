package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.Priority;
import com.moon.more.excel.annotation.DataRecord;

/**
 * @author benshaoye
 */
class ParsedRootDetail {

    private final boolean annotatedOnClass;

    final static ParsedRootDetail DEFAULT = new ParsedRootDetail();

    private final Priority priority;

    private ParsedRootDetail() { this(Priority.GROUP); }

    private ParsedRootDetail(DataRecord entity) {
        this(entity == null ? Priority.GROUP : entity.priority(), entity != null);
    }

    private ParsedRootDetail(Priority priority) {
        this(priority, false);
    }

    private ParsedRootDetail(Priority priority, boolean annotatedOnClass) {
        this.priority = priority == null ? Priority.GROUP : priority;
        this.annotatedOnClass = annotatedOnClass;
    }

    static ParsedRootDetail of(DataRecord record) {
        return record == null ? DEFAULT : new ParsedRootDetail(record);
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isAnnotatedOnClass() { return annotatedOnClass; }

    public PrioritySortStrategy getSortStrategy() {
        return PrioritySortStrategy.getSortStrategy(getPriority());
    }
}
