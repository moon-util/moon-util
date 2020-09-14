package com.moon.core.time.converter;

import org.springframework.core.convert.converter.Converter;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author benshaoye
 */
public class FormDateConverter extends BaseDateConverter {

    /**
     * spring web converter for date
     *
     * @return converters
     */
    public static List<Converter<?, ?>> getConverters() {
        List<Converter<?, ?>> converterList = new ArrayList<>();

        converterList.add(new FormStringToSqlDateConverter());
        converterList.add(new FormStringToTimestampConverter());
        converterList.add(new FormStringToDateConverter());
        converterList.add(new FormStringToTimeConverter());

        converterList.add(new FormLongToDateConverter());
        converterList.add(new FormLongToTimeConverter());
        converterList.add(new FormLongToSqlDateConverter());
        converterList.add(new FormLongToTimestampConverter());

        return converterList;
    }

    private static class FormStringToSqlDateConverter implements Converter<String, java.sql.Date> {

        @Override
        public java.sql.Date convert(String source) { return new java.sql.Date(parseToDate(source).getTime()); }
    }

    private static class FormStringToTimeConverter implements Converter<String, Time> {

        @Override
        public Time convert(String source) { return new Time(parseToDate(source).getTime()); }
    }

    private static class FormStringToTimestampConverter implements Converter<String, Timestamp> {

        @Override
        public Timestamp convert(String source) { return new Timestamp(parseToDate(source).getTime()); }
    }

    private static class FormStringToDateConverter implements Converter<String, Date> {

        @Override
        public Date convert(String source) { return parseToDate(source); }
    }

    private static class FormLongToDateConverter implements Converter<Long, Date> {

        @Override
        public Date convert(Long source) { return new Date(source); }
    }

    private static class FormLongToTimeConverter implements Converter<Long, Time> {

        @Override
        public Time convert(Long source) { return new Time(source); }
    }

    private static class FormLongToTimestampConverter implements Converter<Long, Timestamp> {

        @Override
        public Timestamp convert(Long source) { return new Timestamp(source); }
    }

    private static class FormLongToSqlDateConverter implements Converter<Long, java.sql.Date> {

        @Override
        public java.sql.Date convert(Long source) { return new java.sql.Date(source); }
    }
}
