package com.moon.data.jdbc;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author benshaoye
 */
public class SQLExecuteException extends RuntimeException {

    private static String formatMessage(String sql, Object... args) {
        String as = args == null ? "[]" : Arrays.deepToString(args);
        StringBuilder builder = new StringBuilder();
        builder.append(">>>>>> Execute: >>>>>>");
        builder.append("\n>> SQL: ").append(sql);
        builder.append("\n>> Args: ").append(as);
        builder.append("\n<<<<<< End <<<<<<");
        return builder.toString();
    }

    public SQLExecuteException(SQLException e, String sql, Object... args) { this(e, formatMessage(sql, args)); }

    public SQLExecuteException(SQLException e, String message) { super(message, e); }

    public SQLExecuteException(SQLException e) { super(e); }
}
