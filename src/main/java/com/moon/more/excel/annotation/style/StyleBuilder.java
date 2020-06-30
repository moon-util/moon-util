package com.moon.more.excel.annotation.style;

import org.apache.poi.ss.usermodel.CellStyle;

import java.util.function.Consumer;

/**
 * @author moonsky
 */
public interface StyleBuilder extends Consumer<CellStyle> {

    /**
     * 自定义样式设置
     *
     * @param style 样式
     */
    @Override
    void accept(CellStyle style);
}
