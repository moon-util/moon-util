package com.moon.poi.excel;

import com.moon.core.lang.StringUtil;
import com.moon.core.util.TypeUtil;
import com.moon.core.util.converter.TypeCaster;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author moonsky
 */
public class CellWriter extends BaseWriter<Cell, CellWriter, RowWriter> {

    private final TypeCaster caster = TypeUtil.cast();

    private ImageWriter imageFactory;

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
    public CellWriter(WorkbookProxy proxy, RowWriter parent) { super(proxy, parent); }

    private synchronized ImageWriter newImageFactory() {
        return this.imageFactory = new ImageWriter(getProxy(), this);
    }

    private ImageWriter obtainImageFactory() {
        ImageWriter factory = this.imageFactory;
        return factory == null ? newImageFactory() : factory;
    }

    /**
     * 当前正在操作的单元格实例
     *
     * @return 当前正在操作的单元格实例
     */
    @Override
    protected Cell get() { return getCell(); }

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

    /*
     * ~~~~~~~~ 操作单元格 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * 预定义注释
     *
     * @param uniqueName     整个工作簿注释唯一命名
     * @param commentBuilder 将要如何定义注释的具体逻辑
     *
     * @return 当前对象
     */
    @Override
    public CellWriter definitionComment(String uniqueName, Consumer<Comment> commentBuilder) {
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
    public CellWriter definitionComment(String uniqueName, String comment) {
        super.definitionComment(uniqueName, comment);
        return this;
    }

    /**
     * create an ProxyStyleSetter
     *
     * @return ProxyStyleSetter
     */
    private ProxyStyleSetter newSetter() {
        return new ProxyStyleSetter(getCell(), proxy.getRegion());
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
    public CellWriter styleAs(String classname) {
        proxy.addSetter(newSetter(), classname);
        return this;
    }

    /**
     * 复制当前单元格样式并命名为指定名称
     *
     * @param classname 唯一名称
     *
     * @return 当前对象
     */
    public CellWriter cloneStyleAs(String classname) { return doCloneStyleAs(classname); }

    /**
     * 复制当前单元格样式并命名为指定名称
     *
     * @param classname 唯一名称
     *
     * @return 当前对象
     */
    public CellWriter cloneStyleAs(Integer classname) { return doCloneStyleAs(classname); }

    /**
     * 复制当前单元格样式并命名为指定名称
     *
     * @param classname 唯一名称
     *
     * @return 当前对象
     */
    private CellWriter doCloneStyleAs(Object classname) {
        ProxyStyleBuilder builder = proxy.findBuilder(newSetter());
        if (builder != null) {
            proxy.definitionBuilder(new ProxyStyleBuilder(classname, builder));
        } else {
            CellStyle cellStyle = getCell().getCellStyle();
            if (classname instanceof Integer) {
                definitionStyle((Integer) classname, style -> style.cloneStyleFrom(cellStyle));
            } else {
                definitionStyle(classname.toString(), style -> style.cloneStyleFrom(cellStyle));
            }
        }
        return this;
    }

    /**
     * 隐藏当前列
     *
     * @return 当前 CellFactory
     */
    public CellWriter hide() {
        getSheet().setColumnHidden(getColumnIndex(), true);
        return this;
    }

    /**
     * 显示当前列，与{@link #hide()}对应
     *
     * @return 当前 CellFactory
     */
    public CellWriter show() {
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
     * @see SheetWriter#setColumnsWidth(Integer...)
     * @see SheetWriter#setWidth(int, int)
     */
    public CellWriter width(int width) {
        getProxy().setColumnWidth(getSheet(), getColumnIndex(), width);
        return this;
    }

    /**
     * 设置当前列为在自动宽度
     *
     * @return 当前 CellFactory
     *
     * @see SheetWriter#columnsAutoWidth(int...)
     */
    public CellWriter autoWidth() {
        Sheet sheet = getSheet();
        int index = getColumnIndex();
        if (getProxy().isAllowAutoWidth(sheet, index)) {
            sheet.autoSizeColumn(index);
        }
        return this;
    }


    /**
     * 设置当前列为在自动宽度
     *
     * @param useMergedCells {@link Sheet#autoSizeColumn(int, boolean)}
     *
     * @return 当前 CellFactory
     *
     * @see SheetWriter#columnsAutoWidth(int...)
     * @see SheetWriter#columnsAutoWidth(boolean, int...)
     */
    public CellWriter autoWidth(boolean useMergedCells) {
        getSheet().autoSizeColumn(getColumnIndex(), useMergedCells);
        return this;
    }

    /**
     * 为当前单元格应用预定义注释
     * <p>
     * {@link CellWriter}提供了多种方式使用注释，如果是复杂的注释内容建议使用预定义方式进行集中管理
     *
     * @param definedCommentName 预定义注释唯一命名
     *
     * @return 当前 CellFactory
     */
    public CellWriter commentAs(String definedCommentName) {
        proxy.addSetter(ensureSetter(), definedCommentName);
        return this;
    }

    /**
     * 删除当前单元格的注释
     *
     * @return 当前 CellFactory
     */
    private CellWriter removeComment() {
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
    public CellWriter comment(String comment) {
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
    public CellWriter comment(Comment comment) {
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
    public CellWriter active() {
        getCell().setAsActiveCell();
        return this;
    }

    /*
     * ~~~~~~~~ 设置单元格值 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * 给当前单元格设置值
     *
     * @param value 将要设置的值
     *
     * @return 当前 CellFactory
     */
    public CellWriter val(Date value) {
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
    public CellWriter val(String value) {
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
    public CellWriter val(double value) {
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
    public CellWriter val(boolean value) {
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
    public CellWriter val(Calendar value) {
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
    public CellWriter val(LocalDate value) {
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
    public CellWriter val(LocalDateTime value) {
        getCell().setCellValue(value);
        return this;
    }

    ImageWriter image() {
        return obtainImageFactory();
    }

    /**
     * 设置空值
     *
     * @return 当前 CellFactory
     */
    public CellWriter valBlank() {
        getCell().setBlank();
        return this;
    }

    /*
     * ~~~~~~~~ 读取或使用单元格值 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * 读取当前单元格的值
     *
     * @return 当前单元格的值
     */
    public Object getValue() { return ExcelUtil.getValue(getCell()); }

    /**
     * 以指定类型获取值
     *
     * @param type 指定类型
     * @param <T>  类型
     *
     * @return 当前单元格的值
     */
    public <T> T getValue(Class<? extends T> type) {
        return caster.toType(getValue(), type);
    }

    /**
     * 一指定类型获取值
     *
     * @param converter 转换器
     * @param <T>       类型
     *
     * @return 当前单元格的值
     */
    public <T> T getValue(Function<Object, ? extends T> converter) {
        return converter.apply(getValue());
    }

    /**
     * 读取并使用单元格的内容
     *
     * @param consumer 消费者
     *
     * @return 当前 CellFactory
     */
    public CellWriter useVal(Consumer<Object> consumer) {
        consumer.accept(getValue());
        return this;
    }

    /**
     * 读取并使用单元格的内容
     *
     * @param consumer 消费者
     * @param <T>      类型
     *
     * @return 当前 CellFactory
     */
    public <T> CellWriter useVal(Class<? extends T> type, Consumer<? super T> consumer) {
        consumer.accept(getValue(type));
        return this;
    }

    /**
     * 读取并使用单元格的内容
     *
     * @param consumer 消费者
     * @param <T>      类型
     *
     * @return 当前 CellFactory
     */
    public <T> CellWriter useVal(Function<Object, ? extends T> converter, Consumer<? super T> consumer) {
        consumer.accept(getValue(converter));
        return this;
    }

    /**
     * 完全控制当前 cell
     *
     * @param consumer consumer
     *
     * @return 当前 CellFactory
     */
    public CellWriter controlCell(Consumer<? super Cell> consumer) {
        consumer.accept(getCell());
        return this;
    }

    /**
     * 完全控制当前 cell, 并返回一个值
     *
     * @param transformer transformer 转换器
     *
     * @return 转换后的值
     */
    public <T> T transform(Function<? super Cell, T> transformer) {
        return transformer.apply(getCell());
    }
}
