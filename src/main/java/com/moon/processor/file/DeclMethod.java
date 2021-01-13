package com.moon.processor.file;

import com.moon.processor.manager.Importer;

/**
 * @author benshaoye
 */
public class DeclMethod {

    private final Importer importer;
    private final String name;

    public DeclMethod(Importer importer, String name) {
        this.importer = importer;
        this.name = name;
    }
}
