package com.moon.poi.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * 注释代理
 *
 * @author moonsky
 */
class ProxyCommentModel extends ProxyModel<Sheet, Comment, Cell, ProxyCommentBuilder, ProxyCommentSetter> {

    public ProxyCommentModel() { super(false); }
}
