package com.moon.processor.utils;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;

/**
 * @author benshaoye
 */
public enum Log2 {
    ;
    private static Messager messager;

    public static void initialize(ProcessingEnvironment env){
        Log2.messager = env.getMessager();
    }
}
