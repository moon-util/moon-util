package com.moon.data.jdbc.processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * @author benshaoye
 */
abstract class Imported {

    final static boolean REPOSITORY;
    final static boolean AUTOWIRED;
    final static boolean QUALIFIER;

    static {
        boolean repository;
        try {
            Repository.class.toString();
            repository = true;
        } catch (Throwable ignored) {
            repository = false;
        }
        REPOSITORY = repository;
        boolean autowired;
        try {
            Autowired.class.toString();
            autowired = true;
        } catch (Throwable ignored) {
            autowired = false;
        }
        AUTOWIRED = autowired;
        boolean qualifier;
        try {
            Qualifier.class.toString();
            qualifier = true;
        } catch (Throwable ignored) {
            qualifier = false;
        }
        QUALIFIER = qualifier;
    }
}
