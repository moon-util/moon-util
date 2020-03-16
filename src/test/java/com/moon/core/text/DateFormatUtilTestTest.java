package com.moon.core.text;

import com.moon.core.model.KeyValue;
import com.moon.core.util.ListUtil;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
class DateFormatUtilTestTest {

    @Test
    void testWith() {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        Date date = new Date();
        List<String> messages = ListUtil.newArrayList(DateFormatUtil.values()).stream()
            .map(item -> item.with(pattern).format(date))
            .sorted().collect(Collectors.toList());
        messages.forEach(System.out::println);

        ListUtil.newArrayList(DateFormatUtil.values()).stream().map(KeyValue::of)
            .collect(Collectors.toList()).stream()
            .sorted(Comparator.comparingInt(o -> o.toString().length()))
            .forEach(System.out::println);

        Class<DateFormatUtil> type = DateFormatUtil.class;
        ListUtil.newArrayList(type.getDeclaredClasses()).forEach(System.out::println);
    }
}