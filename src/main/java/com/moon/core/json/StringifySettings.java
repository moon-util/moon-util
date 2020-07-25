package com.moon.core.json;

import com.moon.core.lang.ref.IntAccessor;

/**
 * @author benshaoye
 */
public class StringifySettings {

    final static StringifySettings EMPTY = new StringifySettings();

    private IntAccessor indentAccessor;

    private final int indent;

    private StringifySettings() { this(0); }

    private StringifySettings(int indent) { this.indent = indent; }

    StringifySettings start() {
        indentAccessor = indent > 0 ? IntAccessor.of(-1) : null;
        return this;
    }

    void open(StringBuilder builder, char open) {
        if (indent > 0) {

        }
    }

    void close(StringBuilder builder, char close) {
        if (indent > 0) {

        }
    }

    void close() { indentAccessor = null; }
}
