package com.moon.more.excel;

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
 * @author benshaoye
 */
public class ExcelFactory extends BaseFactory<Workbook, ExcelFactory> {

    private final SheetFactory factory = new SheetFactory(proxy);

    public ExcelFactory(Workbook workbook) { super(new WorkbookProxy(workbook)); }

    @Override
    Workbook get() { return proxy.getWorkbook(); }

    private SheetFactory getSheetFactory() { return factory; }

    public ExcelFactory sheet(Consumer<SheetFactory> operator) { return sheet(null, operator); }

    public ExcelFactory sheet(String sheetName, Consumer<SheetFactory> operator) {
        proxy.useSheet(sheetName);
        operator.accept(getSheetFactory());
        return this;
    }

    public void write2Filepath(String path) { write2Path(Paths.get(path)); }

    public void write2File(File file) { write2Path(file.toPath()); }

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

    public void write(OutputStream outputStream) {
        try {
            proxy.getWorkbook().write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
