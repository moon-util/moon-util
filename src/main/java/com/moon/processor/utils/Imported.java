package com.moon.processor.utils;

import lombok.Data;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import javax.annotation.Generated;
import java.util.function.Supplier;

/**
 * @author benshaoye
 */
public enum Imported {
    ;

    public final static boolean BEAN;
    public final static boolean LOMBOK;
    public final static boolean AUTOWIRED;
    public final static boolean QUALIFIER;
    public final static boolean GENERATED;
    public final static boolean JODA_TIME;
    public final static boolean REPOSITORY;
    public final static boolean CONFIGURATION;
    public final static boolean CONDITIONAL_ON_MISSING_BEAN;

    static {
        LOMBOK = isNoException(() -> Data.class.toString());
        CONFIGURATION = isNoException(() -> Configuration.class.toString());
        CONDITIONAL_ON_MISSING_BEAN = isNoException(() -> ConditionalOnMissingBean.class.toString());
        BEAN = isNoException(() -> Bean.class.toString());
        GENERATED = isNoException(() -> Generated.class.toString());
        JODA_TIME = isNoException(() -> DateTime.class.toString());
        AUTOWIRED = isNoException(() -> Autowired.class.toString());
        QUALIFIER = isNoException(() -> Qualifier.class.toString());
        REPOSITORY = isNoException(() -> Repository.class.toString());
    }

    private static boolean isNoException(Supplier<String> runner) {
        try {
            runner.get();
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
