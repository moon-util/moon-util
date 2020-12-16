package com.moon.poi.excel;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author moonsky
 */
public class TemplateWriter extends BaseWriter<Sheet, TableWriter, SheetWriter> {

    private TemplateRegion region;
    /**
     * 当前正在操作的 sheet 表
     */
    private Sheet sheet;

    public TemplateWriter(WorkbookProxy proxy, SheetWriter parent) { super(proxy, parent); }

    public Sheet getSheet() { return sheet; }

    void setSheet(Sheet sheet) { this.sheet = sheet; }

    public TemplateRegion getRegion() { return region; }

    void setRegion(TemplateRegion region) { this.region = region; }

    @Override
    protected Sheet get() { return getSheet(); }
}
