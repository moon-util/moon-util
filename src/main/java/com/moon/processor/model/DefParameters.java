package com.moon.processor.model;

import java.util.LinkedHashMap;

/**
 * @author benshaoye
 */
public class DefParameters extends LinkedHashMap<String, String> {

    private DefParameters() { }

    public static DefParameters of(String name, String type) { return new DefParameters().add(name, type); }

    public DefParameters add(String name, String type) {
        put(name, type);
        return this;
    }
}
