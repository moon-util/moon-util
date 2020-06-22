package com.moon.more.excel;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
enum WorkbookType implements Predicate<Workbook> {
    /**
     * Excel 2007
     */
    SUPER("org.apache.poi.xssf.streaming.SXSSFWorkbook"),
    /**
     * Excel 2007
     */
    XLSX("org.apache.poi.xssf.usermodel.XSSFWorkbook"),
    /**
     * Excel 2003
     */
    XLS("org.apache.poi.hssf.usermodel.HSSFWorkbook", new HAnchor(), new HRich());

    private final Supplier<ClientAnchor> anchorCreator;
    private final Function<String, RichTextString> richTextBuilder;
    private final Class target;

    WorkbookType(String type) { this(type, new XAnchor(), new XRich()); }

    WorkbookType(
        String type, Supplier<ClientAnchor> anchorCreator, Function<String, RichTextString> richTextBuilder
    ) {
        Class target;
        try {
            target = Class.forName(type);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            target = null;
        }
        this.target = target;
        this.richTextBuilder = richTextBuilder;
        this.anchorCreator = anchorCreator;
    }

    @Override
    public boolean test(Workbook workbook) {
        Class target = this.target;
        return target != null && target.isInstance(workbook);
    }

    RichTextString newRichText(String content) { return richTextBuilder.apply(content); }

    ClientAnchor newAnchor() { return anchorCreator.get(); }

    private static class HRich implements Function<String, RichTextString> {

        @Override
        public RichTextString apply(String s) { return new HSSFRichTextString(s); }
    }

    private static class XRich implements Function<String, RichTextString> {

        @Override
        public RichTextString apply(String s) { return new XSSFRichTextString(s); }
    }

    private static class HAnchor implements Supplier<ClientAnchor> {

        @Override
        public ClientAnchor get() { return new HSSFClientAnchor(); }
    }

    private static class XAnchor implements Supplier<ClientAnchor> {

        @Override
        public ClientAnchor get() { return new XSSFClientAnchor(); }
    }
}
