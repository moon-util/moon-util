package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;

import java.util.Collections;
import java.util.Map;

/**
 * @author moonsky
 */
final class MappingForModel {

    private final static String MAP_NAME = Map.class.getName();

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

    private static String formatClassname(String classname) {
        return classname.replace('.', '_');
    }

    private static void doBeforeBuild(
        StringBuilder builder, String methodName, String thisName, String thatName
    ) { doBeforeBuild(builder, methodName, thisName, thatName, "Object", "Object", "Object"); }

    private static void doBeforeBuild(
        StringBuilder builder,
        String methodName,
        String thisName,
        String thatName,
        String param1Name,
        String param2Name,
        String returnName
    ) {
        builder.append("@Override public ").append(returnName).append(' ');
        builder.append(methodName).append("(" + param1Name + " thisObject, " + param2Name + " thatObject) {");
        builder.append(" if (thisObject == null || thatObject == null) { return thatObject; }");
        builder.append(thisName).append(" from=(").append(thisName).append(")thisObject;");
        builder.append(thatName).append(" to=(").append(thatName).append(")thatObject;");
    }

    private static void doAfterBuild(StringBuilder builder) {
        builder.append("return thatObject;}");
    }

    private static void build$override(
        StringBuilder builder,
        String fromClassname,
        String toClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {
        doBeforeBuild(builder, "override", fromClassname, toClassname);
        // do mapping ...
        doAfterBuild(builder);
    }

    private static void build$overrideFromMap(
        StringBuilder builder, String thisName, Map<String, PropertyModel> thisModelMap
    ) {
        doBeforeBuild(builder, "overrideFromMap", thisName, MAP_NAME, "Object", MAP_NAME, MAP_NAME);
        for (Map.Entry<String, PropertyModel> entry : thisModelMap.entrySet()) {
            PropertyModel model = entry.getValue();
            if (model.hasPublicSetterMethod()) {
                builder.append("from.").append(model.getSetterName()).append("(to.get(").append(entry.getKey())
                    .append("));");
            }
        }
        doAfterBuild(builder);
    }

    private static void build$toMap(
        StringBuilder builder, String thisName, Map<String, PropertyModel> thisModelMap
    ) {
        doBeforeBuild(builder, "toMap", thisName, MAP_NAME, "Object", MAP_NAME, MAP_NAME);
        for (Map.Entry<String, PropertyModel> entry : thisModelMap.entrySet()) {
            PropertyModel model = entry.getValue();
            if (model.hasPublicGetterMethod()) {
                builder.append("to.put(");
                builder.append('"').append(entry.getKey()).append('"');
                builder.append(",from.").append(model.getGetterName()).append("());");
            }
        }
        doAfterBuild(builder);
    }

    private static void buildMappingEnumOf(
        StringBuilder builder,
        String fromClassname,
        String thatClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {
        builder.append(formatClassname(thatClassname)).append(" {");
        // build$override(builder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        builder.append("},");
    }

    private static void buildMapMapping(
        StringBuilder builder, String fromClassname, Map<String, PropertyModel> thisModelMap
    ) {
        builder.append(";");
        // build$overrideFromMap(builder, fromClassname, thisModelMap);
        build$toMap(builder, fromClassname, thisModelMap);
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
            buildMappingEnumOf(builder, thisClassname, entry.getKey(), thisModelMap, entry.getValue());
        }
        buildMapMapping(builder, thisClassname, thisModelMap);
        return builder.append(" }").toString();
    }
}
