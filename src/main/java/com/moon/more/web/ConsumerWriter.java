package com.moon.more.web;

import java.io.PrintWriter;
import java.util.function.Consumer;

/**
 * @author moonsky
 */
public interface ConsumerWriter extends Consumer<PrintWriter> {

    /**
     * write content
     *
     * @param writer
     */
    void write(PrintWriter writer);

    /**
     * write content
     *
     * @param writer
     */
    @Override
    void accept(PrintWriter writer);
}
