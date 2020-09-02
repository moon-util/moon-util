package com.moon.data;

import com.moon.more.RunnerRegistration;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class RecordUtil {

    private final static RunnerRegistration REGISTRATION = RunnerRegistration.newInstance();

    private RecordUtil() { noInstanceError(); }

    static void registry(Runnable runner) { REGISTRATION.registry(runner); }

    public static void runningTakeAll() {
        REGISTRATION.runningTakeAll();
    }
}
