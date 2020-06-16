package com.moon.more.excel.table;

import java.util.Objects;

/**
 * @author benshaoye
 */
final class HeadCell {

    private final String title;

    private final short height;

    public HeadCell() { this(null, (short) -1); }

    HeadCell(String title, short height) {
        this.title = title;
        this.height = height;
    }

    public String getTitle() { return title; }

    public short getHeight() { return height; }

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
