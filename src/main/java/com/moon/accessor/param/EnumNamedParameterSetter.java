package com.moon.accessor.param;

/**
 * @author benshaoye
 */
public class EnumNamedParameterSetter extends StringParameterSetter {

    public EnumNamedParameterSetter(int parameterIndex, Enum<?> value) {
        super(parameterIndex, value.name());
    }

    public EnumNamedParameterSetter(int parameterIndex, int sqlType, Enum<?> value) {
        super(parameterIndex, sqlType, value.name());
    }
}
