package com.moon.processing.decl;

import com.moon.mapper.annotation.MapperFor;
import com.moon.processing.JavaDeclarable;
import com.moon.processing.JavaProvider;
import com.moon.processing.file.JavaEnumFile;

/**
 * @author benshaoye
 */
public class MapperDeclared implements JavaProvider {


    private final MapperFor mapperFor;
    private final CopierDeclared forward;
    private final CopierDeclared backward;

    public MapperDeclared(MapperFor mapperFor,CopierDeclared forward, CopierDeclared backward) {
        this.mapperFor = mapperFor;
        this.forward = forward;
        this.backward = backward;
    }

    @Override
    public JavaDeclarable getJavaDeclare() {
        // JavaEnumFile enumFile = new JavaEnumFile();
        // return enumFile;
        return null;
    }
}
