package com.moon.poi.excel.table;

import java.util.Objects;

/**
 * @author moonsky
 */
final class HeadCell {

    final static HeadCell EMPTY = new HeadCell();

    private final String title;

    private final short height;

    private final boolean offsetFilled;

    private HeadCell() { this(null, (short) -1); }

    HeadCell(String title, short height) { this(title, height, false); }

    HeadCell(String title, short height, boolean offsetFilled) {
        this.offsetFilled = offsetFilled;
        this.height = height;
        this.title = title;
    }

    static String getTitleOfNull(HeadCell cell) { return cell == null ? null : cell.title; }

    public short getHeight() { return height; }

    public boolean isOffsetFilled() { return offsetFilled; }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        HeadCell headCell = (HeadCell) o;
        return Objects.equals(title, headCell.title);
    }

    @Override
    public int hashCode() { return Objects.hash(title); }
}
