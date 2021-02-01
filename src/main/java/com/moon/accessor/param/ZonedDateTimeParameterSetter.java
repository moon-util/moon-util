package com.moon.accessor.param;

import java.time.ZonedDateTime;

/**
 * @author benshaoye
 */
public class ZonedDateTimeParameterSetter extends ObjectParameterSetter implements ParameterSetter {

    public ZonedDateTimeParameterSetter(int parameterIndex, ZonedDateTime value) {
        super(parameterIndex, value);
    }

    public ZonedDateTimeParameterSetter(int parameterIndex, int sqlType, ZonedDateTime value) {
        super(parameterIndex, sqlType, value);
    }
}
