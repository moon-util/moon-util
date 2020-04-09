package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;

/**
 * @author benshaoye
 */
class ProxyCommentSetter extends ProxySetter<Comment, Cell> {

    private boolean removed = false;

    public ProxyCommentSetter(Cell cell) { super(cell); }

    public boolean isRemoved() { return removed; }

    public void setRemoved(boolean removed) { this.removed = removed; }

    @Override
    void setup(Comment comment) {
        if (!removed) {
            getKey().setCellComment(comment);
        }
    }
}
