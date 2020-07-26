package com.moon.more.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moon.core.lang.ThrowUtil;

/**
 * @author benshaoye
 */
public final class JacksonUtil {

    private JacksonUtil() { ThrowUtil.noInstanceError(); }

    private final static ObjectMapper MAPPER = new ObjectMapper();

    public static String stringify(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return ThrowUtil.unchecked(e);
        }
    }
}
