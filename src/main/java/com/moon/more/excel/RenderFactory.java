package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author benshaoye
 */
 class RenderFactory extends BaseFactory<Sheet, RenderFactory, SheetFactory> {

    /**
     * 当前正在操作的 sheet 表
     */
    private Sheet sheet;

    RenderFactory(WorkbookProxy proxy, SheetFactory parent) {
        super(proxy, parent);
    }

    /**
     * 获取正在操作的 sheet 表
     *
     * @return 正在操作的 sheet 表
     */
    public Sheet getSheet() { return sheet; }

    /**
     * 设置将要操作的 sheet 表
     *
     * @param sheet 将要操作的 sheet 表
     */
    void setSheet(Sheet sheet) { this.sheet = sheet; }

    @Override
    Sheet get() { return getSheet(); }
}
