package com.moon.data.jpa.domain;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author moonsky
 */
class AbstractAuditableTestTest {

    public static class TestAuditable extends AbstractJpaAuditRecord<String, Date> {

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