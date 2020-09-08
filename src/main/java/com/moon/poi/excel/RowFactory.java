package com.moon.poi.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

/**
 * @author moonsky
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
    protected Row get() { return getRow(); }

    /**
     * 预定义注释
     *
     * @param uniqueName     整个工作簿注释唯一命名
     * @param commentBuilder 将要如何定义注释的具体逻辑
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

    /**
     * 设置行高
     *
     * @param height 行高
     *
     * @return 当前 RowFactory
     */
    public RowFactory height(int height) {
        getRow().setHeight((short) height);
        return this;
    }

    /**
     * 对当前行应用样式
     *
     * @param classname 预定义的样式名
     *
     * @return 当前 RowFactory
     */
    public RowFactory style(String classname) {
        proxy.addSetter(new ProxyStyleSetter(proxy.getRow()), classname);
        return this;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * create cell and set value
     */

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value 单元格值
     */
    public void next(Date value) { next(value, 1); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value 单元格值
     */
    public void next(String value) { next(value, 1); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value 单元格值
     */
    public void next(double value) { next(value, 1, 1); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value 单元格值
     */
    public void next(boolean value) { next(value, 1); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value 单元格值
     */
    public void next(Calendar value) { next(value, 1); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value 单元格值
     */
    public void next(LocalDate value) { next(value, 1); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value 单元格值
     */
    public void next(LocalDateTime value) { next(value, 1); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value 单元格值
     */
    public void next(Date value, int colspan) { next(value, 1, colspan); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param colspan 合并单元格的数量
     */
    public void next(String value, int colspan) { next(value, 1, colspan); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param colspan 合并单元格的数量
     */
    public void next(double value, int colspan) { next(value, 1, colspan); }


    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param colspan 合并单元格的数量
     */
    public void next(boolean value, int colspan) { next(value, 1, colspan); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param colspan 合并单元格的数量
     */
    public void next(Calendar value, int colspan) { next(value, 1, colspan); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param colspan 合并单元格的数量
     */
    public void next(LocalDate value, int colspan) { next(value, 1, colspan); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param colspan 合并单元格的数量
     */
    public void next(LocalDateTime value, int colspan) { next(value, 1, colspan); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     */
    public void next(String value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     */
    public void next(double value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     */
    public void next(boolean value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     */
    public void next(Date value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     */
    public void next(Calendar value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     */
    public void next(LocalDate value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    /**
     * 快速创建下一个单元格，并设置值
     *
     * @param value   单元格值
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     */
    public void next(LocalDateTime value, int rowspan, int colspan) { nextCell(rowspan, colspan).setCellValue(value); }

    /*
     * ~~~~~~~~~~ return CellFactory ~~~~~~~~~~ ~~~~~~~~~~ ~~~~~~~~~~ ~~~~~~~~~~ ~~~~~~~~~~
     */

    /**
     * 创建下一个单元格，返回对下一个单元格的操作器
     *
     * @return CellFactory
     */
    public CellFactory newCell() { return newCell(1, 1); }

    /**
     * 创建下一个单元格，返回对下一个单元格的操作器
     *
     * @param offset 单元格位置偏移量
     *
     * @return CellFactory
     */
    public CellFactory newCell(int offset) { return newCell(1, 1, offset); }

    /**
     * 创建下一个单元格，返回对下一个单元格的操作器
     *
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     *
     * @return CellFactory
     */
    public CellFactory newCell(int rowspan, int colspan) { return newCell(rowspan, colspan, 0); }

    /**
     * 创建下一个单元格，返回对下一个单元格的操作器
     *
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     * @param offset  单元格位置偏移量
     *
     * @return CellFactory
     */
    public CellFactory newCell(int rowspan, int colspan, int offset) {
        nextCell(rowspan, colspan, offset);
        return getCellFactory();
    }

    /*
     * ~~~~~~~~~~ consumer CellFactory ~~~~~~~~~~ ~~~~~~~~~~ ~~~~~~~~~~ ~~~~~~~~~~ ~~~~~~~~~~
     */

    /**
     * 创建下一个单元格，并使用下一个单元格的操作器
     *
     * @param consumer 操作单元格
     *
     * @return 当前 RowFactory
     */
    public RowFactory newCell(Consumer<CellFactory> consumer) { return newCell(0, consumer); }

    /**
     * 创建下一个单元格，并使用下一个单元格的操作器
     *
     * @param offset   单元格位置偏移量
     * @param consumer 操作单元格
     *
     * @return 当前 RowFactory
     */
    public RowFactory newCell(int offset, Consumer<CellFactory> consumer) { return newCell(1, 1, offset, consumer); }

    /**
     * 创建下一个单元格，并使用下一个单元格的操作器
     *
     * @param rowspan  合并行数，默认 1
     * @param colspan  合并列数，默认 1
     * @param consumer 操作单元格
     *
     * @return 当前 RowFactory
     */
    public RowFactory newCell(int rowspan, int colspan, Consumer<CellFactory> consumer) {
        return newCell(rowspan, colspan, 0, consumer);
    }

    /**
     * 创建下一个单元格，并使用下一个单元格的操作器
     *
     * @param rowspan  合并行数，默认 1
     * @param colspan  合并列数，默认 1
     * @param consumer 操作单元格
     * @param offset   单元格位置偏移量
     *
     * @return 当前 RowFactory
     */
    public RowFactory newCell(int rowspan, int colspan, int offset, Consumer<CellFactory> consumer) {
        nextCell(rowspan, colspan, offset);
        consumer.accept(getCellFactory());
        return this;
    }

    // create an original Cell

    /**
     * 创建并返回下一个单元格
     *
     * @return 下一个单元格
     */
    public Cell nextCell() { return nextCell(0); }

    /**
     * 创建并返回下一个单元格
     *
     * @param offset 单元格位置偏移量
     *
     * @return 下一个单元格
     */
    public Cell nextCell(int offset) { return nextCell(1, 1, offset); }

    /**
     * 创建并返回下一个单元格
     *
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     *
     * @return 下一个单元格
     */
    public Cell nextCell(int rowspan, int colspan) { return nextCell(rowspan, colspan, 0); }

    /**
     * 创建并返回下一个单元格
     *
     * @param rowspan 合并行数，默认 1
     * @param colspan 合并列数，默认 1
     * @param offset  单元格位置偏移量
     *
     * @return 下一个单元格
     */
    public Cell nextCell(int rowspan, int colspan, int offset) {
        return factory.setCell(proxy.nextCell(offset, rowspan, colspan));
    }

    // create a cell at index


    /**
     * 在指定位置使用或创建单元格
     *
     * @param index 单元格位置
     *
     * @return 单元格操作器
     */
    public CellFactory index(int index) {
        factory.setCell(proxy.useOrCreateCell(index));
        return factory;
    }

    /**
     * 在指定位置使用或创建单元格
     *
     * @param index  单元格位置
     * @param create 是否直接创建，而不是优先使用已存在的单元格
     *
     * @return 单元格操作器
     */
    public CellFactory index(int index, boolean create) {
        factory.setCell(proxy.useOrCreateCell(index, create));
        return factory;
    }


    /**
     * 在指定位置使用或创建单元格
     *
     * @param index    单元格位置
     * @param consumer 操作器
     *
     * @return 当前 RowFactory
     */
    public RowFactory index(int index, Consumer<CellFactory> consumer) {
        factory.setCell(proxy.useOrCreateCell(index));
        consumer.accept(factory);
        return this;
    }

    /**
     * 在指定位置使用或创建单元格
     *
     * @param index    单元格位置
     * @param create   是否直接创建，而不是优先使用已存在的单元格
     * @param consumer 操作器
     *
     * @return 当前 RowFactory
     */
    public RowFactory index(int index, boolean create, Consumer<CellFactory> consumer) {
        factory.setCell(proxy.useOrCreateCell(index, create));
        consumer.accept(factory);
        return this;
    }

    // use a present cell or create a new cell

    /**
     * 在指定位置使用或创建单元格
     *
     * @param cellIndexInRow 单元格位置
     * @param consumer       操作器
     *
     * @return 当前 RowFactory
     */
    public RowFactory useCell(int cellIndexInRow, Consumer<CellFactory> consumer) {
        consumer.accept(useCell(cellIndexInRow));
        return this;
    }

    /**
     * 在指定位置使用或创建单元格
     *
     * @param cellIndexInRow 单元格位置
     *
     * @return 单元格操作器
     */
    public CellFactory useCell(int cellIndexInRow) {
        factory.setCell(proxy.useOrCreateCell(cellIndexInRow));
        return factory;
    }

    /**
     * 在指定位置使用或创建单元格
     *
     * @param cellIndexInRow 单元格位置
     * @param rowspan        合并行数，默认 1
     * @param colspan        合并列数，默认 1
     * @param consumer       操作单元格
     *
     * @return 当前 RowFactory
     */
    private RowFactory useCell(int cellIndexInRow, int rowspan, int colspan, Consumer<CellFactory> consumer) {
        consumer.accept(useCell(cellIndexInRow, rowspan, colspan));
        return this;
    }

    /**
     * 在指定位置使用或创建单元格
     *
     * @param cellIndexInRow 单元格位置
     * @param rowspan        合并行数，默认 1
     * @param colspan        合并列数，默认 1
     *
     * @return 单元格操作器
     */
    private CellFactory useCell(int cellIndexInRow, int rowspan, int colspan) {
        factory.setCell(proxy.useOrCreateCell(cellIndexInRow));
        return factory;
    }
}
