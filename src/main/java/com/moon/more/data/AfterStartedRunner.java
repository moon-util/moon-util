package com.moon.more.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.moon.core.util.ListUtil.newArrayList;

/**
 * @author benshaoye
 */
public final class AfterStartedRunner {

    private final static List<Runnable> REGISTERED_RUNNERS

        = new CopyOnWriteArrayList<>();

    public static void registry(Runnable runner) { REGISTERED_RUNNERS.add(runner); }

    public static void runningTakeAll() { takeAll().forEach(Runnable::run); }

    private static synchronized List<Runnable> takeAll() {
        List<Runnable> runners = newArrayList(REGISTERED_RUNNERS);
        REGISTERED_RUNNERS.clear();
        return runners;
    }
}
