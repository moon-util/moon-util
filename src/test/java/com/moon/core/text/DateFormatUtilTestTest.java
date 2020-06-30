package com.moon.core.text;

import com.moon.core.enums.DateFormats;
import com.moon.more.model.KeyValue;
import com.moon.core.util.ListUtil;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author moonsky
 */
class DateFormatUtilTestTest {

    @Test
    void testWith() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        Date date = new Date();
        List<String> messages = ListUtil.newArrayList(DateFormats.values()).stream()
            .map(item -> item.with(pattern).format(date))
            .sorted().collect(Collectors.toList());
        messages.forEach(System.out::println);

        ListUtil.newArrayList(DateFormats.values()).stream().map(KeyValue::of)
            .collect(Collectors.toList()).stream()
            .sorted(Comparator.comparingInt(o -> o.toString().length()))
            .forEach(System.out::println);

        Class<DateFormats> type = DateFormats.class;
        ListUtil.newArrayList(type.getDeclaredClasses()).forEach(System.out::println);
    }
}