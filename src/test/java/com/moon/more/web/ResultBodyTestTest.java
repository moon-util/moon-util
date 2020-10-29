package com.moon.more.web;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class ResultBodyTestTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void testGetStatus() throws Exception {
        ResultBody body = ResultBody.ok().code(500).message("错了").data("value");
        System.out.println(JSON.toJSONString(body));
        mapper.writeValue(System.out, body);
    }
}