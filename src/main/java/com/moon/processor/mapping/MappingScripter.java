package com.moon.processor.mapping;

import com.moon.processor.holder.Importable;

import java.util.List;

/**
 * @author benshaoye
 */
public interface MappingScripter<I extends Importable> {

    List<String> getScriptsOnDefaultVal(I importable, MappingDefaultVal var);

    List<String> getScripts(I importable);

    String getGetterType();

    String getSetterType();
}
