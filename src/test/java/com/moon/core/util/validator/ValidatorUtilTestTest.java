package com.moon.core.util.validator;

import com.moon.core.enums.Testers;
import com.moon.core.util.DetectUtil;
import com.moon.core.util.PropertiesHashMap;
import com.moon.core.util.RandomStringUtil;
import com.moon.core.util.RandomUtil;
import org.junit.jupiter.api.Assertions;
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
        Assertions.assertThrows(Exception.class,
            () -> ValidatorUtil.ofMap(map).requireAtLeastOf(4, (key, value) -> DetectUtil.isInteger(key)).get());
        Assertions.assertThrows(Exception.class,
            () -> ValidatorUtil.ofMap(map).requireAtMostOf(2, (key, value) -> DetectUtil.isInteger(key)).get());
        ValidatorUtil.ofMap(map)

            .requireAtLeastOf(3, (key, value) -> DetectUtil.isInteger(key))

            .get();
    }

    @Test
    @SuppressWarnings("unchecked")
    void testOfIDCard18() {
        Validator.ofNullable(null).require(Testers.isNotNull).get();
    }
}