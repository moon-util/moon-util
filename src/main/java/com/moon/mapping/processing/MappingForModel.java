package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;

import java.util.Collections;
import java.util.Map;

/**
 * @author moonsky
 */
final class MappingForModel {

    private final static char LINE_SEPARATOR = '\n';
    private final static String MAP_NAME = Map.class.getName(), OBJ_NAME = "Object";

    private final String thisClassname;
    private final Map<String, PropertyModel> propertiesModelMap;
    private final Map<String, Map<String, PropertyModel>> targetModelsMap;

    MappingForModel(
        String thisClassname,
        Map<String, PropertyModel> propertiesModelMap,
        Map<String, Map<String, PropertyModel>> targetModelsMap
    ) {
        this.thisClassname = thisClassname;
        this.propertiesModelMap = propertiesModelMap;
        this.targetModelsMap = targetModelsMap;
    }

    private String getThisClassname() { return thisClassname; }

    public Map<String, PropertyModel> getPropertiesModelMap() {
        return propertiesModelMap == null ? Collections.emptyMap() : propertiesModelMap;
    }

    private Map<String, Map<String, PropertyModel>> getTargetModelsMap() {
        return targetModelsMap == null ? Collections.emptyMap() : targetModelsMap;
    }

    private static String formatClassname(String classname) { return classname.replace('.', '_'); }

    private static void doBeforeBuild(
        StringBuilder builder,
        String methodName,
        String thisName,
        String thatName,
        String param1Name,
        String param2Name,
        String returnName,
        boolean returnThis
    ) {
        builder.append("@Override public ").append(returnName).append(' ');
        builder.append(methodName).append("(" + param1Name + " thisObject, " + param2Name + " thatObject) {");
        builder.append(" if (thisObject == null || thatObject == null)");
        builder.append("{ return ").append(returnThis ? "thisObject" : "thatObject").append("; }");
        builder.append(thisName).append(" from=(").append(thisName).append(")thisObject;");
        builder.append(thatName).append(" to=(").append(thatName).append(")thatObject;");
    }

    private static void returnThatObject(StringBuilder builder) { builder.append("return thatObject;}"); }

    private static void returnThisObject(StringBuilder builder) { builder.append("return thisObject;}"); }

    private static void as$fromMap(
        StringBuilder builder, String thisName, Map<String, PropertyModel> thisModelMap
    ) {
        int index = 0;
        doBeforeBuild(builder, "fromMap", thisName, MAP_NAME, OBJ_NAME, MAP_NAME, OBJ_NAME, true);
        for (Map.Entry<String, PropertyModel> entry : thisModelMap.entrySet()) {
            PropertyModel property = entry.getValue();
            if (property.hasPublicSetterMethod()) {
                if (property.isPrimitiveSetterMethod()) {
                    String varName = "var" + (index++);
                    String varType = property.getWrappedSetterType();
                    /**
                     * 类似：
                     * <pre>
                     * Boolean var0 = to.get("male");
                     * if (var0 != null) {
                     *     from.setMale(var0);
                     * }
                     * </pre>
                     */
                    builder.append(varType)
                        .append(' ')
                        .append(varName)
                        .append("=(")
                        .append(varType)
                        .append(")")
                        .append("to.get(")
                        .append('"')
                        .append(entry.getKey())
                        .append('"')
                        .append(");")
                        .append("if (")
                        .append(varName)
                        .append(" != null) {")
                        .append("from.")
                        .append(property.getSetterName())
                        .append('(')
                        .append(varName)
                        .append(");}");
                } else {
                    // 类似: from.setName(to.get("name"));
                    builder.append("from.")
                        .append(property.getSetterName())
                        .append("((")
                        .append(property.getDefaultSetterType())
                        .append(")to.get(")
                        .append('"')
                        .append(entry.getKey())
                        .append('"')
                        .append("));");
                }
            }
        }
        returnThisObject(builder);
    }

