package com.moon.processor.model;

/**
 * @author benshaoye
 */
public class MappingSimplifyDetail extends MappingDetail {

    public MappingSimplifyDetail(
        String fromName,
        String toName,
        String getterName,
        String setterName,
        boolean getterGeneric,
        boolean setterGeneric
    ) {
        super(fromName, toName, getterName, setterName, null, null, getterGeneric, setterGeneric);
    }
}
