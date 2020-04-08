package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Cell;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class RowFactory extends BaseFactory<Cell, RowFactory> {

    private final CellFactory factory;

    private Cell cell;

    public RowFactory(WorkbookProxy proxy) {
        super(proxy);
        this.factory = new CellFactory(proxy);
    }

    private CellFactory getCellFactory() { return factory; }

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

    public RowFactory style(String classname) {
        proxy.addSetter(new StyleSetter(proxy.getRow()), classname);
        return this;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public void cell(Date value) { cell(value, 1); }

    public void cell(String value) { cell(value, 1); }

    public void cell(double value) { cell(value, 1, 1); }

    public void cell(boolean value) { cell(value, 1); }

    public void cell(Calendar value) { cell(value, 1); }

    public void cell(LocalDate value) { cell(value, 1); }

    public void cell(LocalDateTime value) { cell(value, 1); }

    public void cell(Date value, int colspan) { cell(value, 1, colspan); }

    public void cell(String value, int colspan) { cell(value, 1, colspan); }

    // 不提供 double 的 colspan 避免和 cell(int, int) 歧义

    public void cell(boolean value, int colspan) { cell(value, 1, colspan); }

    public void cell(Calendar value, int colspan) { cell(value, 1, colspan); }

    public void cell(LocalDate value, int colspan) { cell(value, 1, colspan); }

    public void cell(LocalDateTime value, int colspan) { cell(value, 1, colspan); }

    public void cell(String value, int rowspan, int colspan) {
        nextCell(rowspan, colspan).setCellValue(value);
    }

    public void cell(double value, int rowspan, int colspan) {
        nextCell(rowspan, colspan).setCellValue(value);
    }

    public void cell(boolean value, int rowspan, int colspan) {
        nextCell(rowspan, colspan).setCellValue(value);
    }

    public void cell(Date value, int rowspan, int colspan) {
        nextCell(rowspan, colspan).setCellValue(value);
    }

    public void cell(Calendar value, int rowspan, int colspan) {
        nextCell(rowspan, colspan).setCellValue(value);
    }

    public void cell(LocalDate value, int rowspan, int colspan) {
        nextCell(rowspan, colspan).setCellValue(value);
    }

    public void cell(LocalDateTime value, int rowspan, int colspan) {
        nextCell(rowspan, colspan).setCellValue(value);
    }

    public CellFactory cell() { return cell(1, 1); }

    public CellFactory cell(int rowspan, int colspan) {
        CellFactory factory = getCellFactory();
        factory.setCell(nextCell(rowspan, colspan));
        return factory;
    }

    public RowFactory cell(Consumer<CellFactory> consumer) { return cell(consumer, 1, 1); }

    public RowFactory cell(Consumer<CellFactory> consumer, int rowspan, int colspan) {
        CellFactory factory = getCellFactory();
        factory.setCell(nextCell(rowspan, colspan));
        consumer.accept(factory);
        return this;
    }

    public Cell nextCell() { return nextCell(0); }

    public Cell nextCell(int skipCells) { return nextCell(1, 1, skipCells); }

    public Cell nextCell(int rowspan, int colspan) { return nextCell(rowspan, colspan, 0); }

    public Cell nextCell(int rowspan, int colspan, int skipCells) {
        return proxy.nextCell(skipCells, rowspan, colspan);
    }
}
