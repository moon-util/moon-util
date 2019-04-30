package com.moon.core.util.require;

import com.moon.core.util.console.GenericConsolePrinter;

/**
 * @author benshaoye
 */
class RequiresConsolePrinter
    extends GenericConsolePrinter {

    public RequiresConsolePrinter() {
        super(RequiresFail.class);
    }
}
