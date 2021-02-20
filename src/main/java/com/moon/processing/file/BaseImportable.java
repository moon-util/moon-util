package com.moon.processing.file;

import com.moon.processor.holder.Importer;

/**
 * @author benshaoye
 */
public abstract class BaseImportable {

    private final Importer importer;

    public BaseImportable(Importer importer) { this.importer = importer; }

    public Importer getImporter() { return importer; }

    public final String onImported(Class<?> importClass) {
        return importer.onImported(importClass);
    }

    public final String onImported(String classname) {
        return importer.onImported(classname);
    }
}
