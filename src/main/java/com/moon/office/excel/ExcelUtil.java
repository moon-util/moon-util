package com.moon.office.excel;

import com.moon.office.excel.core.RendererUtil;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author benshaoye
 */
public final class ExcelUtil extends RendererUtil {
    private ExcelUtil() {
        super();
    }

    public final static Workbook render(Object... data) {
        return renderTo(null, data);
    }

    public final static Workbook renderTo(Workbook workbook, Object... data) {
        return parseAndRenderTo(workbook, data);
    }
}
