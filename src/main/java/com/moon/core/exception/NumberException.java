package com.moon.core.exception;

import static java.lang.String.format;

/**
 * @author moonsky
 */
public class NumberException extends RuntimeException {

    public NumberException() { }

    public NumberException(String message) { super(message); }

    public NumberException(String message, Exception e) { super(message, e); }

    /*
     * int
     */

    public NumberException(int value) { this(format("value: %d", value)); }

    public NumberException(int value, String message) {
        this(format("value: %d; message: %s", value, message));
    }

    public NumberException(int value, String message, Exception e) {
        this(format("value: %d; message: %s", value, message), e);
    }

    public NumberException(int value, Exception e) { this(format("value: %d", value), e); }

    /*
     * long
     */

    public NumberException(long value) { this(format("value: %d", value)); }

    public NumberException(long value, String message) {
        this(format("value: %d; message: %s", value, message));
    }

    public NumberException(long value, String message, Exception e) {
        this(format("value: %d; message: %s", value, message), e);
    }

    public NumberException(long value, Exception e) { this(format("value: %d", value), e); }

    /*
     * double
     */

    public NumberException(double value) { this(format("value: %d", value)); }

    public NumberException(double value, String message) {
        this(format("value: %d; message: %s", value, message));
    }

    public NumberException(double value, String message, Exception e) {
        this(format("value: %d; message: %s", value, message), e);
    }

    public NumberException(double value, Exception e) { this(format("value: %d", value), e); }
}
