package com.moon.processing.accessor;

import com.moon.processing.decl.AnnotatedDeclared;
import com.moon.processing.decl.ColumnDeclared;

/**
 * @author benshaoye
 */
public abstract class ColumnsMapped<A extends AnnotatedDeclared> {

    private boolean hasIfCondition;

    public ColumnsMapped() { this.hasIfCondition = false; }

    protected final A withIfCondition(A declared) {
        this.hasIfCondition = hasIfCondition || declared.hasIfCondition();
        return declared;
    }

    public final boolean hasIfCondition() { return hasIfCondition; }

    /**
     * 添加参数
     *
     * @param column
     * @param reffedDeclared
     */
    public abstract void add(ColumnDeclared column, A reffedDeclared);
}
