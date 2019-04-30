package com.moon.office.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author benshaoye
 */
public class ColumnDescription<T> implements ColumnDescriptor<T> {
    /**
     * 默认宽度
     */
    private final static int DEFAULT_WIDTH = 3600;
    /**
     * 单元格默认值
     */
    private final CharSequence defaultVal;
    /**
     * 标题
     */
    private final String title;
    /**
     * 宽度
     */
    private final int width;
    /**
     * 值获取器
     */
    private final Function<T, ?> converter;

    /*
     * --------------------------------------------------------------------------
     * constructor
     * --------------------------------------------------------------------------
     */

    public ColumnDescription(String title, int width, Function<T, ?> converter) {
        this(title, width, converter, null);
    }

    public ColumnDescription(String title, int width, Function<T, ?> converter, CharSequence defaultVal) {
        this.defaultVal = defaultVal;
        this.converter = converter;
        this.title = title;
        this.width = width;
    }

    /*
     * --------------------------------------------------------------------------
     * static methods
     * --------------------------------------------------------------------------
     */

    public static <T> ColumnDescription<T> of(Function<T, ?> converter) { return of("#", converter); }

    public static <T> ColumnDescription<T> of(
        String title, Function<T, ?> converter
    ) { return of(title, DEFAULT_WIDTH, converter); }

    public static <T> ColumnDescription<T> of(
        String title, int width, Function<T, ?> converter
    ) { return new ColumnDescription<>(title, width, converter); }

    public static <T> ColumnDescription<T> of(
        String title, int width, Function<T, ?> converter, CharSequence defaultVal
    ) { return new ColumnDescription<>(title, width, converter, defaultVal); }

    /*
     * --------------------------------------------------------------------------
     * settings and operator
     * --------------------------------------------------------------------------
     */

    public static <T> Settings<T> settingWith(Class<T> c) { return new Settings(); }

    public static <T> Operator<T> operate(Sheet sheet, Class<T> c) { return new Operator<>(sheet, settingWith(c)); }

    /*
     * --------------------------------------------------------------------------
     * member methods
     * --------------------------------------------------------------------------
     */

    @Override
    public String getTitle() { return title; }

    @Override
    public Function<T, ?> getConverter() { return converter; }

    @Override
    public CharSequence getDefaultValue() { return defaultVal; }

    @Override
    public int getWidth() { return width; }

    /*
     * --------------------------------------------------------------------------
     * settingWith
     * --------------------------------------------------------------------------
     */

    public final static class Settings<T> {

        private Settings() {}

        private final ArrayList<ColumnDescription<T>> settings = new ArrayList<>();

        public Settings<T> add(ColumnDescription description) {
            settings.add(description);
            return this;
        }

        public Settings<T> add(String title, Function<T, ?> converter) {
            return add(of(title, converter));
        }

        public Settings<T> add(String title, int width, Function<T, ?> converter) {
            return add(of(title, width, converter));
        }

        public Settings<T> setColumnsWidth(Sheet sheet) { return setColumnsWidth(sheet, 0); }

        public Settings<T> setColumnsWidth(Sheet sheet, int firstColumnIndex) {
            for (ColumnDescription<T> set : settings) {
                set.setColumnWidth(sheet, firstColumnIndex++);
            }
            return this;
        }

        public Settings<T> setData(Sheet sheet, Collection<T> data) { return setData(sheet, data, 0); }

        public Settings<T> setData(Sheet sheet, T... data) { return setData(sheet, data, 0); }

        public Settings<T> setData(Sheet sheet, T[] data, int firstRowIndex) {
            setHead(sheet, firstRowIndex++);
            setBody(sheet, data, firstRowIndex);
            return this;
        }

        public Settings<T> setData(Sheet sheet, Collection<T> data, int firstRowIndex) {
            setHead(sheet, firstRowIndex++);
            setBody(sheet, data, firstRowIndex);
            return this;
        }

        public Settings<T> setData(Sheet sheet, T[] data, int firstRowIndex, final int firstColumnIndex) {
            setHead(sheet, firstRowIndex++, firstColumnIndex);
            setBody(sheet, data, firstRowIndex, firstColumnIndex);
            return this;
        }

        public Settings<T> setData(Sheet sheet, Collection<T> data, int firstRowIndex, final int firstColumnIndex) {
            setHead(sheet, firstRowIndex++, firstColumnIndex);
            setBody(sheet, data, firstRowIndex, firstColumnIndex);
            return this;
        }

        public Settings<T> setHead(Sheet sheet, int firstRowIndex) { return setHead(sheet, firstRowIndex, 0); }

