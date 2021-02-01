package com.moon.accessor.param;

/**
 * @author benshaoye
 */
public class EnumOrdinalParameterSetter extends IntParameterSetter {

    public EnumOrdinalParameterSetter(int parameterIndex, Enum value) {
        super(parameterIndex, value.ordinal());
    }

    public EnumOrdinalParameterSetter(int parameterIndex, int sqlType, Enum value) {
        super(parameterIndex, sqlType, value.ordinal());
    }
}
