package com.moon.processor.utils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author benshaoye
 */
public class Imported {

    public final static boolean BEAN;
    public final static boolean LOMBOK;
    public final static boolean AUTOWIRED;
    public final static boolean QUALIFIER;
    public final static boolean JODA_TIME;
    public final static boolean CONFIGURATION;
    public final static boolean CONDITIONAL_ON_MISSING_BEAN;

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
        boolean autowired;
        try {
            Autowired.class.toString();
            autowired = true;
        } catch (Throwable t) {
            autowired = false;
        }
        AUTOWIRED = autowired;
        boolean qualifier;
        try {
            Qualifier.class.toString();
            qualifier = true;
        } catch (Throwable t) {
            qualifier = false;
        }
        QUALIFIER = qualifier;
    }
}
