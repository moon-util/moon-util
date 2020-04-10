package com.moon.more.excel;

import com.moon.core.io.IOUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class ExcelFactory extends BaseFactory<Workbook, ExcelFactory, ExcelFactory> {

    private final SheetFactory factory;

    public ExcelFactory(Workbook workbook) {
        super(new WorkbookProxy(workbook), null);
        factory = new SheetFactory(proxy, this);
    }

    /**
     * 获取正在操作的 sheet
     *
     * @return
     */
    public Workbook getWorkbook() { return proxy.getWorkbook(); }

    @Override
    Workbook get() { return proxy.getWorkbook(); }

    private SheetFactory getSheetFactory() { return factory; }

    /**
     * 创建工作表，如果存在则使用已存在的
     *
     * @param sheetName
     * @param append    如果是已存在的工作表，这个是决定是“覆盖”或者“追加”到工作表数据
     * @param operator
     *
     * @return
     */
    public ExcelFactory sheet(String sheetName, boolean append, Consumer<SheetFactory> operator) {
        factory.setSheet(proxy.useSheet(sheetName, append));
        operator.accept(getSheetFactory());
        return this;
    }

    /**
     * 创建工作表，如果存在则使用已存在的
     *
     * @param sheetName
     * @param operator
     *
     * @return
     */
    public ExcelFactory sheet(String sheetName, Consumer<SheetFactory> operator) {
        return sheet(sheetName, DFT_APPEND_TYPE, operator);
    }

    /**
     * 创建一个工作表
     *
     * @param operator
     *
     * @return
     */
    public ExcelFactory sheet(Consumer<SheetFactory> operator) { return sheet(null, operator); }

    /**
     * 将 Excel 写入到指定路径文件
     *
     * @param path
     */
    public void write2Filepath(String path) { write2Path(Paths.get(path)); }

    /**
     * 将 Excel 写入到指定文件
     *
     * @param file
     */
    public void write2File(File file) { write2Path(file.toPath()); }

    /**
     * 将 Excel 写入到指定位置
     *
     * @param path
     */
    public void write2Path(Path path) {
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
     * @param outputStream
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
