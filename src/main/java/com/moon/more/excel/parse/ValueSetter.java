package com.moon.more.excel.parse;

import com.moon.more.excel.PropertySetter;

/**
 * @author benshaoye
 */
abstract class ValueSetter implements PropertySetter {
    private final PropertySetter setter;

    protected ValueSetter(PropertySetter setter) {
        this.setter = setter;
    }
}
