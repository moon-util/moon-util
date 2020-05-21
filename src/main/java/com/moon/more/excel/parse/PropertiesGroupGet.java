package com.moon.more.excel.parse;

import java.util.List;

/**
 * @author benshaoye
 */
class PropertiesGroupGet extends PropertiesGroup<PropertyGet> {

    public PropertiesGroupGet(
        List<PropertyGet> getters, RowRecord root, PropertyGet rootProperty
    ) { super(getters, root, rootProperty); }
}
