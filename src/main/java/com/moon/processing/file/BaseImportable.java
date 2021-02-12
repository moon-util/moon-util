package com.moon.processing.file;

import com.moon.processor.holder.Importer;

/**
 * @author benshaoye
 */
abstract class BaseImportable {

    private final Importer importer;

    public BaseImportable(Importer importer) { this.importer = importer; }

    public Importer getImporter() { return importer; }

    public BaseImportable onImported(Class<?> importClass) {
        importer.onImported(importClass);
        return this;
    }

    public BaseImportable onImported(String classname) {
        importer.onImported(classname);
        return this;
    }
}