    private static void as$toMap(
        StringBuilder builder, String thisName, Map<String, PropertyModel> thisModelMap
    ) {
        doBeforeBuild(builder, "toMap", thisName, MAP_NAME, OBJ_NAME, MAP_NAME, MAP_NAME, false);
        for (Map.Entry<String, PropertyModel> entry : thisModelMap.entrySet()) {
            PropertyModel property = entry.getValue();
            if (property.hasPublicGetterMethod()) {
                // 类似: to.put("name", from.getName());
                builder.append("to.put(");
                builder.append('"').append(entry.getKey()).append('"');
                builder.append(",from.").append(property.getGetterName()).append("());");
            }
        }
        returnThatObject(builder);
    }

    private static void buildMapMapping(
        StringBuilder builder, String fromClassname, Map<String, PropertyModel> thisModelMap
    ) {
        as$fromMap(builder, fromClassname, thisModelMap);
        as$toMap(builder, fromClassname, thisModelMap);
    }

    private static void buildObjectMapping(
        StringBuilder builder, String fromClassname, Map<String, PropertyModel> thisModelMap
    ) {
    }

    private static void build$toString() {}

    private static void build$toTableString() {}

    private static void build$isPropertiesEquals() {}

    private static void build$newThis(
        StringBuilder builder,
        String fromClassname,
        String toClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {

    }

    private static void build$newThat(
        StringBuilder builder,
        String fromClassname,
        String toClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {

    }

    private static void build$toThat(
        StringBuilder builder,
        String fromClassname,
        String toClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {
        doBeforeBuild(builder, "toThat", fromClassname, toClassname, OBJ_NAME, OBJ_NAME, OBJ_NAME, false);
        for (Map.Entry<String, PropertyModel> thisEntry : thisModelMap.entrySet()) {
            final String name = thisEntry.getKey();
            PropertyModel thisModel = thisEntry.getValue();
            PropertyModel thatModel = thatModelMap.get(name);
            if (isUsable(thisModel, thatModel)) {
                builder.append("to.")
                    .append(thatModel.getSetterName())
                    .append("(from.")
                    .append(thisModel.getGetterName())
                    .append("());");
            }
        }
        returnThatObject(builder);
    }

    private static void build$fromThat(
        StringBuilder builder,
        String fromClassname,
        String toClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {
        doBeforeBuild(builder, "fromThat", fromClassname, toClassname, OBJ_NAME, OBJ_NAME, OBJ_NAME, true);
        for (Map.Entry<String, PropertyModel> thisEntry : thisModelMap.entrySet()) {
            final String name = thisEntry.getKey();
            PropertyModel thisModel = thisEntry.getValue();
            PropertyModel thatModel = thatModelMap.get(name);
            if (isUsable(thisModel, thatModel)) {
                builder.append("from.")
                    .append(thisModel.getSetterName())
                    .append("(to.")
                    .append(thatModel.getGetterName())
                    .append("());");
            }
        }
        returnThisObject(builder);
    }

    private static boolean isUsable(PropertyModel thisModel, PropertyModel thatModel) {
        boolean hasThis = thisModel != null && thisModel.hasPublicGetterMethod();
        boolean hasThat = thatModel != null && thatModel.hasPublicSetterMethod();
        return hasThat && hasThis;
    }

    private static void buildBeanMapping(
        StringBuilder builder,
        String fromClassname,
        String thatClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {
        builder.append(formatClassname(thatClassname)).append(" {");
        build$toThat(builder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        build$fromThat(builder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        build$newThat(builder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        build$newThis(builder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        builder.append("},");
    }

    public String getGeneratedMappingName() {
        return "MoonBeanMappingsGenerated_" + formatClassname(getThisClassname());
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        final String thisClassname = getThisClassname();
        Map<String, PropertyModel> thisModelMap = getPropertiesModelMap();
        builder.append("package ").append(MappingUtil.class.getPackage().getName()).append(';');
        builder.append(" enum ").append(getGeneratedMappingName());
        builder.append(" implements ").append(BeanMapping.class.getName());
        builder.append(" {");
        for (Map.Entry<String, Map<String, PropertyModel>> entry : getTargetModelsMap().entrySet()) {
            buildBeanMapping(builder, thisClassname, entry.getKey(), thisModelMap, entry.getValue());
        }
        builder.append(";");
        buildObjectMapping(builder, thisClassname, thisModelMap);
        buildMapMapping(builder, thisClassname, thisModelMap);
        return builder.append(" }").toString();
    }
}
