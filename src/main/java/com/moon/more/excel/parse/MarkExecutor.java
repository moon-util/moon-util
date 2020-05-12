package com.moon.more.excel.parse;

import com.moon.more.excel.RowFactory;

import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
class MarkExecutor {

    final LinkedList<Consumer<RowFactory>> runnerList = new LinkedList<>();

    private boolean running = false;

    public final void add(Consumer<RowFactory> runner) {
        runnerList.addFirst(runner);
    }

    public final void run(RowFactory rowFactory) {
        if (!running) {
            running = true;
            runnerList.forEach(runner -> {
                runner.accept(rowFactory);
            });
            running = false;
        }
    }

    public final Consumer<RowFactory> removeFirst() {
        return runnerList.removeFirst();
    }
}
