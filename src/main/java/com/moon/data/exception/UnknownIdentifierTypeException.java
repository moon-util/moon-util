package com.moon.data.exception;

/**
 * @author moonsky
 */
public class UnknownIdentifierTypeException extends RuntimeException {

    public UnknownIdentifierTypeException(Class type) {
        this(type.toString());
    }
    public UnknownIdentifierTypeException(String message) {
        super(message);
    }
}
