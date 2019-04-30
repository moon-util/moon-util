package com.moon.office.excel.core;

import com.moon.office.excel.Renderer;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.function.Supplier;

/**
 * @author benshaoye
 */
final class GenericRenderer implements Renderer {
    private final TableExcel excel;
    private final Supplier<Workbook> creator;
    private final CenterRenderer renderer;

    GenericRenderer(TableExcel excel) {
        this.excel = excel;
        this.creator = excel.type();
        this.renderer = RendererUtil.getOrParse(excel);
    }

    @Override
    public Workbook renderTo(Workbook workbook, Object... data) {
        WorkCenterMap centerMap = new WorkCenterMap(
            workbook == null ? creator.get() : workbook, data);
        return renderer.render(centerMap).get();
    }
}
