package com.moon.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 计时器
 *
 * @author moonsky
 */
public final class StopWatch {

    private final List<WatchTask> slices = new ArrayList<>();
    private WatchTask currentWatchTask;

    public StopWatch() { }

    public void start(String name) {
        if (currentWatchTask != null) {
            throw new IllegalStateException(String.format("当前计时任务'%s'正在运行...", currentWatchTask.name));
        }
        this.currentWatchTask = new WatchTask(name);
    }

    public void startWith(String name, long startTimeMillis) {
        if (currentWatchTask != null) {
            throw new IllegalStateException(String.format("当前计时任务'%s'正在运行...", currentWatchTask.name));
        }
        this.currentWatchTask = new WatchTask(name, startTimeMillis);
    }

    public void stop() {
        WatchTask task = this.currentWatchTask;
        if (task == null) {
            throw new IllegalStateException("当前没有正在运行的计时任务");
        }
        task.ending();
        this.slices.add(task);
        this.currentWatchTask = null;
    }

    public void stopAt(long endingTimeMillis) {
        WatchTask task = this.currentWatchTask;
        if (task == null) {
            throw new IllegalStateException("当前没有正在运行的计时任务");
        }
        task.ending(endingTimeMillis);
        this.slices.add(task);
        this.currentWatchTask = null;
    }

    /**
     * 是否正在运行
     *
     * @return 是否正在运行
     */
    public boolean isRunning() {
        return this.currentWatchTask != null;
    }

    /**
     * 已完成任务总数
     *
     * @return 已完成任务总数
     */
    public int getCompletedCount() { return slices.size(); }

    private static final class WatchTask implements Supplier<String> {

        private final String name;

        private final long start;

        private long end;

        private WatchTask(String name) {
            this(name, System.currentTimeMillis());
        }

        private WatchTask(String name, long startTimeMillis) {
            this.name = name;
            this.start = startTimeMillis;
        }

        private void ending() {
            this.end = System.currentTimeMillis();
        }

        private void ending(long endingTimeMillis) {
            this.end = endingTimeMillis;
        }


        public String getDuration() {
            return String.format("%s: %d ms;", name, end - start);
        }


        @Override
        public String get() {
            return getDuration();
        }

        @Override
        public String toString() {
            return get();
        }
    }
}
