package com.moon.processor.file;

import java.util.LinkedHashMap;

/**
 * @author benshaoye
 */
public class DeclParams extends LinkedHashMap<String, String> {

    private DeclParams() { }

    public static DeclParams empty() { return DeclParams.of(); }

    public static DeclParams of() { return new DeclParams(); }

    public static DeclParams of(String name, Class<?> type) { return of().add(name, type); }

    public static DeclParams of(String name, String type) { return of().add(name, type); }

    public DeclParams add(String name, Class<?> type) { return add(name, type.getCanonicalName()); }

    public DeclParams add(String name, String type) {
        put(name, type);
        return this;
    }
}
