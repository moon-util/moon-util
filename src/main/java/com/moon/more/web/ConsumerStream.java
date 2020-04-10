package com.moon.more.web;

import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * @author benshaoye
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
    void accept(OutputStream writer);
}
