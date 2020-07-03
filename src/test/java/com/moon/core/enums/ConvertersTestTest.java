package com.moon.core.enums;

import com.moon.core.enums.Converters.MatchConverter;
import com.moon.core.util.ListUtil;
import com.moon.core.util.SetUtil;
import com.moon.more.excel.annotation.style.DefinitionStyle;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author moonsky
 */
class ConvertersTestTest {

    private enum MatchComparator implements Comparator<Class> {
        VALUE,
        ;

        @Override
        public int compare(Class o1, Class o2) {
            boolean forward = o1.isAssignableFrom(o2);
            boolean backward = o2.isAssignableFrom(o1);
            return forward ? (backward ? 0 : 1) : -1;
        }
    }

    @Test
    void testValueOf() throws Exception {
        Set<MatchConverter> values = new TreeSet<>();
        MatchConverter[] classes = {
            new MatchConverter(Collection.class, null),
            new MatchConverter(LinkedList.class, null),
            new MatchConverter(List.class, null),
            new MatchConverter(CharSequence.class, null),
            new MatchConverter(Iterable.class, null)
        };
        SetUtil.addAll(values, classes);
        values.forEach(value -> {
            System.out.println(value.supportedType);
        });

        System.out.println("========================================================");
        LinkedList<MatchConverter> converters =ListUtil.newLinkedList(classes);
        converters.sort(MatchConverter::compareTo);
        converters.forEach(value -> {
            System.out.println(value.supportedType);
        });
        System.out.println(converters.getLast().supportedType);
    }
}