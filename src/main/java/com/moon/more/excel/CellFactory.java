package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * @author benshaoye
 */
public class CellFactory extends BaseFactory<Cell, CellFactory> {

    private Cell cell;

    public CellFactory(WorkbookProxy proxy) { super(proxy); }

    @Override
    Cell get() { return getCell(); }

    void setCell(Cell cell) { this.cell = cell; }

    public Cell getCell() {
        Cell cell = this.cell;
        if (cell == null) {
            this.setCell(cell = proxy.getCell());
        }
        return cell;
    }

    public CellFactory style(String classname) {
        proxy.addSetter(new StyleSetter(cell), classname);
        return this;
    }

    public CellFactory hide() {
        proxy.getSheet().setColumnHidden(cell.getColumnIndex(), true);
        return this;
    }

    public CellFactory show() {
        proxy.getSheet().setColumnHidden(cell.getColumnIndex(), false);
        return this;
    }

    public CellFactory comment(Comment comment) {
        cell.setCellComment(comment);
        return this;
    }

    public CellFactory active() {
        cell.setAsActiveCell();
        return this;
    }

    public CellFactory val(Date value) {
        cell.setCellValue(value);
        return this;
    }

    public CellFactory val(String value) {
        cell.setCellValue(value);
        return this;
    }

    public CellFactory val(double value) {
        cell.setCellValue(value);
        return this;
    }

    public CellFactory val(boolean value) {
        cell.setCellValue(value);
        return this;
    }

    public CellFactory val(Calendar value) {
        cell.setCellValue(value);
        return this;
    }

    public CellFactory val(LocalDate value) {
        cell.setCellValue(value);
        return this;
    }

    public CellFactory val(LocalDateTime value) {
        cell.setCellValue(value);
        return this;
    }
}
