package com.moon.spring.jpa.id;

import com.moon.spring.jpa.HibernateBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author moonsky
 */
public class IdentifierInitialier extends HibernateBaseTest {

    @BeforeEach
    void setUp() {
        initializeFactory(PushedHookDetail.class);
    }

    public IdentifierInitialier() {
    }

    @Test
    void testName() throws Exception {

    }
}
