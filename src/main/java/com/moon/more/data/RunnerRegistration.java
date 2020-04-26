package com.moon.more.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.moon.core.util.ListUtil.newArrayList;

/**
 * @author benshaoye
 */
public final class RunnerRegistration {

    private final List<Runnable> REGISTERED_RUNNERS

        = new CopyOnWriteArrayList<>();

    public void registry(Runnable runner) { REGISTERED_RUNNERS.add(runner); }

    public void runningTakeAll() { takeAll().forEach(Runnable::run); }

    private synchronized List<Runnable> takeAll() {
        List<Runnable> runners = newArrayList(REGISTERED_RUNNERS);
        REGISTERED_RUNNERS.clear();
        return runners;
    }

    private final static RunnerRegistration REGISTRATION = new RunnerRegistration();

    public static RunnerRegistration getInstance() { return REGISTRATION; }
}
