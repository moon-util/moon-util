package com.moon.office.excel;

import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface Renderer {
    /**
     * 渲染到指定 Workbook 文档
     *
     * @param workbook
     * @param data
     * @return
     */
    Workbook renderTo(Workbook workbook, Object... data);

    /**
     * 使用当前配置信息渲染到默认配置 Workbook 文档
     *
     * @param data
     * @return
     */
    default Workbook render(Object... data) {
        return renderTo(null, data);
    }
}
