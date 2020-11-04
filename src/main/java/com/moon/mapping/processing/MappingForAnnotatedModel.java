package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;

import java.util.Collections;
import java.util.Map;

/**
 * @author moonsky
 */
final class MappingForAnnotatedModel {

    private final static String MAP_NAME = Map.class.getName();

    private final String rootClass;
    private final Map<String, PropertyModel> propertiesModelMap;
    private final Map<String, Map<String, PropertyModel>> targetModelsMap;

    MappingForAnnotatedModel(
        String rootClass,
        Map<String, PropertyModel> propertiesModelMap,
        Map<String, Map<String, PropertyModel>> targetModelsMap
    ) {
        this.rootClass = rootClass;
        this.propertiesModelMap = propertiesModelMap;
        this.targetModelsMap = targetModelsMap;
    }

    private String getRootClassName() { return rootClass; }

    public Map<String, PropertyModel> getPropertiesModelMap() {
        return propertiesModelMap == null ? Collections.emptyMap() : propertiesModelMap;
    }

    private Map<String, Map<String, PropertyModel>> getTargetModelsMap() {
        return targetModelsMap == null ? Collections.emptyMap() : targetModelsMap;
    }

    private static String formatClassname(String classname) {
        return classname.replace('.', '_');
    }

    private static void doBeforeBuild(StringBuilder builder, String methodName, String fromName, String toName) {
        builder.append("@Override public Object ");
        builder.append(methodName).append("(Object thisObject, Object thatObject) {");
        builder.append(" if (thisObject == null || thatObject == null) { return thatObject; }");
        builder.append(fromName).append(" from=(").append(fromName).append(")thisObject;");
        builder.append(toName).append(" to=(").append(toName).append(")thatObject;");
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
        StringBuilder builder, String fromClassname, Map<String, PropertyModel> thisModelMap
    ) {
        doBeforeBuild(builder, "overrideFromMap", fromClassname, MAP_NAME);
        for (Map.Entry<String, PropertyModel> entry : thisModelMap.entrySet()) {
            PropertyModel model = entry.getValue();
            if (model.hasPublicSetterMethod()) {
                builder.append("from.")
                    .append(model.getSetterName())
                    .append("(to.get(")
                    .append(entry.getKey())
                    .append("));");
            }
        }
        doAfterBuild(builder);
    }

    private static void build$toMap(
        StringBuilder builder, String fromClassname, Map<String, PropertyModel> thisModelMap
    ) {
        doBeforeBuild(builder, "toMap", fromClassname, MAP_NAME);
        for (Map.Entry<String, PropertyModel> entry : thisModelMap.entrySet()) {
            PropertyModel model = entry.getValue();
            if (model.hasPublicGetterMethod()) {
                builder.append("to.put(")
                    .append(entry.getKey())
                    .append(",from.")
                    .append(model.getGetterName())
                    .append("());");
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
        // build$overrideFromMap(builder, fromClassname, thisModelMap);
        build$toMap(builder, fromClassname, thisModelMap);
        builder.append("},");
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        String fromClassname = getRootClassName();
        Map<String, PropertyModel> thisModelMap = getPropertiesModelMap();
        builder.append(" enum ").append(formatClassname(fromClassname));
        builder.append(" implements ").append(BeanMapping.class.getName());
        builder.append(" {");
        for (Map.Entry<String, Map<String, PropertyModel>> entry : getTargetModelsMap().entrySet()) {
            buildMappingEnumOf(builder, fromClassname, entry.getKey(), thisModelMap, entry.getValue());
        }
        return builder.append(" }").toString();
    }
}
