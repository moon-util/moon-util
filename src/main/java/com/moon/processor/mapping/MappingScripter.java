package com.moon.processor.mapping;

import com.moon.processor.manager.Importable;

/**
 * @author benshaoye
 */
public interface MappingScripter<I extends Importable> {

    String[] getScriptsOnDefaultVal(I importable, MappingDefaultVal var);

    String[] getScripts(I importable);

    String getGetterType();

    String getSetterType();
}
