package com.moon.spring.data.jpa.repository;

import com.moon.core.lang.ClassUtil;
import org.junit.jupiter.api.Test;
import org.springframework.data.repository.query.QueryLookupStrategy;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class SqlQueryLookupStrategyCreatorTestTest {

    @Test
    void testCreate() throws Exception {
        Class type = QueryLookupStrategy.Key.class;
        System.out.println(type);
        Class loadedType = ClassUtil.forNameOrNull(type.getName());
        assertEquals(loadedType, type);
    }
}