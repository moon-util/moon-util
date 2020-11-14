package com.moon.mapping.processing;

/**
 * @author moonsky
 */
final class Manager {

    private final ImportManager importManager;
    private final StaticManager staticManager;
    private final ConvertManager convertManager;

    public Manager() {
        ImportManager importManager = new ImportManager();
        ConvertManager convertManager = new ConvertManager(importManager);
        StaticManager staticManager = new StaticManager(importManager);
        this.convertManager = convertManager;
        this.importManager = importManager;
        this.staticManager = staticManager;
    }

    public StaticManager ofStatic() { return staticManager; }

    public ImportManager ofImport() { return importManager; }

    public ConvertManager ofConvert() { return convertManager; }

    public String onImported(String classname) {
        return importManager.onImported(classname);
    }

    public String onImported(Class<?> classname) {
        return importManager.onImported(classname);
    }

    public String getImports() { return importManager.toString(); }
}
