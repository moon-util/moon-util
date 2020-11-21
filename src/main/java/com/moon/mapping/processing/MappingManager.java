package com.moon.mapping.processing;

/**
 * @author moonsky
 */
final class MappingManager {

    private final MappingModel model;
    private final ImportManager importManager;

    MappingManager(MappingModel model, ImportManager importManager) {
        this.model = model;
        this.importManager = importManager;
    }

    public String onDeclare(MappingModel model, String mapping, String defaultVal) {
        return get(model.getSetterDeclareType(), model.getGetterDeclareType(), mapping, defaultVal);
    }

    public String get(String setterType, String getterType, String mapping, String defaultVal) {
        if (StringUtils.isPrimitive(getterType)) {
            if (StringUtils.isPrimitive(setterType)) {
                return primitive2primitive(setterType, getterType, mapping, defaultVal);
            } else {
                return primitive2object(setterType, getterType, mapping, defaultVal);
            }
        } else {
            if (StringUtils.isPrimitive(setterType)) {
                return object2primitive(setterType, getterType, mapping, defaultVal);
            } else {
                return object2object(setterType, getterType, mapping, defaultVal);
            }
        }
    }


    private String primitive2primitive(String setterType, String getterType, String mapping, String defaultVal) {
        return primitive2object(setterType, getterType, mapping, defaultVal);
    }

    private String primitive2object(String setterType, String getterType, String mapping, String defaultVal) {
        mapping = mapping == null ? "{var}" : mapping;
        int count = StringUtils.count(mapping, "{var}");
        final String getValue = "{fromName}.{getterName}()";
        final String singletonVar = "{toName}.{setterName}({MAPPINGS});";
        final String multiplyVar = "{getterType} {var} = {fromName}.{getterName}();" + singletonVar;
        final String t0;
        if (count == 0) {
            t0 = singletonVar;
        } else if (count == 1) {
            t0 = singletonVar;
            mapping = Replacer.var.replace(mapping, getValue);
        } else {
            t0 = multiplyVar;
        }
        return doReplaceVariables(t0, setterType, getterType, mapping);
    }

    private String object2object(String setterType, String getterType, String mapping, String defaultVal) {
        @SuppressWarnings("all")
        final String nonDefaultVal = "" +//
            "{getterType} {var} = {fromName}.{getterName}();" +//
            "{toName}.{setterName}({var} == null ? null : {MAPPINGS});";
        @SuppressWarnings("all")
        final String hasDefaultVal = "" +//
            "{getterType} {var} = {fromName}.{getterName}();" +//
            "{toName}.{setterName}({var} == null ? {value} : {MAPPINGS});";
        return doReplaceVariables(setterType, getterType, mapping, defaultVal, nonDefaultVal, hasDefaultVal);
    }

    private String object2primitive(String setterType, String getterType, String mapping, String defaultVal) {
        @SuppressWarnings("all")
        final String nonDefaultVal = "" +//
            "{getterType} {var} = {fromName}.{getterName}();" +//
            "if ({var} != null) { {toName}.{setterName}({MAPPINGS}); }";
        @SuppressWarnings("all")
        final String hasDefaultVal = "" +//
            "{getterType} {var} = {fromName}.{getterName}();" +//
            "{toName}.{setterName}({var} == null ? {value} :{MAPPINGS});";
        return doReplaceVariables(setterType, getterType, mapping, defaultVal, nonDefaultVal, hasDefaultVal);
    }

    private String doReplaceVariables(
        String setterType,
        String getterType,
        String mapping,
        String defaultVal,
        String nonDefaultVal,
        String hasDefaultVal
    ) {
        final String template = defaultVal == null ? nonDefaultVal : hasDefaultVal;
        String t0 = Replacer.value.replace(template, defaultVal);
        return doReplaceVariables(t0, setterType, getterType, mapping);
    }

    private String doReplaceVariables(
        String t0, String setterType, String getterType, String mapping
    ) {
        t0 = Replacer.MAPPINGS.replace(t0, mapping == null ? "{var}" : mapping);
        t0 = Replacer.setterType.replace(t0, importManager.onImported(setterType));
        return Replacer.getterType.replace(t0, importManager.onImported(getterType));
    }
}
