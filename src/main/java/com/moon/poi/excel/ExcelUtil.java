package com.moon.poi.excel;

import com.moon.core.util.TypeUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * 此工具提供 Excel 写功能，基于 poi 4.x 开发，但默认并未导入 poi 相关依赖；
 * <p>
 * 工具屏蔽了绝大多数具体实现，只需要在创建时指定类型（{@link #xls()}、{@link #xlsx()}）即可，
 * <p>
 * 极少可能会用到{@link #stream()}
 * <p>
 * <p>
 * 设计思路是参照 html 方式设计，它具有 html 类似的结构，如咱们可以这样使用它：
 * <p>
 * <pre>
 * ``` java
 * public void test() {
 *
 *     // 创建 excelFactory
 *     ExcelFactory excelFactory = ExcelUtil.xlsx();
 *
 *     // 创建一个命名工作簿，如果指定名称工作簿存在就直接使用，否则就新建
 *     // 如果是已存在的 sheet，可传入 append 参数，
 *     // 表示数据默认是追加还是覆盖，默认 append = true；
 *     excelFactory.sheet("sheetName", sheetFactory -> {
 *
 *         // 设置列宽，按索引设置列宽，忽略 null，
 *         // 可用 null 来跳过设置某些列
 *         sheetFactory.setColumnsWidth(3000,4000,null,5000,6000);
 *
 *         // 定义样式，可以在任意位置定义样式，并在任意位置使用
 *         // 由于 excel 有限制 CellStyle 的数量上限，
 *         // 所以建议通过这“定义+引用”的方式使用
 *         // 尤其不要在循环里创建 CellStyle，
 *         sheetFactory.definitionStyle("classname", (style, font) > {
 *
 *             // 如果要设置字体，需要手动 setFont
 *             style.setFont(font);
 *         });
 *
 *         // 预定义一个注释
 *         sheetFactory.definitionComment("comment-name", "预定义注释");
 *
 *         // 可以通过链式调用创建“行”和“单元格”
 *         sheetFactory.row(rowFactory -> {
 *
 *             // 通过链式调用 next 可以不断创建单元格并设置值
 *             rowFactory.next(1).next(2).next(3);
 *
 *             // 如果需要对 cell 更多操作(如引用样式)可通过这种方式
 *             rowFactory.cell().val(4).style("unique-classname");
 *
 *             // 通过 comment 方法为单元格添加注释
 *             // 这个注释并不是预定义的，所以不受管理，而是立即执行添加
 *             rowFactory.cell().val(5).comment("这是一条注释");
 *
 *             rowFactory.cell(cellFactory -> {
 *                 // 这里通过 commentAs 使用预定义的注释
 *                 cellFactory.val(6).commentAs("defined-comment-unique-name")
 *             });
 *         }).row(rowFactory -> {
 *             // 合并单元格，这里通过传入 rowspan 和 colspan 参数
 *             // 合并 2 行 5 列单元格，rowspan 和 colspan 最小值均为 1
 *             // 如果小于 1 则具有回退或替换单元格的功能，
 *             // 但还是别这样用了吧...
 *             rowFactory.next("value", 2, 5);
 *         });
 *
 *     // 实际上预定义样式和注释都分别通过各自的“代理”分别管理，
 *     // 正因为如此，咱们才可以在任意位置定义，并在任意位置使用
 *     // 实际你要应用你所设置的样式和注释的话，
 *     // 需要手动调用 finish 方法才应用预定义样式和注释
 *     }).finish().write(...);
 *     // write 方法里也会调用 finish 方法，但可以重复调用
 *     // 可以通过 write 方法将构建好的 excel 写入到“文件”或“流”
 * }
 * ```
 * </pre>
 *
 * @author moonsky
 */
public final class ExcelUtil extends LoadUtil {

    private ExcelUtil() { noInstanceError(); }

    /**
     * 创建 Excel 2003 工厂
     *
     * @return Excel 操作工厂（代理）
     */
    public static WorkbookFactory xls() { return of(ExcelType.XLS); }

    /**
     * 创建 Excel 2007 工厂
     *
     * @return Excel 操作工厂（代理）
     */
    public static WorkbookFactory xlsx() { return of(ExcelType.XLSX); }

    /**
     * 创建 Excel 2007 工厂
     *
     * @return Excel 操作工厂（代理）
     */
    public static WorkbookFactory stream() { return of(ExcelType.SUPER); }

    /**
     * 使用预定义类型创建 Excel 工厂
     *
     * @param type 预定义 Excel 类型
     *
     * @return Excel 操作工厂（代理）
     */
    public static WorkbookFactory of(ExcelType type) { return of(type.get()); }

    /**
     * 自定义实现创建 Excel 工厂
     *
     * @param creator 自定义 Excel 创建器
     *
     * @return Excel 操作工厂（代理）
     */
    public static WorkbookFactory of(Supplier<Workbook> creator) { return of(creator.get()); }

    /**
     * 自定义创建 Excel 工厂
     *
     * @param workbook 自定义 Excel 工作簿
     *
     * @return Excel 操作工厂（代理）
     */
    public static WorkbookFactory of(Workbook workbook) { return new WorkbookFactory(workbook); }

    /**
     * 从已知 Excel 文件加载
     *
     * @param absoluteFilepath Excel 文件绝对路径
     *
     * @return Excel 操作工厂（代理）
     */
    public static WorkbookFactory load(String absoluteFilepath) { return load(new File(absoluteFilepath)); }

    /**
     * 从已知 Excel 文件加载
     *
     * @param absoluteFilepath Excel 文件绝对路径
     *
     * @return Excel 操作工厂（代理）
     */
    public static WorkbookFactory load(Path absoluteFilepath) { return load(absoluteFilepath.toFile()); }

    /**
     * 从已知 Excel 文件加载
     *
     * @param excelFile Excel 文件
     *
     * @return Excel 操作工厂（代理）
     *
     * @throws NullPointerException          excelFile is null
     * @throws UnsupportedOperationException excelFile type is unknown
     */
    public static WorkbookFactory load(File excelFile) {
        return load(excelFile, deduceType(excelFile).orElseGet(() -> {
            // 这里用文件内容的方式判断类型
            throw new UnsupportedOperationException("等待支持.");
        }));
    }

    /**
     * 从已知 Excel 文件加载
     *
     * @param excelFile Excel 文件
     * @param type      类型
     *
     * @return Excel 操作工厂（代理）
     */
    public static WorkbookFactory load(File excelFile, ExcelType type) { return of(type.load(excelFile)); }

    /**
     * 从已知 Excel 文件流加载
     *
     * @param excelInputStream Excel 输入流
     *
     * @return Excel 操作工厂（代理）
     */
    public static WorkbookFactory load(InputStream excelInputStream, ExcelType type) {
        return of(type.load(excelInputStream));
    }

    /**
     * 格式化文件名
     *
     * @param originFilename 文件名
     * @param workbook       Excel 文档
     *
     * @return 符合格式的文件名
     */
    public static String formatFilename(String originFilename, Workbook workbook) {
        for (ExcelType value : ExcelType.values()) {
            if (value.test(workbook)) {
                return value.formatFilename(originFilename);
            }
        }
        return originFilename;
    }

    /**
     * 获取单元格的值
     *
     * @param cell 单元格对象
     *
     * @return 单元格的值
     */
    public static Object getValue(Cell cell) {
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                return cell.getNumericCellValue();
            case ERROR:
                return cell.getErrorCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case BLANK:
            case _NONE:
            default:
                return null;
        }
    }

    /**
     * 获取单元格的值，并转换
     *
     * @param cell      单元格
     * @param converter 转换器
     * @param <T>       返回值类型
     *
     * @return 返回转换后的值
     */
    public static <T> T getValue(Cell cell, Function<Object, ? extends T> converter) {
        return converter.apply(getValue(cell));
    }

    /**
     * 以指定类型获取单元格值
     *
     * @param cell 单元格
     * @param type 类型
     * @param <T>  类型
     *
     * @return 指定类型的单元格值
     */
    public static <T> T getValueAs(Cell cell, Class<T> type) {
        if (cell == null || type == null) { return null; }
        Object value = getValue(cell);
        return TypeUtil.cast().toType(value, type);
    }

    /**
     * 合并单元格
     *
     * @param sheet    sheet
     * @param firstRow 起始行
     * @param lastRow  结束行
     * @param firstCol 起始列
     * @param lastCol  结束列
     *
     * @return sheet
     */
    public static Sheet merge(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        sheet.addMergedRegion(range(firstRow, lastRow, firstCol, lastCol));
        return sheet;
    }

    /**
     * 合并单元格
     *
     * @param firstRow 开始行号，从 0 开始
     * @param firstCol 开始列号，从 0 开始
     * @param rowspan  count of rows
     * @param colspan  count of columns
     *
     * @return CellRangeAddress
     */
    public static CellRangeAddress region(int firstRow, int firstCol, int rowspan, int colspan) {
        return range(firstRow, firstRow + rowspan - 1, firstCol, firstCol + colspan - 1);
    }

    /**
     * 合并单元格
     *
     * @param firstRow 开始行
     * @param lastRow  结束行
     * @param firstCol 开始列
     * @param lastCol  结束列
     *
     * @return CellRangeAddress
     */
    public static CellRangeAddress range(int firstRow, int lastRow, int firstCol, int lastCol) {
        return new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
    }
}
