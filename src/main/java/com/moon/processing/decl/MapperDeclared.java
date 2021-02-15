package com.moon.processing.decl;

import com.moon.mapper.annotation.MapperFor;

/**
 * @author benshaoye
 */
public class MapperDeclared {


    private final MapperFor mapperFor;
    private final CopierDeclared forward;
    private final CopierDeclared backward;

    public MapperDeclared(MapperFor mapperFor,CopierDeclared forward, CopierDeclared backward) {
        this.mapperFor = mapperFor;
        this.forward = forward;
        this.backward = backward;
    }
}
