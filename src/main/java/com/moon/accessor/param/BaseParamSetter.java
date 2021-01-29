package com.moon.accessor.param;

/**
 * @author benshaoye
 */
abstract class BaseParamSetter {

    private final int parameterIndex;

    public BaseParamSetter(int parameterIndex) {
        this.parameterIndex = parameterIndex;
    }

    public int getParameterIndex() { return parameterIndex; }
}
