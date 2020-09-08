package com.moon.data.jpa.sql.parse;

/**
 * @author moonsky
 */
public class SqlSelectStatementBody {

    private final String source;

    public SqlSelectStatementBody(String source) {
        this.source = source;
    }

    public String[] getColumnsLabel() {
        return null;
    }

    public boolean hasNamedParameter() {
        return false;
    }

    public boolean hasIndexedParameter() {
        return false;
    }
}
