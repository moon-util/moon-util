package com.moon.processor.mapping;

/**
 * @author benshaoye
 */
public class MappingDefaultVal {

    private final String defaultVar;
    private final MappingType type;

    public MappingDefaultVal(String defaultVar, MappingType type) {
        this.defaultVar = defaultVar;
        this.type = type;
    }

    public static MappingDefaultVal of(String defaultVar, MappingType type) {
        return new MappingDefaultVal(defaultVar, type);
    }

    public String getDefaultVar() { return defaultVar; }

    public MappingType getType() { return type; }
}
