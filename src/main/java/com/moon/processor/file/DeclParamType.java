package com.moon.processor.file;

/**
 * @author benshaoye
 */
public class DeclParamType {

    private final String type;
    private final String bound;
    private final boolean generic;

    private DeclParamType(String type, String bound, boolean generic) {
        this.type = type;
        this.bound = bound;
        this.generic = generic;
    }

    public String getType() { return type; }

    public String getBound() { return bound; }

    @Override
    public String toString() { return isGeneric() ? bound : type; }

    public boolean isGeneric() { return generic; }

    public static DeclParamType ofActual(String type) {
        return new DeclParamType(type, null, false);
    }

    public static DeclParamType ofGeneric(String type, String bound) {
        return new DeclParamType(type, bound, true);
    }
}
