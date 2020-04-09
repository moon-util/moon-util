package com.moon.more.excel;

import com.moon.core.lang.StringUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class CellFactory extends BaseFactory<Cell, CellFactory> {

    private Cell cell;
    private int columnIndex = -1;
    private ProxyCommentSetter setter;

    public CellFactory(WorkbookProxy proxy) { super(proxy); }

    @Override
    Cell get() { return getCell(); }

    Cell setCell(Cell cell) {
        this.columnIndex = -1;
        this.setter = null;
        this.cell = cell;
        return cell;
    }

    ProxyCommentSetter getSetter() { return setter; }

    ProxyCommentSetter ensureSetter() {
        ProxyCommentSetter setter = this.setter;
        if (setter == null) {
            setter = new ProxyCommentSetter(getCell());
            this.setter = setter;
        }
        return setter;
    }

    int getColumnIndex() {
        int idx = this.columnIndex;
        if (idx < 0) {
            idx = getCell().getColumnIndex();
            this.columnIndex = idx;
        }
        return idx;
    }

    Sheet getSheet() { return proxy.getSheet(); }

    public Cell getCell() { return cell; }

    @Override
    public CellFactory definitionComment(String classname, Consumer<Comment> commentBuilder) {
        super.definitionComment(classname, commentBuilder);
        return this;
    }

    public CellFactory style(String classname) {
        proxy.addSetter(new ProxyStyleSetter(getCell()), classname);
        return this;
    }

    public CellFactory hide() {
        getSheet().setColumnHidden(getColumnIndex(), true);
        return this;
    }

    public CellFactory show() {
        getSheet().setColumnHidden(getColumnIndex(), false);
        return this;
    }

    public CellFactory width(int width) {
        getSheet().setColumnWidth(getColumnIndex(), width);
        return this;
    }

    public CellFactory autoWidth() {
        getSheet().autoSizeColumn(getColumnIndex());
        return this;
    }

    public CellFactory autoWidth(boolean useMergedCells) {
        getSheet().autoSizeColumn(getColumnIndex(), useMergedCells);
        return this;
    }

    public CellFactory commentAs(String definedCommentName) {
        proxy.addSetter(ensureSetter(), definedCommentName);
        return this;
    }

    private void removeComment() {
        cell.removeCellComment();
        if (this.setter != null) {
            proxy.removeSetter(setter);
        }
    }

    public CellFactory comment(String comment) {
        Cell cell = getCell();
        if (StringUtil.isEmpty(comment)) {
            removeComment();
        } else {
            cell.setCellComment(proxy.createComment(comment));
        }
        return this;
    }

    public CellFactory comment(Comment comment) {
        Cell cell = getCell();
        if (comment == null) {
            removeComment();
        } else {
            cell.setCellComment(comment);
        }
        return this;
    }

    public CellFactory active() {
        getCell().setAsActiveCell();
        return this;
    }

    public CellFactory val(Date value) {
        getCell().setCellValue(value);
        return this;
    }

    public CellFactory val(String value) {
        getCell().setCellValue(value);
        return this;
    }

    public CellFactory val(double value) {
        getCell().setCellValue(value);
        return this;
    }

    public CellFactory val(boolean value) {
        getCell().setCellValue(value);
        return this;
    }

    public CellFactory val(Calendar value) {
        getCell().setCellValue(value);
        return this;
    }

    public CellFactory val(LocalDate value) {
        getCell().setCellValue(value);
        return this;
    }

    public CellFactory val(LocalDateTime value) {
        getCell().setCellValue(value);
        return this;
    }
}
