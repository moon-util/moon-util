package com.moon.accessor.exception;

import com.moon.accessor.util.String2;

/**
 * @author benshaoye
 */
public enum Exception2 {
    ;

    public static SqlException with(String message) { return new SqlException(message); }

    public static SqlException with(String message, Object... values) { return with(String2.format(message, values)); }

    public static SqlException with(Throwable t) { return new SqlException(t); }

    public static SqlException with(Throwable t, String message) { return new SqlException(message, t); }

    public static SqlException with(Throwable t, String message, Object... values) {
        return with(t, String2.format(message, values));
    }

    public static SqlException tx(String message) { return new TransactionException(message); }
}
