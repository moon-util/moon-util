package com.moon.office.excel.creator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;

import java.util.Calendar;
import java.util.Date;

import static java.util.Objects.requireNonNull;

/**
 * @author benshaoye
 */
public class CellCreator extends BaseCreator{

    private Cell cell;

    private int currentIndexOfCell;
    private int currentIndexOfRow;

    CellCreator() { }

    CellCreator setCell(Cell cell, int currentIndexOfCell, int currentIndexOfRow) {
        this.cell = requireNonNull(cell);
        this.currentIndexOfCell = currentIndexOfCell;
        this.currentIndexOfRow = currentIndexOfRow;
        return this;
    }

    public int getCurrentCellIndex() { return currentIndexOfCell; }

    public int getCurrentRowIndex() { return currentIndexOfRow; }

    public Cell getCell() { return cell; }

    public CellCreator merge(int rowspan, int colspan) {
        return this;
    }

    /*
     * -----------------------------------------------------------
     * value set
     * -----------------------------------------------------------
     */

    public CellCreator nullableSet(double value) {
        cell.setCellValue(value);
        return this;
    }

    public CellCreator nullableSet(boolean value) {
        cell.setCellValue(value);
        return this;
    }

    public CellCreator nullableSet(Number value) {
        return value == null ? this : nullableSet(value.doubleValue());
    }

    public CellCreator nullableSet(String value) {
        if (value != null) { cell.setCellValue(value); }
        return this;
    }

    public CellCreator nullableSet(Date value) {
        if (value != null) { cell.setCellValue(value); }
        return this;
    }

    public CellCreator nullableSet(Calendar value) {
        if (value != null) { cell.setCellValue(value); }
        return this;
    }

    public CellCreator nullableSet(RichTextString value) {
        if (value != null) { cell.setCellValue(value); }
        return this;
    }

    public CellCreator requireSet(double value) {
        cell.setCellValue(value);
        return this;
    }

    public CellCreator requireSet(boolean value) {
        cell.setCellValue(value);
        return this;
    }

    public CellCreator requireSet(Number value) { return requireSet(value.doubleValue()); }

    public CellCreator requireSet(String value) {
        cell.setCellValue(requireNonNull(value));
        return this;
    }

    public CellCreator requireSet(Date value) {
        cell.setCellValue(requireNonNull(value));
        return this;
    }

    public CellCreator requireSet(Calendar value) {
        cell.setCellValue(requireNonNull(value));
        return this;
    }

    public CellCreator requireSet(RichTextString value) {
        cell.setCellValue(requireNonNull(value));
        return this;
    }

    public CellCreator set(double value) { return requireSet(value); }

    public CellCreator set(boolean value) { return requireSet(value); }

    public CellCreator set(Number value) { return requireSet(value); }

    public CellCreator set(String value) { return requireSet(value); }

    public CellCreator set(Date value) { return requireSet(value); }

    public CellCreator set(Calendar value) { return requireSet(value); }

    public CellCreator set(RichTextString value) { return requireSet(value); }

}
