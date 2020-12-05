package com.moon.web;

import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * @author moonsky
 */
public interface ConsumerStream extends Consumer<OutputStream> {

    /**
     * write data
     *
     * @param writer writer
     */
    void write(OutputStream writer);

    /**
     * write data
     *
     * @param writer writer
     */
    @Override
    default void accept(OutputStream writer) { write(writer); }
}
