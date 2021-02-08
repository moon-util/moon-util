package com.moon.processor.create;

import java.util.List;

/**
 * @author benshaoye
 */
interface ScriptsSupplier {

    List<String> getScripts(final int indentCount);
}
