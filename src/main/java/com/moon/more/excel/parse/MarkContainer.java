package com.moon.more.excel.parse;

import com.moon.more.excel.RowFactory;

/**
 * @author benshaoye
 */
class MarkContainer {

    final static MarkContainer DEFAULT = new None();

    public MarkContainer() { }

    void execute(RowFactory factory) {}

    private static class None extends MarkContainer {

        @Override
        void execute(RowFactory factory) { }
    }
}
