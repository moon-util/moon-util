package com.moon.core.util;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * @author benshaoye
 */
class PropertiesParserTestTest {

    @Test
    void testParse() {
        ThrowUtil.ignoreThrowsRun(() -> {
            String path = "local-test/moon.properties";
            PropertiesParser parser = new PropertiesParser("moon.profiles");
            TreeMap<String, String> sortedProps = new TreeMap<>(Comparator.naturalOrder());
            PropertiesHashMap properties = parser.parse(path);

            sortedProps.putAll(properties);
            sortedProps.forEach((key, value) -> System.out.println(StringUtil.padEnd(key, 20, ' ') + "\t\t" + value));

            PropertiesGroup grouped = PropertiesGroup.of(properties);

            sortedProps.forEach((key, value) -> {
                String[] keys = key.split("\\.");
                Assertions.assertEquals(value, grouped.getString(keys));
            });
        }, true);
    }
}