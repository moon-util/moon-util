package com.moon.more.web;

import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * @author moonsky
 */
public interface ConsumerStream extends Consumer<OutputStream> {

    /**
     * write data
     *
     * @param writer
     */
    void write(OutputStream writer);

    /**
     * write data
     *
     * @param writer
     */
    @Override
    default void accept(OutputStream writer) { write(writer); }
}
