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
    private final WarningManager warningManager;

    public Manager() {
        final MappingModel model = new MappingModel();
        final ImportManager importManager = new ImportManager();
        final StaticManager staticManager = new StaticManager(importManager);
        final ScopedManager scopedManager = new ScopedManager(importManager);
        final WarningManager warningManager = new WarningManager(model);
        final MappingManager mappingManager = new MappingManager(model, importManager);
        this.convertManager = new ConvertManager(mappingManager, warningManager, importManager);
        this.mappingManager = mappingManager;
        this.warningManager = warningManager;
        this.importManager = importManager;
        this.staticManager = staticManager;
        this.scopedManager = scopedManager;
        this.model = model;
    }

    public boolean canUsableModel(Mappable thisProp, Mappable thatProp, PropertyAttr attr, ConvertStrategy strategy) {
        return getModel().onConvert(thisProp, thatProp, attr, strategy).isUsable();
    }

    public MappingModel getModel() { return model; }

    public MappingManager getMapping() { return mappingManager; }

    public ScopedManager ofScoped() { return scopedManager; }

    public ConvertManager ofConvert() { return convertManager; }

    private StaticManager ofStatic() { return staticManager; }

    private ImportManager ofImport() { return importManager; }

    public String onImported(String classname) { return ofImport().onImported(classname); }

    public String onImported(Class<?> classname) { return ofImport().onImported(classname); }

    public void setupWarning() { warningManager.reset(); }

    public String toStringForImports() { return ofImport().toString(); }

    public String toStringForStaticVars() { return ofStatic().toString(); }

    /*
    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ~~~ 静态变量管理
    ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public String staticVarForEnumValuesOnDeclare() {
        return staticVarForEnumValues(setterDeclareType());
    }

    public String staticVarForEnumValues(String enumClassname) {
        return ofStatic().onEnumValues(enumClassname);
    }

    public String staticVarForJodaDateTimeFormatter(String formatVal) {
        return ofStatic().onJodaDateTimeFormat(formatVal);
    }

    public String staticVarForDateTimeFormatter(String formatVal) {
        return ofStatic().onDateTimeFormatter(formatVal);
    }

    public String staticVarForDefaultBigInteger() {
        String defaultValue = defaultVal();
        if (defaultValue != null) {
            return ofStatic().onDefaultBigInteger(defaultValue);
        }
        return ofStatic().defaultNull();
    }

    public String staticVarForDefaultBigDecimal() {
        String defaultValue = defaultVal();
        if (defaultValue != null) {
            return ofStatic().onDefaultBigDecimal(defaultValue);
        }
        return ofStatic().defaultNull();
    }

    public String staticVarForDefaultChar() {
        return ofStatic().onDefaultChar(defaultVal());
    }

    public String staticVarForDefaultBooleanOnDeclare() {
        return staticVarForDefaultBoolean(setterDeclareType());
    }

    public String staticVarForDefaultBoolean(String classname) {
        return ofStatic().onDefaultBoolean(classname, defaultVal());
    }

    public String staticVarForDefaultNumberOnDeclare() {
        return staticVarForDefaultNumber(setterDeclareType());
    }

    public String staticVarForDefaultNumber(String expectedClass) {
        String defaultValue = defaultVal();
        if (defaultValue != null) {
            return ofStatic().onDefaultNumber(expectedClass, defaultValue);
        }
        return ofStatic().defaultNull();
    }

    public String staticVarForDefaultNumberValueOf(String classname, String value) {
        return ofStatic().onDefaultNumber(classname, value);
    }

    public String staticVarForDefaultString() {
        return ofStatic().onString(defaultVal());
    }

    public String staticVarForDefaultEnumOnDeclare() {
        return doStaticVarForDefaultEnum(setterDeclareType());
    }

    private String doStaticVarForDefaultEnum(String enumClassname) {
        String dftValue = defaultVal();
        if (DetectUtils.isDigit(dftValue)) {
            return ofStatic().onEnumIndexed(enumClassname, dftValue);
        } else if (DetectUtils.isVar(dftValue)) {
            return ofStatic().onEnumNamed(enumClassname, dftValue);
        } else {
            return ofStatic().defaultNull();
        }
    }

    private String setterDeclareType() { return getModel().getSetterDeclareType(); }

    private String defaultVal() { return getModel().getAttr().defaultValue(); }
}
