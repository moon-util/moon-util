package com.moon.poi.excel;

import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.function.Consumer;

/**
 * @author moonsky
 */
class ProxyCommentBuilder extends ProxyBuilder<Sheet, Comment> {

    private final Sheet sheet;
    private final WorkbookType type;
    private final Consumer<Comment> builder;

    public ProxyCommentBuilder(Object classname, Sheet sheet, WorkbookType type, Consumer<Comment> builder) {
        super(classname);
        this.builder = builder;
        this.sheet = sheet;
        this.type = type;
    }

    /**
     * 从 sheet 工作表创建一条注释
     *
     * @param unUse 工作表，由于注释是与 sheet 绑定的，需要在创建时就绑定，故这里不使用
     *
     * @return 注释
     *
     * @see #sheet
     */
    @Override
    Comment build(Sheet unUse) {
        Drawing drawing = this.sheet.createDrawingPatriarch();
        Comment comment = drawing.createCellComment(type.newAnchor());
        builder.accept(comment);
        return comment;
    }
}
