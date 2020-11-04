package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author moonsky
 */
final class MappingForModel {

    private final static char LINE_SEPARATOR = '\n';
    private final static String PACKAGE = MappingUtil.class.getPackage().getName();
    private final static String INTERFACE = BeanMapping.class.getName();
    private final static String MAP_NAME = Map.class.getName(), OBJ_NAME = "Object";

    private final Implementation implementation;
    private final ProcessingEnvironment env;
    private final String thisClassname;
    private final Map<String, PropertyModel> propertiesModelMap;
    private final Map<String, Map<String, PropertyModel>> targetModelsMap;

    MappingForModel(
        ProcessingEnvironment env,
        String thisClassname,
        Map<String, PropertyModel> propertiesModelMap,
        Map<String, Map<String, PropertyModel>> targetModelsMap
    ) {
        this.thisClassname = thisClassname;
        this.propertiesModelMap = propertiesModelMap;
        this.targetModelsMap = targetModelsMap;
        this.implementation = new Implementation(env);
        this.env = env;
    }

    private Implementation getImpl(){ return implementation; }

    private String getThisClassname() { return thisClassname; }

    public Map<String, PropertyModel> getPropertiesModelMap() {
        return propertiesModelMap == null ? Collections.emptyMap() : propertiesModelMap;
    }

    private Map<String, Map<String, PropertyModel>> getTargetModelsMap() {
        return targetModelsMap == null ? Collections.emptyMap() : targetModelsMap;
    }

    private static String formatClassname(String classname) { return classname.replace('.', '_'); }

    private  void doBeforeBuild(
        MappingForStringer builder,
        String methodName,
        String thisName,
        String thatName,
        String param1Name,
        String param2Name,
        String returnName,
        boolean returnThis
    ) {
        builder.add("@Override public ").add(returnName).add(' ');
        builder.add(methodName).add("(").add(param1Name).add(" thisObject, ");
        builder.add(param2Name).add(" thatObject) {");
        builder.add(" if (thisObject == null || thatObject == null)");
        builder.add("{ return ").add(returnThis ? "thisObject" : "thatObject").add("; }");
        builder.add(thisName).add(" from=(").add(thisName).add(")thisObject;");
        builder.add(thatName).add(" to=(").add(thatName).add(")thatObject;");
    }

    private  void returnThatObject(MappingForStringer builder) { builder.add("return thatObject;}"); }

    private  void returnThisObject(MappingForStringer builder) { builder.add("return thisObject;}"); }

    private  void as$fromMap(
        MappingForStringer builder, String thisName, Map<String, PropertyModel> thisModelMap
    ) {
        int index = 0;
        doBeforeBuild(builder, "fromMap", thisName, MAP_NAME, OBJ_NAME, MAP_NAME, OBJ_NAME, true);
        for (Map.Entry<String, PropertyModel> entry : thisModelMap.entrySet()) {
            PropertyModel property = entry.getValue();
            if (property.hasPublicSetterMethod()) {
                if (property.isPrimitiveSetterMethod()) {
                    String varName = "var" + (index++);
                    String varType = property.getWrappedSetterType();
                    /*
                    类似：
                    <pre>
                    Boolean var0 = to.get("male");
                    if (var0 != null) {
                        from.setMale(var0);
                    }
                    </pre>
                     */
                    builder.add(varType).add(' ').add(varName).add("=(").add(varType).add(")").add("to.get(").add('"')
                        .add(entry.getKey()).add('"').add(");").add("if (").add(varName).add(" != null) {").add("from.")
                        .add(property.getSetterName()).add('(').add(varName).add(");}");
                } else {
                    /*
                    <pre>
                    类似: from.setName(to.get("name"));
                    </pre>
                     */
                    builder.add("from.").add(property.getSetterName()).add("((").add(property.getSetterActualType())
                        .add(")to.get(").add('"').add(entry.getKey()).add('"').add("));");
                }
            }
        }
        returnThisObject(builder);
    }

