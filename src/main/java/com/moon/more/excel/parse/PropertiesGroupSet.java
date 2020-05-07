package com.moon.more.excel.parse;

import java.util.List;

/**
 * @author benshaoye
 */
public class PropertiesGroupSet extends PropertiesGroup<PropertySet> {

    public PropertiesGroupSet(
        List<PropertySet> setters, DetailRoot root, PropertySet starting, PropertySet ending
    ) { super(setters, root, starting, ending); }
}
