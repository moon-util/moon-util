package com.moon.accessor.param;

/**
 * @author benshaoye
 */
public abstract class BaseParameterSetter implements ParameterSetter {

    private final int parameterIndex;

    public BaseParameterSetter(int parameterIndex) { this.parameterIndex = parameterIndex; }

    public int getParameterIndex() { return parameterIndex; }
}
