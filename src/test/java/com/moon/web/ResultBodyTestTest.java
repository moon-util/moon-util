package com.moon.web;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;

/**
 * @author moonsky
 */
class ResultBodyTestTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void testGetStatus() throws Exception {
        ResultBody body = ResultBody.ok().code(500).message("错了").data("value");
        System.out.println(JSON.toJSONString(body));
        try (Writer writer = new StringWriter()){
            mapper.writeValue(writer, body);
            System.out.println("===>> "+writer.toString());
        }
    }
}