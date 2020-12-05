package com.moon.web;

import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * @author moonsky
 */
public interface ConsumerWriter extends Consumer<PrintWriter> {

    /**
     * write content
     *
     * @param writer writer
     */
    void write(PrintWriter writer);

    /**
     * write content
     *
     * @param writer writer
     */
    @Override
    default void accept(PrintWriter writer) { write(writer); }
}
