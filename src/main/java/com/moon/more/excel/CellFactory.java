package com.moon.more.excel;

import com.moon.core.lang.StringUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class CellFactory extends BaseFactory<Cell, CellFactory, RowFactory> {

    /**
     * 当前正在操作的单元格
     */
    private Cell cell;
    /**
     * 当前正在操作的单元格索引，-1 表示未设置
     */
    private int columnIndex = -1;
    /**
     * 当前单元格的注释代理
     */
    private ProxyCommentSetter setter;

    /**
     * 创建一个工厂
     *
     * @param proxy  Excel 集中代理
     * @param parent 父节点
     */
    public CellFactory(WorkbookProxy proxy, RowFactory parent) {
        super(proxy, parent);
    }

    /**
     * 当前正在操作的单元格实例
     *
     * @return 当前正在操作的单元格实例
     */
    @Override
    Cell get() { return getCell(); }

    /**
     * 设置将要操作的单元格
     *
     * @param cell 将要操作的单元格
     *
     * @return 正在操作的单元格（== 将要操作的单元格）
     */
    Cell setCell(Cell cell) {
        this.columnIndex = -1;
        this.setter = null;
        this.cell = cell;
        return cell;
    }

    /**
     * 获取当前单元格的注释代理
     *
     * @return 当前单元格注释代理
     */
    ProxyCommentSetter getSetter() { return setter; }

    /**
     * 获取或创建当前单元格注释代理
     *
     * @return 当前单元格注释代理
     */
    ProxyCommentSetter ensureSetter() {
        ProxyCommentSetter setter = this.setter;
        if (setter == null) {
            setter = new ProxyCommentSetter(getCell());
            this.setter = setter;
        }
        return setter;
    }

    /**
     * 当前正在操作单元格索引
     *
     * @return 当前正在操作单元格索引
     */
    int getColumnIndex() {
        int idx = this.columnIndex;
        if (idx < 0) {
            idx = getCell().getColumnIndex();
            this.columnIndex = idx;
        }
        return idx;
    }

    /**
     * 当前正在操作的工作表
     *
     * @return 当前正在操作的工作表
     */
    Sheet getSheet() { return proxy.getSheet(); }

    /**
     * 当前正在操作的单元格实例
     *
     * @return 当前正在操作的单元格实例
     */
    public Cell getCell() { return cell; }

    /**
     * 预定义注释
     *
     * @param uniqueName     整个工作簿注释唯一命名
     * @param commentBuilder 将要如何定义注释的具体逻辑
     *
     * @return 当前对象
     */
    @Override
    public CellFactory definitionComment(String uniqueName, Consumer<Comment> commentBuilder) {
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
    public CellFactory definitionComment(String uniqueName, String comment) {
        super.definitionComment(uniqueName, comment);
        return this;
    }

    /**
     * 给当前单元格应用预定义样式，定义样式参考{@link #definitionStyle(String, Consumer)}
     *
     * @param classname 样式类名，命名方式参考前端 html，
     *
     * @return 当前对象
     *
     * @see #definitionStyle(String, BiConsumer)
     */
    public CellFactory style(String classname) {
        proxy.addSetter(new ProxyStyleSetter(getCell(), proxy.getRegion()), classname);
        return this;
    }

    /**
     * 隐藏当前列
     *
     * @return 当前 CellFactory
     */
    public CellFactory hide() {
        getSheet().setColumnHidden(getColumnIndex(), true);
        return this;
    }

    /**
     * 显示当前列，与{@link #hide()}对应
     *
     * @return 当前 CellFactory
     */
    public CellFactory show() {
        getSheet().setColumnHidden(getColumnIndex(), false);
        return this;
    }

    /**
     * 给当前列设置宽度
     *
     * @param width 宽度值
     *
     * @return 当前 CellFactory
     *
     * @see SheetFactory#setColumnsWidth(Integer...)
     * @see SheetFactory#setWidth(int, int)
     */
    public CellFactory width(int width) {
        getSheet().setColumnWidth(getColumnIndex(), width);
        return this;
    }

    /**
     * 设置当前列为在自动宽度
     *
     * @return 当前 CellFactory
     *
     * @see SheetFactory#columnsAutoWidth(int...)
     */
    public CellFactory autoWidth() {
        getSheet().autoSizeColumn(getColumnIndex());
        return this;
    }


    /**
     * 设置当前列为在自动宽度
     *
     * @param useMergedCells {@link Sheet#autoSizeColumn(int, boolean)}
     *
     * @return 当前 CellFactory
     *
     * @see SheetFactory#columnsAutoWidth(int...)
     * @see SheetFactory#columnsAutoWidth(boolean, int...)
     */
    public CellFactory autoWidth(boolean useMergedCells) {
        getSheet().autoSizeColumn(getColumnIndex(), useMergedCells);
        return this;
    }

    /**
     * 为当前单元格应用预定义注释
     * <p>
     * {@link CellFactory}提供了多种方式使用注释，如果是复杂的注释内容建议使用预定义方式进行集中管理
     *
     * @param definedCommentName 预定义注释唯一命名
     *
     * @return 当前 CellFactory
     */
    public CellFactory commentAs(String definedCommentName) {
        proxy.addSetter(ensureSetter(), definedCommentName);
        return this;
    }

    /**
     * 删除当前单元格的注释
     *
     * @return 当前 CellFactory
     */
    private CellFactory removeComment() {
        cell.removeCellComment();
        if (this.getSetter() != null) {
            proxy.removeSetter(setter);
            this.setter = null;
        }
        return this;
    }

    /**
     * 给当前单元格设置注释，注释内容为空表示删除注释
     * <p>
     * 这种方式不受集中管理，可在单独设置时使用
     *
     * @param comment 注释内容
     *
     * @return 当前 CellFactory
     */
    public CellFactory comment(String comment) {
        Cell cell = getCell();
        if (StringUtil.isEmpty(comment)) {
            removeComment();
        } else {
            cell.setCellComment(proxy.createComment(comment));
        }
        return this;
    }

    /**
     * 给当前单元格设置注释，如果注释为 null 表示删除注释
     * <p>
     * 这种方式不受集中管理，可在单独设置时使用
     *
     * @param comment 注释实例
     *
     * @return 当前 CellFactory
     */
    public CellFactory comment(Comment comment) {
        Cell cell = getCell();
        if (comment == null) {
            removeComment();
        } else {
            cell.setCellComment(comment);
        }
        return this;
    }

    /**
     * 设置当前单元格为活动单元格
     *
     * @return 当前 CellFactory
     */
    public CellFactory active() {
        getCell().setAsActiveCell();
        return this;
    }

    /**
     * 给当前单元格设置值
     *
     * @param value 将要设置的值
     *
     * @return 当前 CellFactory
     */
    public CellFactory val(Date value) {
        getCell().setCellValue(value);
        return this;
    }

    /**
     * 给当前单元格设置值
     *
     * @param value 将要设置的值
     *
     * @return 当前 CellFactory
     */
    public CellFactory val(String value) {
        getCell().setCellValue(value);
        return this;
    }

    /**
     * 给当前单元格设置值
     *
     * @param value 将要设置的值
     *
     * @return 当前 CellFactory
     */
    public CellFactory val(double value) {
        getCell().setCellValue(value);
        return this;
    }

    /**
     * 给当前单元格设置值
     *
     * @param value 将要设置的值
     *
     * @return 当前 CellFactory
     */
    public CellFactory val(boolean value) {
        getCell().setCellValue(value);
        return this;
    }

    /**
     * 给当前单元格设置值
     *
     * @param value 将要设置的值
     *
     * @return 当前 CellFactory
     */
    public CellFactory val(Calendar value) {
        getCell().setCellValue(value);
        return this;
    }

    /**
     * 给当前单元格设置值
     *
     * @param value 将要设置的值
     *
     * @return 当前 CellFactory
     */
    public CellFactory val(LocalDate value) {
        getCell().setCellValue(value);
        return this;
    }

    /**
     * 给当前单元格设置值
     *
     * @param value 将要设置的值
     *
     * @return 当前 CellFactory
     */
    public CellFactory val(LocalDateTime value) {
        getCell().setCellValue(value);
        return this;
    }
}