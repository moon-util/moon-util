package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.function.Consumer;

/**
 * @author benshaoye
 */
class ProxyCommentBuilder extends ProxyBuilder<Sheet, Comment> {

    private final Sheet sheet;
    private final WorkbookType type;
    private final Consumer<Comment> builder;

    public ProxyCommentBuilder(String classname, Sheet sheet, WorkbookType type, Consumer<Comment> builder) {
        super(classname);
        this.builder = builder;
        this.sheet = sheet;
        this.type = type;
    }

    @Override
    Comment build(Sheet unUse) {
        Drawing drawing = this.sheet.createDrawingPatriarch();
        Comment comment = drawing.createCellComment(type.newAnchor());
        builder.accept(comment);
        return comment;
    }
}
