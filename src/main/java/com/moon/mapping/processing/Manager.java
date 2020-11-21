package com.moon.mapping.processing;

/**
 * @author moonsky
 */
final class Manager {

    private final MappingModel model;
    private final ImportManager importManager;
    private final StaticManager staticManager;
    private final MappingManager mappingManager;
    private final ConvertManager convertManager;
    private final ScopedManager scopedManager;

    public Manager() {
        final MappingModel model = new MappingModel();
        final ImportManager importManager = new ImportManager();
        final ConvertManager convertManager = new ConvertManager(importManager);
        final StaticManager staticManager = new StaticManager(importManager);
        final ScopedManager scopedManager = new ScopedManager(importManager);
        final MappingManager mappingManager = new MappingManager(model, importManager);
        this.convertManager = convertManager;
        this.mappingManager = mappingManager;
        this.importManager = importManager;
        this.staticManager = staticManager;
        this.scopedManager = scopedManager;
        this.model = model;
    }

    public boolean canUsableModel(Mappable thisProp, Mappable thatProp, PropertyAttr attr, boolean forward) {
        return getModel().onConvert(thisProp, thatProp, attr, forward).isUsable();
    }

    public MappingModel getModel() { return model; }

    public MappingManager getMapping() { return mappingManager; }

    public ScopedManager ofScoped() { return scopedManager; }

    public StaticManager ofStatic() { return staticManager; }

    public ImportManager ofImport() { return importManager; }

    public ConvertManager ofConvert() { return convertManager; }

    public String onImported(String classname) { return ofImport().onImported(classname); }

    public String onImported(Class<?> classname) { return ofImport().onImported(classname); }

    public String defaultString() {
        return null;
    }

    public String getImports() { return importManager.toString(); }
}
