package com.moon.poi.excel;

import com.moon.core.io.IOUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * @author moonsky
 */
public class ExcelWriter extends BaseWriter<Workbook, ExcelWriter, ExcelWriter> {

    private final SheetWriter factory;

    public ExcelWriter(Workbook workbook) {
        super(new WorkbookProxy(workbook), null);
        factory = new SheetWriter(proxy, this);
    }

    /**
     * 获取正在操作的 Excel 工作簿
     *
     * @return 正在操作的 Excel 工作簿
     */
    public Workbook getWorkbook() { return proxy.getWorkbook(); }

    /**
     * 获取正在操作的 Excel 工作簿
     *
     * @return 正在操作的 Excel 工作簿
     */
    @Override
    protected Workbook get() { return proxy.getWorkbook(); }

    private SheetWriter getSheetFactory() { return factory; }

    /**
     * 创建工作表，如果存在则使用已存在的
     *
     * @param sheetName sheet name
     * @param append    如果是已存在的工作表，这个是决定是“覆盖”或者“追加”到工作表数据
     * @param operator  sheet 操作器
     *
     * @return 当前 WorkbookFactory
     */
    public ExcelWriter sheet(String sheetName, boolean append, Consumer<SheetWriter> operator) {
        factory.setSheet(proxy.useSheet(sheetName, append));
        operator.accept(getSheetFactory());
        return this;
    }

    /**
     * 使用或创建指定位置工作表，如果存在则使用已存在的
     *
     * @param index    sheet 位置索引
     * @param append   是否采用追加行方式，默认{@code true}
     * @param operator sheet 操作器
     *
     * @return 当前 WorkbookFactory
     */
    public ExcelWriter sheetAt(int index, boolean append, Consumer<SheetWriter> operator) {
        factory.setSheet(proxy.useSheet(index, append));
        operator.accept(getSheetFactory());
        return this;
    }

    /**
     * 使用或创建指定位置工作表，如果存在则使用已存在的
     *
     * @param index    sheet 位置索引
     * @param operator sheet 操作器
     *
     * @return 当前 WorkbookFactory
     */
    public ExcelWriter sheetAt(int index, Consumer<SheetWriter> operator) {
        return sheetAt(index, true, operator);
    }

    /**
     * 创建工作表，如果存在则使用已存在的
     *
     * @param sheetName sheet name
     * @param operator  sheet 操作器
     *
     * @return 当前 WorkbookFactory
     */
    public ExcelWriter sheet(String sheetName, Consumer<SheetWriter> operator) {
        return sheet(sheetName, DFT_APPEND_TYPE, operator);
    }

    /**
     * 创建一个工作表
     *
     * @param operator sheet 操作器
     *
     * @return 当前 WorkbookFactory
     */
    public ExcelWriter sheet(Consumer<SheetWriter> operator) { return sheet(null, operator); }

    /**
     * 将 Excel 写入到指定路径文件
     *
     * @param path 将要写出的目标文件路径
     */
    public void write(String path) { write(Paths.get(path)); }

    /**
     * 将 Excel 写入到指定文件
     *
     * @param file 将要写出的目标文件
     */
    public void write(File file) { write(file.toPath()); }

    /**
     * 将 Excel 写入到指定位置
     *
     * @param path 将要写出的目标路径
     */
    public void write(Path path) {
        OutputStream out = null;
        try {
            if (!Files.exists(path)) {
                File dir = path.toFile().getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Files.createFile(path);
            }
            out = Files.newOutputStream(path);
            write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil.close(out);
        }
    }

    /**
     * 将 Excel 写入到输出流
     *
     * @param outputStream 写出的目标输出流
     */
    public void write(OutputStream outputStream) {
        finish();
        try {
            proxy.getWorkbook().write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
