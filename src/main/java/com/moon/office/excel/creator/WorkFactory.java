package com.moon.office.excel.creator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @author benshaoye
 */
class WorkFactory {

    private final Workbook workbook;

    private Sheet sheet;

    private Row row;

    private Cell cell;

    public WorkFactory(Workbook workbook) { this.workbook = workbook; }

    /*
     * --------------------------------------------------------------------------------
     * sheet
     * --------------------------------------------------------------------------------
     */

    public Sheet createSheet(String name) { return setSheet(workbook.createSheet(name)); }

    public Sheet createSheet() { return setSheet(workbook.createSheet()); }

    public Sheet withSheet(int index) { return setSheet(workbook.getSheetAt(index)); }

    public Sheet withSheet(String name) { return setSheet(workbook.getSheet(name)); }

    /*
     * --------------------------------------------------------------------------------
     * row
     * --------------------------------------------------------------------------------
     */

    public Row createRow(int index) { return setRow(sheet.createRow(index)); }

    public Row withRow(int index) { return setRow(sheet.getRow(index)); }

    /*
     * --------------------------------------------------------------------------------
     * cell
     * --------------------------------------------------------------------------------
     */

    public Cell createCell(int index) { return setCell(row.createCell(index)); }

    public Cell withCell(int index) { return setCell(row.getCell(index)); }

    /*
     * --------------------------------------------------------------------------------
     * getter and setter
     * --------------------------------------------------------------------------------
     */

    public Workbook getWorkbook() { return workbook; }

    public Sheet setSheet(Sheet sheet) { return this.sheet = sheet; }

    public Sheet getSheet() { return sheet; }

    public Row getRow() { return row; }

    public Row setRow(Row row) { return this.row = row; }

    public Cell getCell() { return cell; }

    public Cell setCell(Cell cell) { return this.cell = cell; }
}