    private  void as$toMap(
        MappingForStringer builder, String thisName, Map<String, PropertyModel> thisModelMap
    ) {
        doBeforeBuild(builder, "toMap", thisName, MAP_NAME, OBJ_NAME, MAP_NAME, MAP_NAME, false);
        for (Map.Entry<String, PropertyModel> entry : thisModelMap.entrySet()) {
            PropertyModel property = entry.getValue();
            if (property.hasPublicGetterMethod()) {
                /*
                <pre>
                类似: to.put("name", from.getName());
                </pre>
                 */
                builder.add("to.put(");
                builder.add('"').add(entry.getKey()).add('"');
                builder.add(",from.").add(property.getGetterName()).add("());");
            }
        }
        returnThatObject(builder);
    }

    private  void buildMapMapping(
        MappingForStringer builder, String fromClassname, Map<String, PropertyModel> thisModelMap
    ) {
        as$fromMap(builder, fromClassname, thisModelMap);
        as$toMap(builder, fromClassname, thisModelMap);
    }

    private void build$toString(
        MappingForStringer builder, String fromClassname, Map<String, PropertyModel> thisModelMap
    ) {
        Implementation impl = getImpl();
        List<String> joinFields = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, PropertyModel> entry : thisModelMap.entrySet()) {
            PropertyModel model = entry.getValue();
            if (model != null && model.hasPublicGetterMethod()) {
                joinFields.add(impl.toStringField(model, (index++) == 0));
            }
        }
        builder.add(impl.toStringMethod(fromClassname, joinFields));
    }

    private void buildObjectMapping(
        MappingForStringer builder, String fromClassname, Map<String, PropertyModel> thisModelMap
    ) { build$toString(builder, fromClassname, thisModelMap); }

    private  void build$newThis(
        MappingForStringer builder,
        String fromClassname,
        String toClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {
    }

    private  void build$newThat(
        MappingForStringer builder,
        String fromClassname,
        String toClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {
    }

    private  void build$toThat(
        MappingForStringer builder,
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
                thatModel.getSetterTypename();
                thisModel.getGetter();
                builder.add("to.").add(thatModel.getSetterName()).add("(from.");
                builder.add(thisModel.getGetterName()).add("());");
            }
        }
        returnThatObject(builder);
    }

    private  void build$fromThat(
        MappingForStringer builder,
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
            if (isUsable(thatModel, thisModel)) {
                builder.add("from.").add(thisModel.getSetterName()).add("(to.");
                builder.add(thatModel.getGetterName()).add("());");
            }
        }
        returnThisObject(builder);
    }

    private  boolean isUsable(PropertyModel thisModel, PropertyModel thatModel) {
        boolean hasThis = thisModel != null && thisModel.hasPublicGetterMethod();
        boolean hasThat = thatModel != null && thatModel.hasPublicSetterMethod();
        return hasThat && hasThis;
    }

    private  void buildBeanMapping(
        MappingForStringer builder,
        String fromClassname,
        String thatClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {
        builder.add(formatClassname(thatClassname)).add(" {");
        build$toThat(builder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        build$fromThat(builder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        build$newThat(builder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        build$newThis(builder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        builder.add("},");
    }

    public String getGeneratedMappingName() {
        return "MoonBeanMappingsGenerated_" + formatClassname(getThisClassname());
    }

    @Override
    public String toString() {
        final MappingForStringer builder = new MappingForStringer();
        final String thisClassname = getThisClassname();
        Map<String, PropertyModel> thisModelMap = getPropertiesModelMap();
        builder.add("package ").add(PACKAGE).add(';');
        builder.add("enum ").add(getGeneratedMappingName()).add(" implements ").add(INTERFACE).add(" {");
        for (Map.Entry<String, Map<String, PropertyModel>> entry : getTargetModelsMap().entrySet()) {
            buildBeanMapping(builder, thisClassname, entry.getKey(), thisModelMap, entry.getValue());
        }
        builder.add(";");
        buildObjectMapping(builder, thisClassname, thisModelMap);
        buildMapMapping(builder, thisClassname, thisModelMap);
        builder.add("}");
        return builder.toString();
    }
}
