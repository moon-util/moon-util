package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author benshaoye
 */
class ProxyCommentModel extends ProxyModel<Sheet, Comment, Cell, ProxyCommentBuilder, ProxyCommentSetter> {

    public ProxyCommentModel() { super(false); }
}
