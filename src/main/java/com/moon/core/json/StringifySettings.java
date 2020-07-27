package com.moon.core.json;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ref.IntAccessor;

/**
 * @author benshaoye
 */
public class StringifySettings {

    final static StringifySettings EMPTY = new StringifySettings();

    final static StringifySettings INDENT2 = new StringifySettings(2);

    final static StringifySettings INDENT4 = new StringifySettings(4);

    private IntAccessor indentAccessor;

    private String indentWhitespaces;

    private final int indent;

    private final boolean spaceAfterComma = true;

    private StringifySettings() { this(0); }

    StringifySettings(int indent) { this.indent = indent; }

    public static StringifySettings of(int indent) {
        switch (indent) {
            case 0:
                return EMPTY;
            case 2:
                return INDENT2;
            case 4:
                return INDENT4;
            default:
                return new StringifySettings(indent);
        }
    }

    /*
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * operation supporter                                         *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     */

    final StringifySettings initialize() {
        indentAccessor = indent > 0 ? IntAccessor.of(0) : null;
        return this;
    }

    final void open() {
        if (indent > 0) {
            int count = indentAccessor.incrementAndGet();
            indentWhitespaces = StringUtil.repeat(" ", indent * count);
        }
    }

    final void close() {
        if (indent > 0) {
            int count = indentAccessor.decrementAndGet();
            indentWhitespaces = StringUtil.repeat(" ", indent * count);
        }
    }

    final String getIndentWhitespaces() {
        return indentWhitespaces;
    }

    final boolean isSpaceAfterComma() {
        return spaceAfterComma;
    }

    final void destroy() {
        indentAccessor = null;
        indentWhitespaces = null;
    }
}
