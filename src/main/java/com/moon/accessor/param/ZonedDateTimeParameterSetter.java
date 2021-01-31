package com.moon.accessor.param;

import java.time.ZonedDateTime;

/**
 * @author benshaoye
 */
public class ZonedDateTimeParameterSetter extends ObjectParameterSetter implements ParameterSetter {

    public ZonedDateTimeParameterSetter(int parameterIndex, ZonedDateTime value) {
        super(parameterIndex, value);
    }
}
