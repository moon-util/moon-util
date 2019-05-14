package com.moon.core.enums;

import com.moon.core.lang.EnumUtil;
import org.junit.jupiter.api.Test;

/**
 * @author benshaoye
 */
class MoonPropsTestTest {

    @Test
    void testGetName() {
        EnumUtil.valuesList(MoonProps.class).forEach(prop -> {
            System.out.println(prop.toString() + '\t' + prop.getName() + '\t'
                + prop.key() + '\t' + prop.value() + '\t' + (prop.value() == null));
        });
    }

    @Test
    void testGetText() {
    }
}