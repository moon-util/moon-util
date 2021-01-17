package com.moon.processor.file;

import java.util.LinkedHashMap;

/**
 * @author benshaoye
 */
public class DeclParams extends LinkedHashMap<String, DeclParamType> implements Cloneable {

    private DeclParams() { }

    public static DeclParams empty() { return DeclParams.of(); }

    public static DeclParams of() { return new DeclParams(); }

    public static DeclParams of(String name, Class<?> type) { return of().addActual(name, type); }

    public static DeclParams of(String name, String type) { return of().addActual(name, type); }

    public DeclParams addActual(String name, Class<?> type) { return addActual(name, type.getCanonicalName()); }

    public DeclParams addActual(String name, String type) {
        put(name, DeclParamType.ofActual(type));
        return this;
    }

    public DeclParams addGeneralization(String name, String type, Class<?> bound) {
        return addGeneralization(name, type, bound.getCanonicalName());
    }

    public DeclParams addGeneralization(String name, String type, String bound) {
        put(name, DeclParamType.ofGeneric(type, bound));
        return this;
    }

    @Override
    public DeclParams clone() { return (DeclParams) super.clone(); }
}
