package com.moon.poi.excel;

import org.apache.poi.ss.usermodel.Cell;

/**
 * @author moonsky
 */
public class ImageFactory extends BaseFactory<Cell, ImageFactory, CellFactory> {

    /**
     * 当前正在操作的单元格
     */
    private Cell cell;

    public ImageFactory(WorkbookProxy proxy, CellFactory parent) {
        super(proxy, parent);
    }

    final ImageFactory setCell(Cell cell) {
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
