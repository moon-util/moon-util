package com.moon.more.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

/**
 * @author benshaoye
 */
class StyleSetter {

    private final Object object;

    public StyleSetter(Object object) {
        this.object = object;
    }

    void useStyle(CellStyle style) {
        Object object = this.object;
        if (object instanceof Cell) {
            ((Cell) object).setCellStyle(style);
        } else if (object instanceof Row) {
            ((Row) object).setRowStyle(style);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        StyleSetter setter = (StyleSetter) o;
        return Objects.equals(object, setter.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object);
    }
}
