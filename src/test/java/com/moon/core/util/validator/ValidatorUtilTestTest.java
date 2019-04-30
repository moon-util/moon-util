package com.moon.core.util.validator;

import com.moon.core.util.PropertiesHashMap;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class ValidatorUtilTestTest {

    @Test
    void testOf() {
    }

    @Test
    void testOfCollect() {
    }

    @Test
    void testOfMap() {
        PropertiesHashMap map = new PropertiesHashMap();
        map.put("111", "222");
        map.put("222", "333");
        map.put("333", "444");
        ValidatorUtil.ofMap(map);
    }

    @Test
    void testOfIDCard18() {
    }
}