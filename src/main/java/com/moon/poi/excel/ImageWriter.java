package com.moon.poi.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * @author moonsky
 */
public class ImageWriter extends BaseWriter<Cell, ImageWriter, CellWriter> {

    /**
     * 当前正在操作的单元格
     */
    private Cell cell;

    public ImageWriter(WorkbookProxy proxy, CellWriter parent) {
        super(proxy, parent);
    }

    final ImageWriter setCell(Cell cell) {
        this.cell = cell;
        return this;
    }

    /**
     * 获取当前正在操作的对象
     *
     * @return 当前正在操作的对象
     */
    @Override
    protected Cell get() { return this.cell; }
}
