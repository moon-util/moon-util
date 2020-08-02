package com.moon.poi.excel;

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
 * @author moonsky
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

    private final AnchorSupplier anchorCreator;
    private final Function<String, RichTextString> richTextBuilder;
    private final Class target;

    WorkbookType(String type) { this(type, new XAnchor(), new XRich()); }

    WorkbookType(
        String type, AnchorSupplier anchorCreator, Function<String, RichTextString> richTextBuilder
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

    ClientAnchor newAnchor(int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
        return anchorCreator.apply(dx1, dy1, dx2, dy2, col1, row1, col2, row2);
    }

    private static class HRich implements Function<String, RichTextString> {

        @Override
        public RichTextString apply(String s) { return new HSSFRichTextString(s); }
    }

    private static class XRich implements Function<String, RichTextString> {

        @Override
        public RichTextString apply(String s) { return new XSSFRichTextString(s); }
    }

    @SuppressWarnings("all")
    private interface AnchorSupplier extends Supplier<ClientAnchor> {

        @Override
        ClientAnchor get();

        ClientAnchor apply(int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2);
    }

    private static class HAnchor implements AnchorSupplier {

        @Override
        public ClientAnchor get() { return new HSSFClientAnchor(); }

        @Override
        public ClientAnchor apply(int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
            return new HSSFClientAnchor(dx1, dy1, dx2, dy2, (short) col1, row1, (short) col2, row2);
        }
    }

    private static class XAnchor implements AnchorSupplier {

        @Override
        public ClientAnchor get() { return new XSSFClientAnchor(); }

        @Override
        public ClientAnchor apply(int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
            return new XSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1, col2, row2);
        }
    }
}
