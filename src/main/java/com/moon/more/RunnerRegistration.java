package com.moon.more;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import static com.moon.core.util.ListUtil.newArrayList;

/**
 * @author benshaoye
 */
public final class RunnerRegistration {

    private final List<Runnable> RUNNERS;

    public RunnerRegistration() { this(CopyOnWriteArrayList::new); }

    public RunnerRegistration(Supplier<List<Runnable>> creator) {
        this.RUNNERS = creator.get();
    }

    /**
     * 注册一个 runner
     *
     * @param runner
     */
    public void registry(Runnable runner) { RUNNERS.add(runner); }

    /**
     * 执行所有任务,并删除
     */
    public void runningTakeAll() { takeAll(true).forEach(Runnable::run); }

    /**
     * 执行所有任务
     */
    public void runningAll() { takeAll(false).forEach(Runnable::run); }

    /**
     * 取出所有任务
     *
     * @param clear
     *
     * @return
     */
    public synchronized List<Runnable> takeAll(boolean clear) {
        List<Runnable> runners = newArrayList(RUNNERS);
        if (clear) {
            RUNNERS.clear();
        }
        return runners;
    }

    /**
     * 默认队列
     */
    private final static RunnerRegistration REGISTRATION = newInstance();

    public static RunnerRegistration getInstance() { return REGISTRATION; }

    public static RunnerRegistration newInstance() { return new RunnerRegistration(); }
}
