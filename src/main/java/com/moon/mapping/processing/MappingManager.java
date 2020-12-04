package com.moon.mapping.processing;

/**
 * @author moonsky
 */
final class MappingManager {

    private ModeStrategy type;
    private final PropertyModel model;
    private final ImportManager importManager;

    MappingManager(PropertyModel model, ImportManager importManager) {
        this.model = model;
        this.importManager = importManager;
    }

    private ModeStrategy getType() { return type == null ? ModeStrategy.DECLARE : type; }

    public void withPropertyType(ModeStrategy type) { this.type = type; }

    final PropertyModel getModel() { return model; }

    public String getSetterType() { return getType().getSetterType(getModel()); }

    public String getGetterType() { return getType().getGetterType(getModel()); }

    public String doMap(String mapping, String defaultVal) {
        return doMap(getModel(), mapping, defaultVal);
    }

    private String doMap(PropertyModel model, String mapping, String defaultVal) {
        boolean mandatory = model.isSetterGeneric() || model.isGetterGeneric();
        return get(getType().getSetterType(model), getType().getGetterType(model), mapping, defaultVal, mandatory);
    }

    public String get(String setterType, String getterType, String mapping, String defaultVal, boolean mandatory) {
        if (StringUtils.isPrimitive(getterType)) {
            if (StringUtils.isPrimitive(setterType)) {
                return primitive2primitive(setterType, getterType, mapping, defaultVal, mandatory);
            } else {
                return primitive2object(setterType, getterType, mapping, defaultVal, mandatory);
            }
        } else {
            if (StringUtils.isPrimitive(setterType)) {
                return object2primitive(setterType, getterType, mapping, defaultVal, mandatory);
            } else {
                return object2object(setterType, getterType, mapping, defaultVal, mandatory);
            }
        }
    }


    private String primitive2primitive(
        String setterType, String getterType, String mapping, String defaultVal, boolean mandatory
    ) {
        return primitive2object(setterType, getterType, mapping, defaultVal, mandatory);
    }

    private String primitive2object(
        String setterType, String getterType, String mapping, String defaultVal, boolean mandatory
    ) {
        mapping = formatMapping(mapping, mandatory);
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

    private String object2object(
        String setterType, String getterType, String mapping, String defaultVal, boolean mandatory
    ) {
        mapping = formatMapping(mapping, mandatory);
        @SuppressWarnings("all")
        final String nonDefaultVal;
        @SuppressWarnings("all")
        final String hasDefaultVal = "" +//
            "{getterType} {var} = {fromName}.{getterName}();" +//
            "{toName}.{setterName}({var} == null ? {value} : {MAPPINGS});";
        if (defaultVal == null) {
            String t0 = "{toName}.{setterName}({MAPPINGS});";
            final int count = StringUtils.count(mapping, "{var}");
            if (count == 0) {
                nonDefaultVal = t0;
            } else if (count == 1) {
                nonDefaultVal = t0;
                final String getValue = "{fromName}.{getterName}()";
                mapping = Replacer.var.replace(mapping, getValue);
            } else {
                nonDefaultVal = "" +//
                    "{getterType} {var} = {fromName}.{getterName}();" +//
                    "{toName}.{setterName}({MAPPINGS});";
            }
        } else {
            nonDefaultVal = null;
        }
        return doReplaceVariables(setterType, getterType, mapping, defaultVal, nonDefaultVal, hasDefaultVal);
    }

    private String object2primitive(
        String setterType, String getterType, String mapping, String defaultVal, boolean mandatory
    ) {
        mapping = formatMapping(mapping, mandatory);
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
        t0 = Replacer.MAPPINGS.replace(t0, mapping);
        t0 = Replacer.setterType.replace(t0, importManager.onImported(setterType));
        return Replacer.getterType.replace(t0, importManager.onImported(getterType));
    }

    private static String formatMapping(String mapping, boolean mandatory) {
        mapping = mapping == null ? "{var}" : mapping;
        return mandatory ? "({setterType})" + mapping : mapping;
    }
}
