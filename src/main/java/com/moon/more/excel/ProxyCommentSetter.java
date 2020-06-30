package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;

/**
 * 注释设置器
 *
 * @author moonsky
 */
class ProxyCommentSetter extends ProxySetter<Comment, Cell> {

    public ProxyCommentSetter(Cell cell) { super(cell); }

    /**
     * 设置注释到单元格
     *
     * @param comment
     */
    @Override
    void setup(Comment comment) {
        getKey().setCellComment(comment);
    }
}
