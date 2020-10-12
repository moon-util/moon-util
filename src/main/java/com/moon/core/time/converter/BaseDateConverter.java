package com.moon.core.time.converter;

import com.moon.core.time.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.groupingBy;

/**
 * @author benshaoye
 */
public abstract class BaseDateConverter {

    private final static Map<Integer, PatternMatcher[]> matcherMap = new HashMap<>();

    private final static void addFormatters(String[] patterns) {
        Function<List<PatternMatcher>, PatternMatcher[]> applier = list -> {
            List<PatternMatcher> newList = new ArrayList<>(list);
            newList.add(0, list.get(0));
            return newList.toArray(new PatternMatcher[newList.size()]);
        };
        Stream.of(patterns)
            .map(PatternMatcher::new)
            .collect(groupingBy(matcher -> matcher.getPattern().length()))
            .forEach((len, list) -> matcherMap.put(len, applier.apply(list)));
    }

    private static final class PatternMatcher implements Predicate<String> {

        final Supplier<DateFormat> supplier;
        final DateTimeFormatter formatter;
        private final String pattern;

        private PatternMatcher(String pattern) {
            supplier = () -> new SimpleDateFormat(pattern);
            formatter = ofPattern(pattern);

            this.pattern = pattern.replaceAll("'T'", "T");
        }

        DateFormat getDateFormat() { return getSupplier().get(); }

        public Supplier<DateFormat> getSupplier() { return supplier; }

        public DateTimeFormatter getFormatter() { return formatter; }

        public String getPattern() { return pattern; }

        @Override
        public boolean test(String value) {
            if (value == null) {
                return false;
            }

            String pattern = this.pattern;
            int valLen = value.length();
            if (valLen != pattern.length()) {
                return false;
            }

            char holder, val;
            for (int i = 0; i < valLen; i++) {
                holder = pattern.charAt(i);
                val = value.charAt(i);
                if (isDigit(val) && isLetter(holder)) {
                    continue;
                } else if (val == holder) {
                    continue;
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() { return pattern; }
    }

    static {
        // 慎用 YYYY，跨年的时候会引起错误
        addFormatters(new String[]{
            "yyyy年MM月dd日 HH时mm分ss.SSS",
            "yyyy年MM月dd日 HH时mm分ss秒SSS",
            "yyyy年MM月dd日 HH时",
            "yyyy年MM月dd日 HH时mm分",
            "yyyy年MM月dd日 HH时mm分ss秒",

            "yyyy年MM月dd日HH时mm分ss.SSS",
            "yyyy年MM月dd日HH时mm分ss秒SSS",
            "yyyy年MM月dd日HH时",
            "yyyy年MM月dd日HH时mm分",
            "yyyy年MM月",
            "yyyy年MM月dd日",
            "yyyy年MM月dd日HH时mm分ss秒",

            "yyyy/MM/dd'T'HH:mm:ss.SSS",
            "yyyy/MM/dd'T'HH:mm:ss SSS",
            "yyyy/MM/dd'T'HH",
            "yyyy/MM/dd'T'HH:mm",
            "yyyy/MM/dd'T'HH:mm:ss",

            "yyyy/MM/dd HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss SSS",
            "yyyy/MM/dd HH",
            "yyyy/MM/dd HH:mm",
            "yyyy/MM",
            "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss",

            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss SSS",
            "yyyy-MM-dd'T'HH",
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd'T'HH:mm:ss",

            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss SSS",
            "yyyy-MM-dd HH",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM",
            "yyyy-MM-dd",
            "yyyy-MM-dd HH:mm:ss",
        });
    }

    private static PatternMatcher findMatcher(String value) {
        PatternMatcher matcher;
        PatternMatcher[] matchersArr = matcherMap.get(value.length());
        for (int i = 0; i < matchersArr.length; i++) {
            matcher = matchersArr[i];
            if (matcher != null && matcher.test(value)) {
                if (i > 0) {
                    matchersArr[0] = matcher;
                }
                return matcher;
            }
        }
        return null;
    }

    public static DateFormat findFormat(String value) {
        PatternMatcher matcher = findMatcher(value);
        return matcher == null ? null : matcher.getDateFormat();
    }

    public static DateTimeFormatter findFormatter(String value) {
        PatternMatcher matcher = findMatcher(value);
        return matcher == null ? null : matcher.getFormatter();
    }

    protected final static Date parseToDate(CharSequence value) {
        String dateStr = value.toString().trim();
        DateFormat format = getFormat(dateStr);
        if (format == null) {
            return DateUtil.parseToDate(dateStr);
        }
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    protected static DateFormat getFormat(String value) { return findFormat(value); }

    protected static DateTimeFormatter getFormatter(String value) { return findFormatter(value); }
}
