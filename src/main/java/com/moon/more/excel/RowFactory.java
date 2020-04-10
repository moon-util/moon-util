package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class RowFactory extends BaseFactory<Row, RowFactory, SheetFactory> {

    private final CellFactory factory;

    private Row row;

    public RowFactory(WorkbookProxy proxy, SheetFactory parent) {
        super(proxy, parent);
        this.factory = new CellFactory(proxy, this);
    }

    private CellFactory getCellFactory() { return factory; }

    public Row getRow() { return row; }

    void setRow(Row row) { this.row = row; }

    @Override
    Row get() { return getRow(); }
    /**
     * 预定义注释
     *
     * @param uniqueName 整个工作簿注释唯一命名
     * @param commentBuilder    将要如何定义注释的具体逻辑
     *
     * @return 当前对象
     */
    @Override
    public RowFactory definitionComment(String uniqueName, Consumer<Comment> commentBuilder) {
        super.definitionComment(uniqueName, commentBuilder);
        return this;
    }
    /**
     * 预定义注释
     *
     * @param uniqueName 整个工作簿注释唯一命名
     * @param comment    注释内容
     *
     * @return 当前对象
     */
    @Override
    public RowFactory definitionComment(String uniqueName, String comment) {
        super.definitionComment(uniqueName, comment);
        return this;
    }

    public RowFactory height(int height) {
        getRow().setHeight((short) height);
        return this;
    }

    public RowFactory style(String classname) {
        proxy.addSetter(new ProxyStyleSetter(proxy.getRow()), classname);
        return this;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * create cell and set value
     */

    public void next(Date value) { next(value, 1); }

    public void next(String value) { next(value, 1); }

    public void next(double value) { next(value, 1, 1); }

    public void next(boolean value) { next(value, 1); }

    public void next(Calendar value) { next(value, 1); }

    public void next(LocalDate value) { next(value, 1); }

    public void next(LocalDateTime value) { next(value, 1); }


    public void next(Date value, int colspan) { next(value, 1, colspan); }

    public void next(String value, int colspan) { next(value, 1, colspan); }

    // 不提供 double 的 colspan 避免和 next(int, int) 歧义

    public void next(boolean value, int colspan) { next(value, 1, colspan); }

    public void next(Calendar value, int colspan) { next(value, 1, colspan); }

    public void next(LocalDate value, int colspan) { next(value, 1, colspan); }

    public void next(LocalDateTime value, int colspan) { next(value, 1, colspan); }


    public void next(String value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    public void next(double value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    public void next(boolean value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    public void next(Date value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    public void next(Calendar value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    public void next(LocalDate value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    public void next(LocalDateTime value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    // return CellFactory

    public CellFactory cell() { return cell(1, 1); }

    public CellFactory cell(int rowspan, int colspan) { return cell(rowspan, colspan, 0); }

    public CellFactory cell(int rowspan, int colspan, int skipCells) {
        nextCell(rowspan, colspan, skipCells);
        return getCellFactory();
    }

    // consumer CellFactory

    public RowFactory cell(Consumer<CellFactory> consumer) { return cell(1, consumer); }

    public RowFactory cell(int colspan, Consumer<CellFactory> consumer) { return cell(1, colspan, consumer); }

    public RowFactory cell(int rowspan, int colspan, Consumer<CellFactory> consumer) {
        return cell(rowspan, colspan, consumer, 0);
    }

    public RowFactory cell(int rowspan, int colspan, Consumer<CellFactory> consumer, int skipCells) {
        nextCell(rowspan, colspan, skipCells);
        consumer.accept(getCellFactory());
        return this;
    }

    // create an original Cell

    public Cell nextCell() { return nextCell(0); }

    public Cell nextCell(int skipCells) { return nextCell(1, 1, skipCells); }

    public Cell nextCell(int rowspan, int colspan) { return nextCell(rowspan, colspan, 0); }

    public Cell nextCell(int rowspan, int colspan, int skipCells) {
        return factory.setCell(proxy.nextCell(skipCells, rowspan, colspan));
    }

    public RowFactory useCell(int cellIndexInRow, Consumer<CellFactory> consumer) {
        consumer.accept(useCell(cellIndexInRow));
        return this;
    }

    public CellFactory useCell(int cellIndexInRow) {
        factory.setCell(proxy.useOrCreateCell(cellIndexInRow));
        return factory;
    }

    public RowFactory useCell(int cellIndexInRow, int rowspan, int colspan, Consumer<CellFactory> consumer) {
        consumer.accept(useCell(cellIndexInRow, rowspan, colspan));
        return this;
    }

    public CellFactory useCell(int cellIndexInRow, int rowspan, int colspan) {
        factory.setCell(proxy.useOrCreateCell(cellIndexInRow));
        return factory;
    }
}
