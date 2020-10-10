package com.moon.data.accessor;

import com.moon.core.util.RunnerRegistration;

import static com.moon.core.lang.ThrowUtil.noInstanceError;

/**
 * @author moonsky
 */
public final class AccessorRegistration {

    private final static RunnerRegistration REGISTRATION = RunnerRegistration.newInstance();

    private AccessorRegistration() { noInstanceError(); }

    static void registry(Runnable runner) { REGISTRATION.registry(runner); }

    public static void runningTakeAll() {
        REGISTRATION.runningTakeAll();
    }
}
