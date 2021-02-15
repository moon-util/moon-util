package com.moon.processing.holder;

import com.moon.processing.JavaDeclarable;
import com.moon.processing.JavaFiler;
import com.moon.processing.JavaWritable;

import java.util.Collection;

/**
 * @author benshaoye
 */
abstract class BaseHolder implements JavaWritable {

    public BaseHolder() {}

    /**
     * 文件描述
     *
     * @return
     */
    protected abstract Collection<? extends JavaDeclarable> getJavaDeclares();

    @Override
    public final void write(JavaFiler writer) {
        getJavaDeclares().forEach(writer::write);
    }
}
