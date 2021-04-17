package com.moon.processing.accessor;

import com.moon.processing.decl.ColumnDeclared;
import com.moon.processing.decl.PropertyDeclared;

/**
 * @author benshaoye
 */
public class ColumnsMappedProp extends ColumnsMapped<PropertyDeclared> {

    @Override
    public void add(ColumnDeclared column, PropertyDeclared reffedDeclared) {
        withIfCondition(reffedDeclared);
    }
}
