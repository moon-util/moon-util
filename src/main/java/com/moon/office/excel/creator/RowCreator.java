package com.moon.office.excel.creator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class RowCreator extends BaseCreator {

    private final CellCreator creator;

    private int indexOfCell;

    private Row row;

    private Cell cell;

    private int currentIndex;

    RowCreator() { creator = new CellCreator(); }

    RowCreator setRow(Row row, int currentIndex) {
        this.row = Objects.requireNonNull(row);
        this.currentIndex = currentIndex;
        this.indexOfCell = 0;
        return this;
    }

    private Cell create() { return cell = row.createCell(indexOfCell++); }

    public Row getCurrentRow() { return row; }

    public Cell getCurrentCell() { return cell; }

    public RowCreator skip() { return skip(1); }

    public RowCreator skip(int count) {
        indexOfCell += count;
        return this;
    }

    public int getCurrentRowIndex() { return currentIndex; }

    public int getCurrentCellIndex() { return indexOfCell; }

    /*
     * ----------------------------------------------------------------
     * create cell
     * ----------------------------------------------------------------
     */

    public RowCreator createCell(String value) {
        create().setCellValue(value);
        return this;
    }

    public RowCreator createCell(String value, Consumer<CellCreator> consumer) {
        createCell(value);
        consumer.accept(creator.setCell(cell, indexOfCell, currentIndex));
        return this;
    }

    public RowCreator createCell(double value) {
        create().setCellValue(value);
        return this;
    }

    public RowCreator createCell(double value, Consumer<CellCreator> consumer) {
        createCell(value);
        consumer.accept(creator.setCell(cell, indexOfCell, currentIndex));
        return this;
    }

    public RowCreator createCell(boolean value) {
        create().setCellValue(value);
        return this;
    }

    public RowCreator createCell(boolean value, Consumer<CellCreator> consumer) {
        createCell(value);
        consumer.accept(creator.setCell(cell, indexOfCell, currentIndex));
        return this;
    }

    public RowCreator createCell(Date value) {
        create().setCellValue(value);
        return this;
    }

    public RowCreator createCell(Date value, Consumer<CellCreator> consumer) {
        createCell(value);
        consumer.accept(creator.setCell(cell, indexOfCell, currentIndex));
        return this;
    }

    public RowCreator createCell(Calendar value) {
        create().setCellValue(value);
        return this;
    }

    public RowCreator createCell(Calendar value, Consumer<CellCreator> consumer) {
        createCell(value);
        consumer.accept(creator.setCell(cell, indexOfCell, currentIndex));
        return this;
    }

    public RowCreator createCell(RichTextString value) {
        create().setCellValue(value);
        return this;
    }

    public RowCreator createCell(RichTextString value, Consumer<CellCreator> consumer) {
        createCell(value);
        consumer.accept(creator.setCell(cell, indexOfCell, currentIndex));
        return this;
    }

    public RowCreator createCell(Consumer<CellCreator> consumer) {
        consumer.accept(creator.setCell(create(), indexOfCell, currentIndex));
        return this;
    }

    /*
     * ---------------------n-------------------------------------------
     * cell creator
     * ----------------------------------------------------------------
     */

    public CellCreator cell() { return creator.setCell(create(), indexOfCell, currentIndex); }
}
