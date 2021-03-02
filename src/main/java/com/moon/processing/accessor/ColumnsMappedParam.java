package com.moon.processing.accessor;

import com.moon.processing.decl.ColumnDeclared;
import com.moon.processing.decl.ParameterDeclared;

/**
 * @author benshaoye
 */
public class ColumnsMappedParam extends ColumnsMapped<ParameterDeclared> {

    @Override
    public void add(ColumnDeclared column, ParameterDeclared reffedDeclared) {
        withIfCondition(reffedDeclared);
    }
}
