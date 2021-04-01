package com.moon.core.util.concurrent;

import java.util.concurrent.*;

/**
 * @author benshaoye
 */
public class ThreadPoolBuilder {

    /** 核心线程数 */
    private int corePoolSize = 1;
    /** 最大线程数 */
    private int maximumPoolSize = Integer.MAX_VALUE;
    /** 线程最大空闲时间 */
    private long keepAliveTime = 60;
    /** 时间单位 */
    private TimeUnit unit;
    /** 任务队列 */
    private BlockingQueue<Runnable> workQueue;
    /** 线程工程 */
    private ThreadFactory threadFactory;
    /** 拒绝策略 */
    private RejectedExecutionHandler handler;

    public ThreadPoolBuilder() { }

    public static ThreadPoolBuilder of() { return new ThreadPoolBuilder(); }

    public ThreadPoolBuilder withCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ThreadPoolBuilder withMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public ThreadPoolBuilder withKeepAliveTime(int keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ThreadPoolBuilder withTimeUnit(TimeUnit unit) {
        this.unit = unit;
        return this;
    }

    public ThreadPoolBuilder withWorkQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public ThreadPoolBuilder withThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ThreadPoolBuilder withRejectedHandler(RejectedExecutionHandler handler) {
        this.handler = handler;
        return this;
    }

    public int getCorePoolSize() { return corePoolSize; }

    public int getMaximumPoolSize() { return maximumPoolSize; }

    public long getKeepAliveTime() { return keepAliveTime; }

    public TimeUnit getUnit() { return unit == null ? (unit = TimeUnit.MILLISECONDS) : unit; }

    public BlockingQueue<Runnable> getWorkQueue() {
        return workQueue == null ? (workQueue = new LinkedBlockingQueue<>()) : workQueue;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory == null ? (threadFactory = Executors.defaultThreadFactory()) : threadFactory;
    }

    public RejectedExecutionHandler getHandler() {
        return handler == null ? (handler = RejectedUtil.callerRun()) : handler;
    }

    public ScheduledThreadPoolExecutor buildScheduled() {
        return new ScheduledThreadPoolExecutor(getCorePoolSize(), getThreadFactory(), getHandler());
    }

    public ThreadPoolExecutor build() {
        return new ThreadPoolExecutor(getCorePoolSize(),
            getMaximumPoolSize(),
            getKeepAliveTime(),
            getUnit(),
            getWorkQueue(),
            getThreadFactory(),
            getHandler());
    }
}
