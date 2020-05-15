package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.Priority;
import com.moon.more.excel.annotation.TableRecord;

/**
 * @author benshaoye
 */
class DetailRoot {

    private final boolean annotatedOnClass;

    final static DetailRoot DEFAULT = new DetailRoot();

    private final Priority priority;
    private final int offset;

    private DetailRoot() { this(Priority.GROUP); }

    private DetailRoot(TableRecord entity) {
        this(entity == null ? Priority.GROUP : entity.priority(), entity.offset(), entity != null);
    }

    private DetailRoot(Priority priority) { this(priority, 0); }

    private DetailRoot(Priority priority, int offset) { this(priority, offset, false); }

    private DetailRoot(Priority priority, int offset, boolean annotatedOnClass) {
        this.priority = priority == null ? Priority.GROUP : priority;
        this.annotatedOnClass = annotatedOnClass;
        this.offset = offset;
    }

    static DetailRoot of(TableRecord record) { return record == null ? DEFAULT : new DetailRoot(record); }

    public Priority getPriority() { return priority; }

    public int getOffset() { return offset; }

    public boolean isAnnotatedOnClass() { return annotatedOnClass; }
}
