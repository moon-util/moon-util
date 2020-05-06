package com.moon.more.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public enum ExcelType implements Supplier<Workbook>, Predicate<String> {
    /**
     * Excel 2003
     */
    XLS(new XlsGetter()),
    /**
     * Excel 2007
     */
    XLSX(new XlsxGetter()),
    /**
     * Excel 2007 用于超大文件
     * <p>
     * 一般情况下极少用到
     */
    SUPER(new SuperGetter(), "xlsx");
    /**
     * excel workbook 创建器
     */
    private final WorkbookBuilder builder;

    private final String extension;

    Workbook load(InputStream stream) { return builder.apply(stream); }

    Workbook load(File file) { return builder.load(file); }

    ExcelType(WorkbookBuilder creator) {
        this.extension = name().toLowerCase();
        this.builder = creator;
    }

    ExcelType(WorkbookBuilder creator, String extension) {
        this.extension = extension;
        this.builder = creator;
    }

    /**
     * 创建空工作簿
     *
     * @return an empty workbook
     */
    @Override
    public Workbook get() { return builder.get(); }

    /**
     * 创建 ExcelFactory
     *
     * @return ExcelFactory
     */
    public WorkbookFactory newFactory() { return new WorkbookFactory(get()); }

    /**
     * 检测文件名是否匹配
     *
     * @param filepath 文件名/绝对路径
     *
     * @return 匹配: true，不匹配：false
     */
    @Override
    public boolean test(String filepath) {
        int index = filepath == null ? -1 : filepath.lastIndexOf('.');
        return index > 0 && filepath.substring(index + 1).equalsIgnoreCase(extension);
    }

    interface WorkbookBuilder extends Supplier<Workbook>, Function<InputStream, Workbook> {

        /**
         * 加载 Excel 文档
         *
         * @param file excel 文件
         *
         * @return excel Workbook
         *
         * @throws IOException
         * @throws InvalidFormatException
         * @throws IllegalStateException
         */
        Workbook load(File file);
    }

    static class XlsGetter implements WorkbookBuilder {

        @Override
        public Workbook get() { return new HSSFWorkbook(); }

        @Override
        public Workbook apply(InputStream stream) {
            try {
                return new HSSFWorkbook(stream);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public Workbook load(File file) {
            try {
                return new HSSFWorkbook(POIFSFileSystem.create(file));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    static class XlsxGetter implements WorkbookBuilder {

        @Override
        public Workbook get() { return new XSSFWorkbook(); }

        @Override
        public Workbook apply(InputStream stream) {
            try {
                return new XSSFWorkbook(stream);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public Workbook load(File file) {
            try {
                return new XSSFWorkbook(file);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    static class SuperGetter implements WorkbookBuilder {

        @Override
        public Workbook get() { return new SXSSFWorkbook(); }

        @Override
        public Workbook apply(InputStream stream) {
            try {
                return new SXSSFWorkbook(new XSSFWorkbook(stream));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public Workbook load(File file) {
            try {
                return new SXSSFWorkbook(new XSSFWorkbook(file));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
