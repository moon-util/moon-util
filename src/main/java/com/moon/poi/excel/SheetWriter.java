package com.moon.poi.excel;

import com.moon.poi.excel.annotation.TableColumn;
import com.moon.poi.excel.annotation.TableColumnGroup;
import com.moon.poi.excel.annotation.TableRecord;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.Consumer;

/**
 * 工作表操作器
 *
 * @author moonsky
 */
public class SheetWriter extends BaseWriter<Sheet, SheetWriter, ExcelWriter> {

    /**
     * @see Sheet#setColumnWidth(int, int)
     */
    private final static int MAX_WIDTH = 255 * 256;
    /**
     * row 工厂
     */
    private final RowWriter factory;
    private final TemplateWriter templateFactory;
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
    public SheetWriter(WorkbookProxy proxy, ExcelWriter parent) {
        super(proxy, parent);
        factory = new RowWriter(proxy, this);
        templateFactory = new TemplateWriter(proxy, this);
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
    protected Sheet get() { return getSheet(); }

    private RowWriter getRowFactory() { return this.factory; }

    /**
     * 预定义注释
     *
     * @param uniqueName     整个工作簿注释唯一命名
     * @param commentBuilder 将要如何定义注释
     *
     * @return 当前对象
     */
    @Override
    public SheetWriter definitionComment(String uniqueName, Consumer<Comment> commentBuilder) {
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
    public SheetWriter definitionComment(String uniqueName, String comment) {
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
     *
     * @see Sheet#autoSizeColumn(int, boolean)
     */
    public SheetWriter columnsAutoWidth(boolean useMergedCells, int... columnIndexes) {
        if (columnIndexes != null) {
            for (int columnIndex : columnIndexes) {
                sheet.autoSizeColumn(columnIndex, useMergedCells);
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
     *
     * @see Sheet#autoSizeColumn(int)
     */
    public SheetWriter columnsAutoWidth(int... columnIndexes) {
        if (columnIndexes != null) {
            for (int columnIndex : columnIndexes) {
                sheet.autoSizeColumn(columnIndex);
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
    public SheetWriter setColumnsWidth(Integer... widths) { return setColumnsWidthBegin(0, widths); }

    /**
     * 按索引给各列设置指定宽度
     *
     * @param startIdx 起始列号
     * @param widths   每个值代表当前索引列的宽度，null 为不设置
     *
     * @return 当前 SheetFactory
     */
    public SheetWriter setColumnsWidthBegin(int startIdx, Integer[] widths) {
        int len = widths == null ? 0 : widths.length;
        if (len > 0) {
            Integer columnWidth;
            Sheet sheet = getSheet();
            WorkbookProxy proxy = getProxy();
            for (int i = 0; i < len; i++) {
                if ((columnWidth = widths[i]) != null) {
                    int index = i + startIdx;
                    int width = columnWidth;
                    if (width > -1) {
                        if (width < MAX_WIDTH) {
                            proxy.setColumnWidth(sheet, index, width);
                        } else if (proxy.isAllowAutoWidth(sheet, index)) {
                            sheet.autoSizeColumn(index);
                        }
                    }
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
    public SheetWriter setWidth(int columnIndex, int width) {
        getProxy().setColumnWidth(getSheet(), columnIndex, width);
        return this;
    }

    /**
     * 设置当前 sheet 工作表为活动表
     *
     * @return 当前 SheetFactory
     */
    public SheetWriter active() {
        Workbook workbook = proxy.getWorkbook();
        workbook.setActiveSheet(workbook.getSheetIndex(getSheet()));
        return this;
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * create row & handle current row
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    /**
     * 创建下一行并设置为当前操作行
     *
     * @param consumer 操作器
     *
     * @return 当前 SheetFactory
     */
    public SheetWriter row(Consumer<RowWriter> consumer) {
        RowWriter rowFactory = getRowFactory();
        rowFactory.setRow(proxy.nextRow());
        consumer.accept(rowFactory);
        return this;
    }

    /**
     * 跳过指定行数，创建新的一行并设置为当前操作行
     *
     * @param offset   行位置偏移量
     * @param consumer 操作器
     *
     * @return 当前 SheetFactory
     */
    public SheetWriter row(int offset, Consumer<RowWriter> consumer) {
        RowWriter rowFactory = getRowFactory();
        rowFactory.setRow(proxy.nextRow(offset));
        consumer.accept(rowFactory);
        return this;
    }

    /**
     * 创建下一行并设置为当前操作行
     *
     * @return 行操作器
     */
    public RowWriter row() {
        RowWriter rowFactory = getRowFactory();
        rowFactory.setRow(proxy.nextRow());
        return rowFactory;
    }

    /**
     * 跳过指定行数，创建新的一行并设置为当前操作行
     *
     * @param offset 行位置偏移量
     *
     * @return 行操作器
     */
    public RowWriter row(int offset) {
        RowWriter rowFactory = getRowFactory();
        rowFactory.setRow(proxy.nextRow(offset));
        return rowFactory;
    }

    /**
     * 使用并操作指定行，如果不存在则创建
     *
     * @param rowIndex 指定行号
     * @param consumer 操作器
     *
     * @return 当前 SheetFactory
     */
    public SheetWriter useRow(int rowIndex, Consumer<RowWriter> consumer) {
        return useRow(rowIndex, DFT_APPEND_TYPE, consumer);
    }

    /**
     * 使用并操作指定行，如果不存在则创建
     *
     * @param rowIndex   指定行号
     * @param appendCell 如果指定行存在，并且有数据，操作方式是采取追加数据还是覆盖数据；
     *                   true: 追加新数据（默认）
     *                   false: 覆盖旧数据
     * @param consumer   操作器
     *
     * @return 当前 SheetFactory
     */
    public SheetWriter useRow(int rowIndex, boolean appendCell, Consumer<RowWriter> consumer) {
        consumer.accept(useRow(rowIndex, appendCell));
        return this;
    }

    /**
     * 使用并操作指定行，如果不存在则创建
     *
     * @param rowIndex 指定行号
     *
     * @return 行操作器
     */
    public RowWriter useRow(int rowIndex) {
        return useRow(rowIndex, DFT_APPEND_TYPE);
    }

    /**
     * 使用并操作指定行，如果不存在则创建
     *
     * @param rowIndex   指定行号
     * @param appendCell 如果指定行存在，并且有数据，那么在操作单元格时是采取追加数据还是覆盖数据的方式；
     *                   true: 追加新数据（默认）
     *                   false: 覆盖旧数据
     *
     * @return 行操作器
     */
    public RowWriter useRow(int rowIndex, boolean appendCell) {
        RowWriter rowFactory = getRowFactory();
        rowFactory.setRow(proxy.useOrCreateRow(rowIndex, appendCell));
        return rowFactory;
    }

    /**
     * 遍历处理每行数据
     *
     * @param consumer 处理器
     *
     * @return 当前 SheetFactory
     */
    public SheetWriter forEach(Consumer<RowWriter> consumer) {
        return forEach(0, consumer);
    }

    /**
     * 遍历处理每行数据
     *
     * @param fromRowIndex 从哪一行开始
     * @param consumer     处理器
     *
     * @return 当前 SheetFactory
     */
    public SheetWriter forEach(int fromRowIndex, Consumer<RowWriter> consumer) {
        return forEach(fromRowIndex, getSheet().getLastRowNum() + 1, consumer);
    }

    /**
     * 遍历处理每行数据
     *
     * @param fromRowIndex 从哪一行开始
     * @param toRowIndex   至哪一行结束
     * @param consumer     处理器
     *
     * @return 当前 SheetFactory
     */
    public SheetWriter forEach(int fromRowIndex, int toRowIndex, Consumer<RowWriter> consumer) {
        RowWriter rowFactory = getRowFactory();
        for (int i = fromRowIndex; i < toRowIndex; i++) {
            rowFactory.setRow(proxy.useOrCreateRow(i, true));
            consumer.accept(rowFactory);
        }
        return this;
    }

    /**
     * 聚合所有行
     *
     * @return 当前对象 SheetFactory
     */
    private SheetWriter reduce() {
        throw new UnsupportedOperationException();
    }

    private SheetWriter reduceAll() {
        throw new UnsupportedOperationException();
    }

    private static class Boundary {

    }

    /**
     * 注解式表格渲染
     *
     * @param consumer 表格渲染处理器
     *
     * @return 当前对象 SheetFactory
     *
     * @see TableColumn at field or method
     * @see TableColumnGroup at field or method
     * @see TableIndexer at field or method if present {@link TableColumn} or {@link TableColumnGroup}
     * @see TableRecord at type
     */
    public SheetWriter table(Consumer<TableWriter> consumer) {
        TableWriter factory = new TableWriter(proxy, this);
        factory.setSheet(getSheet());
        consumer.accept(factory);
        return this;
    }

    private SheetWriter table(int rowIndex, int colIndex) {
        return this;
    }

    /**
     * 模板渲染，在 sheet 中划定一块区域作为模板
     * 模板中可以包含占位符、取值符号等
     * 每个数据都用这个模板渲染并替换其中符合条件的占位符
     * <p>
     * 可以横向或纵向自由伸展，但请注意不要超过 sheet 的最大边界
     *
     * @param consumer 模板渲染处理器
     *
     * @return 当前对象 SheetFactory
     */
    SheetWriter template(TemplateRegion region, Consumer<TemplateWriter> consumer) {
        templateFactory.setSheet(getSheet());
        templateFactory.setRegion(region);
        consumer.accept(templateFactory);
        return this;
    }
}
