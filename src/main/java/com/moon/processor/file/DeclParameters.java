package com.moon.processor.file;

import java.util.LinkedHashMap;

/**
 * @author benshaoye
 */
public class DeclParameters extends LinkedHashMap<String, String> {

    private DeclParameters() { }

    public static DeclParameters empty() { return DeclParameters.of(); }

    public static DeclParameters of() { return new DeclParameters(); }

    public static DeclParameters of(String name, String type) { return of().add(name, type); }

    public DeclParameters add(String name, String type) {
        put(name, type);
        return this;
    }
}
