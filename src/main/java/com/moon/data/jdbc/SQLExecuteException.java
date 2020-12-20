package com.moon.data.jdbc;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author benshaoye
 */
public class SQLExecuteException extends RuntimeException {

    private static String formatMessage(String sql, Object... args) {
        @SuppressWarnings("all")
        StringBuilder builder = new StringBuilder(">>>>>> execute >>>>>>");
        builder.append("\n>> SQL: ").append(sql);
        String as = args == null ? "[]" : Arrays.deepToString(args);
        builder.append("\n>> Args: ").append(as);
        return builder.append("\n<<<<<< End <<<<<<").toString();
    }

    public SQLExecuteException(SQLException e, String sql, Object... args) {
        this(e, formatMessage(sql, args));
    }

    public SQLExecuteException(SQLException e, String message) { super(message, e); }

    public SQLExecuteException(SQLException e) { super(e); }
}
