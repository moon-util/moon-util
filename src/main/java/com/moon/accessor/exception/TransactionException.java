package com.moon.accessor.exception;

/**
 * @author benshaoye
 */
public class TransactionException extends SqlException {

    public TransactionException() { super(); }

    public TransactionException(String message) { super(message); }

    public TransactionException(String message, Throwable cause) { super(message, cause); }

    public TransactionException(Throwable cause) { super(cause); }

    public TransactionException(
        String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
    ) { super(message, cause, enableSuppression, writableStackTrace); }
}
