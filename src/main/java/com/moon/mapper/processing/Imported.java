package com.moon.mapper.processing;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author moonsky
 */
abstract class Imported {

    final static boolean BEAN;
    final static boolean LOMBOK;
    final static boolean JODA_TIME;
    final static boolean CONFIGURATION;
    final static boolean CONDITIONAL_ON_MISSING_BEAN;

    static {
        boolean hasLombok;
        try {
            Data.class.toString();
            Getter.class.toString();
            Setter.class.toString();
            hasLombok = true;
        } catch (Throwable t) {
            hasLombok = false;
        }
        LOMBOK = hasLombok;
        boolean hasCfg;
        try {
            Configuration.class.toString();
            hasCfg = true;
        } catch (Throwable t) {
            hasCfg = false;
        }
        CONFIGURATION = hasCfg;
        boolean hasCfgMissingBean;
        try {
            ConditionalOnMissingBean.class.toString();
            hasCfgMissingBean = true;
        } catch (Throwable t) {
            hasCfgMissingBean = false;
        }
        CONDITIONAL_ON_MISSING_BEAN = hasCfgMissingBean;
        boolean hasBean;
        try {
            Bean.class.toString();
            hasBean = true;
        } catch (Throwable t) {
            hasBean = false;
        }
        BEAN = hasBean;
        boolean hasJoda;
        try {
            DateTime.now().toString();
            hasJoda = true;
        } catch (Throwable t) {
            hasJoda = false;
        }
        JODA_TIME = hasJoda;
    }
}
