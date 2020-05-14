package com.moon.more.excel.parse;

import com.moon.more.excel.RowFactory;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
class MarkTask {

    public final static MarkTask NONE = new Nothing();

    private final MarkTask parent;
    private final Consumer<RowFactory> runner;

    public MarkTask(Consumer<RowFactory> runner, MarkTask parent) {
        this(runner, parent, false);
    }

    private MarkTask(Consumer<RowFactory> runner, MarkTask parent, boolean initialize) {
        this.parent = initialize ? parent : Objects.requireNonNull(parent);
        this.runner = runner;
    }


    public void run(RowFactory rowFactory) {
        parent.run(rowFactory);
        runner.accept(rowFactory);
    }


    private static class Nothing extends MarkTask {

        private Nothing() { super(null, null, true); }

        @Override
        public void run(RowFactory rowFactory) { }
    }
}
