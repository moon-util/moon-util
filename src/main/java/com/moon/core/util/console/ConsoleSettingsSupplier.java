package com.moon.core.util.console;

import java.util.function.Supplier;

/**
 * @author benshaoye
 */
@FunctionalInterface
public interface ConsoleSettingsSupplier extends Supplier<ConsoleSettings> {
    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    ConsoleSettings get();
}