        public Settings<T> setHead(Sheet sheet, int firstRowIndex, final int firstColumnIndex) {
            if (sheet != null) {
                int cellIndex = firstColumnIndex;
                Row row = sheet.createRow(firstRowIndex);
                for (ColumnDescription<T> set : settings) {
                    Cell cell = row.createCell(cellIndex++);
                    set.setTitle(cell);
                }
            }
            return this;
        }

        public Settings<T> setBody(Sheet sheet, T[] data, int firstRowIndex) {
            return setBody(sheet, data, firstRowIndex, 0);
        }

        public Settings<T> setBody(Sheet sheet, Collection<T> data, int firstRowIndex) {
            return setBody(sheet, data, firstRowIndex, 0);
        }

        public Settings<T> setBody(Sheet sheet, Collection<T> data, int firstRowIndex, final int firstColumnIndex) {
            if (sheet != null && data != null) {
                for (T item : data) {
                    firstRowIndex = getFirstRowIndex(sheet, firstRowIndex, firstColumnIndex, item);
                }
            }
            return this;
        }

        public Settings<T> setBody(Sheet sheet, T[] data, int firstRowIndex, final int firstColumnIndex) {
            if (sheet != null && data != null) {
                for (int i = 0; i < data.length; i++) {
                    firstRowIndex = getFirstRowIndex(sheet, firstRowIndex, firstColumnIndex, data[i]);
                }
            }
            return this;
        }

        public Operator<T> operate(Sheet sheet) { return new Operator<>(sheet, this); }

        private int getFirstRowIndex(Sheet sheet, int firstRowIndex, int firstColumnIndex, T item) {
            Row row = sheet.createRow(firstRowIndex++);
            for (ColumnDescription<T> set : settings) {
                Cell cell = row.createCell(firstColumnIndex++);
                set.setCellValue(cell, item);
            }
            return firstRowIndex;
        }
    }

    /*
     * --------------------------------------------------------------------------
     * operator
     * --------------------------------------------------------------------------
     */

    public final static class Operator<T> {
        private final Sheet sheet;
        private final Settings<T> settings;

        private Operator(Sheet sheet, Settings<T> settings) {
            this.settings = settings;
            this.sheet = sheet;
        }

        public Operator<T> config(Consumer<Settings<T>> consumer) {
            consumer.accept(settings);
            return this;
        }

        public Operator<T> preset(Consumer<Sheet> consumer) {
            consumer.accept(getSheet());
            return this;
        }

        public Operator<T> setColumnsWidth() { return setColumnsWidth(0); }

        public Operator<T> setColumnsWidth(int firstColumnIndex) {
            settings.setColumnsWidth(getSheet(), firstColumnIndex);
            return this;
        }

        public Operator<T> setData(T[] data, int firstRowIndex) { return setData(data, firstRowIndex, 0); }

        public Operator<T> setData(Collection<T> data, int firstRowIndex) { return setData(data, firstRowIndex, 0); }

        public Operator<T> setData(T[] data, int firstRowIndex, int firstColumnIndex) {
            settings.setData(getSheet(), data, firstRowIndex, firstColumnIndex);
            return this;
        }

        public Operator<T> setData(Collection<T> data, int firstRowIndex, int firstColumnIndex) {
            settings.setData(getSheet(), data, firstRowIndex, firstColumnIndex);
            return this;
        }

        public Operator<T> setHead(int firstRowIndex) {
            settings.setHead(getSheet(), firstRowIndex);
            return this;
        }

        public Operator<T> setBody(Collection<T> data, int firstRowIndex) {
            settings.setBody(getSheet(), data, firstRowIndex);
            return this;
        }

        public Operator<T> setBody(T[] data, int firstRowIndex) {
            settings.setBody(getSheet(), data, firstRowIndex);
            return this;
        }

        public Operator<T> setBody(Collection<T> data, int firstRowIndex, int firstColumnIndex) {
            settings.setBody(getSheet(), data, firstRowIndex, firstColumnIndex);
            return this;
        }

        public Operator<T> setBody(T[] data, int firstRowIndex, int firstColumnIndex) {
            settings.setBody(getSheet(), data, firstRowIndex, firstColumnIndex);
            return this;
        }

        public Sheet getSheet() { return sheet; }

        public Workbook getWorkbook() { return sheet.getWorkbook(); }

        public Operator<T> operate(Sheet sheet) { return new Operator<>(sheet, settings); }

        public Settings<T> getSettings() { return settings; }
    }
}
