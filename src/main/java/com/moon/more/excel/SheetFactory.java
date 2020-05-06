package com.moon.more.excel;

import com.moon.more.excel.annotation.DataColumn;
import com.moon.more.excel.annotation.DataColumnFlatten;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 工作表操作器
 *
 * @author benshaoye
 */
public class SheetFactory extends BaseFactory<Sheet, SheetFactory, WorkbookFactory> {

    /**
     * row 工厂
     */
    private final RowFactory factory;
    /**
     * 当前正在操作的 sheet 表
     */
    private Sheet sheet;

    /**
     * 创建 sheet 工厂
     *
     * @param proxy  代理中心
     * @param parent 父节点
     */
    public SheetFactory(WorkbookProxy proxy, WorkbookFactory parent) {
        super(proxy, parent);
        factory = new RowFactory(proxy, this);
    }

    /**
     * 获取正在操作的 sheet 表
     *
     * @return 正在操作的 sheet 表
     */
    public Sheet getSheet() { return sheet; }

    /**
     * 设置将要操作的 sheet 表
     *
     * @param sheet 将要操作的 sheet 表
     */
    void setSheet(Sheet sheet) { this.sheet = sheet; }

    /**
     * 获取正在操作的 sheet 表
     *
     * @return 正在操作的 sheet 表
     */
    @Override
    Sheet get() { return getSheet(); }

    private RowFactory getRowFactory() { return this.factory; }

    /**
     * 预定义注释
     *
     * @param uniqueName     整个工作簿注释唯一命名
     * @param commentBuilder 将要如何定义注释
     *
     * @return 当前对象
     */
    @Override
    public SheetFactory definitionComment(String uniqueName, Consumer<Comment> commentBuilder) {
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
    public SheetFactory definitionComment(String uniqueName, String comment) {
        super.definitionComment(uniqueName, comment);
        return this;
    }

    /**
     * 给指定索引列设置自动宽度
     *
     * @param useMergedCells {@link Sheet#autoSizeColumn(int, boolean)}
     * @param columnIndexes  索引列列表
     *
     * @return 当前 SheetFactory
     */
    public SheetFactory columnsAutoWidth(boolean useMergedCells, int... columnIndexes) {
        if (columnIndexes != null) {
            int length = columnIndexes.length;
            for (int i = 0; i < length; i++) {
                sheet.autoSizeColumn(columnIndexes[i], useMergedCells);
            }
        }
        return this;
    }

    /**
     * 给指定索引列设置自动宽度
     *
     * @param columnIndexes 索引列列表
     *
     * @return 当前 SheetFactory
     */
    public SheetFactory columnsAutoWidth(int... columnIndexes) {
        if (columnIndexes != null) {
            int length = columnIndexes.length;
            for (int i = 0; i < length; i++) {
                sheet.autoSizeColumn(columnIndexes[i]);
            }
        }
        return this;
    }

    /**
     * 按索引给各列设置指定宽度
     *
     * @param widths 每个值代表当前索引列的宽度，null 为不设置
     *
     * @return 当前 SheetFactory
     */
    public SheetFactory setColumnsWidth(Integer... widths) {
        int len = widths == null ? 0 : widths.length;
        if (len > 0) {
            Integer columnWidth;
            Sheet sheet = getSheet();
            for (int i = 0; i < len; i++) {
                if ((columnWidth = widths[i]) != null) {
                    sheet.setColumnWidth(i, columnWidth);
                }
            }
        }
        return this;
    }

    /**
     * 给指定索引列设置指定宽度
     *
     * @param columnIndex 指定列，从 0 开始
     * @param width       宽度
     *
     * @return 当前 SheetFactory
     */
    public SheetFactory setWidth(int columnIndex, int width) {
        getSheet().setColumnWidth(columnIndex, width);
        return this;
    }

    /**
     * 设置当前 sheet 工作表为活动表
     *
     * @return 当前 SheetFactory
     */
    public SheetFactory active() {
        Workbook workbook = proxy.getWorkbook();
        workbook.setActiveSheet(workbook.getSheetIndex(getSheet()));
        return this;
    }

    /**
     * 使用并操作指定行，如果不存在则创建
     *
     * @param rowIndex 指定行号
     * @param consumer 操作器
     *
     * @return 当前 SheetFactory
     */
    public SheetFactory use(int rowIndex, Consumer<RowFactory> consumer) {
        return use(rowIndex, DFT_APPEND_TYPE, consumer);
    }

    /**
     * 使用并操作指定行，如果不存在则创建
     *
     * @param rowIndex 指定行号
     * @param append   如果指定行存在，并且有数据，操作方式是采取追加数据还是覆盖数据；
     *                 true: 追加新数据（默认）
     *                 false: 覆盖旧数据
     * @param consumer 操作器
     *
     * @return 当前 SheetFactory
     */
    public SheetFactory use(int rowIndex, boolean append, Consumer<RowFactory> consumer) {
        factory.setRow(proxy.useOrCreateRow(rowIndex, append));
        consumer.accept(getRowFactory());
        return this;
    }

    /**
     * 创建下一行并设置为当前操作行
     *
     * @param consumer 操作器
     *
     * @return 当前 SheetFactory
     */
    public SheetFactory row(Consumer<RowFactory> consumer) {
        factory.setRow(proxy.nextRow());
        consumer.accept(getRowFactory());
        return this;
    }

    /**
     * 跳过指定行数，创建新的一行并设置为当前操作行
     *
     * @param skipRows 跳过的行数
     * @param consumer 操作器
     *
     * @return 当前 SheetFactory
     */
    public SheetFactory row(int skipRows, Consumer<RowFactory> consumer) {
        factory.setRow(proxy.nextRow(skipRows));
        consumer.accept(getRowFactory());
        return this;
    }

    /**
     * 从下一行开始将 List 数据渲染到当前 sheet 表中
     * <p>
     * 可用{@link DataColumn}定义表头
     * <p>
     * 否则将按照字段顺序写入到表格，并且忽略集合、数组或 Map 字段
     * <p>
     * 关于集合字段的处理方式参考{@link DataColumnFlatten}
     *
     * @param collect     数据
     * @param targetClass 目标类型
     * @param <T>         数据类型
     */
    private <T> void renderList(Iterator<T> collect, Class<T> targetClass) { }

    private <T> void renderList(Iterator<T> collect) { }

    private <T> void renderList(Iterable<T> collect, Class<T> targetClass) {
        renderList(collect.iterator(), targetClass);
    }

    private <T> void renderList(Iterable<T> collect) { renderList(collect.iterator()); }

    private <T> void renderList(Stream<T> stream, Class<T> targetClass) { renderList(stream.iterator(), targetClass); }

    private <T> void renderList(Stream<T> stream) { renderList(stream.iterator()); }

    private <T> void renderList(T... collect) {
    }
}
