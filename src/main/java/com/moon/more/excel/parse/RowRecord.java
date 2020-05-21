package com.moon.more.excel.parse;

import com.moon.more.excel.annotation.Priority;
import com.moon.more.excel.annotation.TableRecord;

/**
 * @author benshaoye
 */
class RowRecord {

    private final boolean annotatedOnClass;

    final static RowRecord DEFAULT = new RowRecord();

    private final Priority priority;
    private final int offset;

    private RowRecord() { this(Priority.GROUP); }

    private RowRecord(TableRecord record) {
        this(record == null ? Priority.GROUP : record.priority(), record.offset(), record != null);
    }

    private RowRecord(Priority priority) { this(priority, 0); }

    private RowRecord(Priority priority, int offset) { this(priority, offset, false); }

    private RowRecord(Priority priority, int offset, boolean annotatedOnClass) {
        this.priority = priority == null ? Priority.GROUP : priority;
        this.annotatedOnClass = annotatedOnClass;
        this.offset = offset;
    }

    static RowRecord of(TableRecord record) { return record == null ? DEFAULT : new RowRecord(record); }

    public Priority getPriority() { return priority; }

    public int getOffset() { return offset; }

    public boolean isAnnotatedOnClass() { return annotatedOnClass; }
}
