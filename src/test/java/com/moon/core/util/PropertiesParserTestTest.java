package com.moon.core.util;

import com.moon.core.lang.StringUtil;
import com.moon.core.lang.ThrowUtil;
import com.moon.core.lang.ref.FinalAccessor;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author benshaoye
 */
class PropertiesParserTestTest {

    @Test
    void testParse() {
        FinalAccessor<TreeMap<String, String>> propsAccessor = FinalAccessor.of();
        FinalAccessor<PropertiesGroup> groupAccessor = FinalAccessor.of();
        ThrowUtil.ignoreThrowsRun(() -> {
            String path = "local-test/moon.properties";
            PropertiesParser parser = new PropertiesParser("moon.profiles");
            TreeMap<String, String> sortedProps = new TreeMap<>(Comparator.naturalOrder());
            PropertiesHashMap properties = parser.parse(path);

            sortedProps.putAll(properties);
            sortedProps.forEach((key, value) -> {
                String nowVal = StringUtil.defaultIfNull(value, "<----------------->");
                System.out.println(StringUtil.padEnd(key, 20, ' ') + "\t\t" + nowVal);
            });

            propsAccessor.set(sortedProps);
            groupAccessor.set(PropertiesGroup.of(properties));
        }, true);

        propsAccessor.ifPresent(tree -> {
            PropertiesGroup grouped = groupAccessor.get();
            tree.forEach((key, value) -> {
                assertEquals(value, grouped.get(key));
                assertEquals(value, grouped.getString(key.split("\\.")));
            });
        });
    }
}