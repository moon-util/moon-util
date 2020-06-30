package com.moon.spring.jpa.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class AbstractAuditableTestTest {

    public static class TestAuditable extends AbstractAuditable<String, Date> {

        @Override
        public String getCreatedBy() {
            return super.getCreatedBy();
        }

        @Override
        public void setCreatedBy(String createdBy) {
            super.setCreatedBy(createdBy);
        }

        public String getCreatedUserId() {
            return super.getCreatedBy();
        }

        // public void setCreatedUserId(String id) {
        // super.setCreatedBy(id);
        // }
    }

    @Test
    void testJsonSerialize() throws Exception {
        TestAuditable audit = new TestAuditable();
        audit.setCreatedBy("123");
        String json = JSON.toJSONString(audit);
        System.out.println(json);
        TestAuditable auditable = JSON.parseObject(json, TestAuditable.class);
        System.out.println();

    }

    @Test
    void testJackson() throws Exception {
        TestAuditable audit = new TestAuditable();
        audit.setCreatedBy("123");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(System.out, audit);
    }
}