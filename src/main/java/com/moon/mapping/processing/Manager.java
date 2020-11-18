package com.moon.mapping.processing;

/**
 * @author moonsky
 */
final class Manager {

    private final ImportManager importManager;
    private final StaticManager staticManager;
    private final ConvertManager convertManager;
    private final ScopedManager scopedManager;

    public Manager() {
        final ImportManager importManager = new ImportManager();
        ConvertManager convertManager = new ConvertManager(importManager);
        StaticManager staticManager = new StaticManager(importManager);
        ScopedManager scopedManager = new ScopedManager(importManager);
        this.convertManager = convertManager;
        this.importManager = importManager;
        this.staticManager = staticManager;
        this.scopedManager = scopedManager;
    }

    public ScopedManager ofScoped() { return scopedManager; }

    public StaticManager ofStatic() { return staticManager; }

    public ImportManager ofImport() { return importManager; }

    public ConvertManager ofConvert() { return convertManager; }

    public String onImported(String classname) { return ofImport().onImported(classname); }

    public String onImported(Class<?> classname) { return ofImport().onImported(classname); }

    public String getImports() { return importManager.toString(); }
}
