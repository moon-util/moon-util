package com.moon.core.util.condition;

import java.util.function.BooleanSupplier;

/**
 * @author benshaoye
 */
public final class DynamicCondition implements Conditional {

    private final BooleanSupplier dynamicCondition;

    public DynamicCondition(BooleanSupplier dynamicCondition) {
        this.dynamicCondition = dynamicCondition;
    }

    public static DynamicCondition of(BooleanSupplier dynamicCondition) { return new DynamicCondition(dynamicCondition); }

    /**
     * 返回是否符合条件
     *
     * @return true: 符合条件
     */
    @Override
    public boolean isMatched() {
        return dynamicCondition.getAsBoolean();
    }
}
