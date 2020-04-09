package com.moon.more.excel;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
enum WorkbookType {
    SUPER,
    XLSX,
    XLS(HSSFClientAnchor::new, HSSFRichTextString::new);

    private final Supplier<ClientAnchor> anchorCreator;
    private final Function<String, RichTextString> richTextBuilder;

    WorkbookType() { this(XSSFClientAnchor::new, XSSFRichTextString::new); }

    WorkbookType(
        Supplier<ClientAnchor> anchorCreator, Function<String, RichTextString> richTextBuilder
    ) {
        this.richTextBuilder = richTextBuilder;
        this.anchorCreator = anchorCreator;
    }

    RichTextString newRichText(String content) { return richTextBuilder.apply(content); }

    ClientAnchor newAnchor() { return anchorCreator.get(); }
}
