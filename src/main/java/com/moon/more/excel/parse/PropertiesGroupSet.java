package com.moon.more.excel.parse;

import java.util.List;

/**
 * @author benshaoye
 */
class PropertiesGroupSet extends PropertiesGroup<PropertySet> {

    public PropertiesGroupSet(
        List<PropertySet> setters, DetailRoot root, PropertySet rootProperty
    ) { super(setters, root, rootProperty); }
}