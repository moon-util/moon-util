package com.moon.more.excel.parse;

import java.util.List;
import java.util.function.IntFunction;

/**
 * @author benshaoye
 */
class PropertiesGroupGet extends PropertiesGroup<PropertyGet> {

    public PropertiesGroupGet(
        List<PropertyGet> getters, DetailRoot root, PropertyGet starting, PropertyGet ending
    ) { super(getters, root, starting, ending); }

    @Override
    protected IntFunction<PropertyGet[]> getArrCreator() { return PropertyGet[]::new; }
}
