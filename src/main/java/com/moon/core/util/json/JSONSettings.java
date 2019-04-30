package com.moon.core.util.json;

import java.util.Objects;

/**
 * @author benshaoye
 */
@Deprecated
public class JSONSettings {
    /**
     * 是否忽略 null 值
     */
    private boolean ignoreNullValue;
    /**
     * 是否格式化缩进
     * 0 代表不缩进，格式化成普通字符串
     * 大于 0 的数均按照层级缩进
     */
    private int indentSpaces;

    public JSONSettings() {
    }

    public JSONSettings(boolean ignoreNullValue, int indentSpaces) {
        this.ignoreNullValue = ignoreNullValue;
        this.indentSpaces = indentSpaces;
    }

    public boolean isIgnoreNullValue() {
        return ignoreNullValue;
    }

    public void setIgnoreNullValue(boolean ignoreNullValue) {
        this.ignoreNullValue = ignoreNullValue;
    }

    public int getIndentSpaces() {
        return indentSpaces;
    }

    public void setIndentSpaces(int indentSpaces) {
        this.indentSpaces = indentSpaces;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JSONSettings that = (JSONSettings) o;
        return ignoreNullValue == that.ignoreNullValue &&
            indentSpaces == that.indentSpaces;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ignoreNullValue, indentSpaces);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JSONSettings{");
        sb.append("ignoreNullValue=").append(ignoreNullValue);
        sb.append(", indentSpaces=").append(indentSpaces);
        sb.append('}');
        return sb.toString();
    }
}
