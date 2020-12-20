package com.moon.data.jdbc.processing;

import javax.lang.model.element.TypeElement;

/**
 * @author benshaoye
 */
final class Manager {

    private final Importer importer;

    public Manager() {
        this.importer = new Importer();
    }

    public String onImported(String classname) { return importer.onImported(classname); }

    public String onImported(Class<?> classname) { return importer.onImported(classname); }

    public String onImported(TypeElement element) { return importer.onImported(element); }
}
