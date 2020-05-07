package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author benshaoye
 */
public class TemplateFactory extends BaseFactory<Sheet, TableFactory, SheetFactory> {

    private TemplateRegion region;
    /**
     * 当前正在操作的 sheet 表
     */
    private Sheet sheet;

    public TemplateFactory(WorkbookProxy proxy, SheetFactory parent) { super(proxy, parent); }

    public Sheet getSheet() { return sheet; }

    void setSheet(Sheet sheet) { this.sheet = sheet; }

    public TemplateRegion getRegion() { return region; }

    void setRegion(TemplateRegion region) { this.region = region; }

    @Override
    protected Sheet get() { return getSheet(); }
}
