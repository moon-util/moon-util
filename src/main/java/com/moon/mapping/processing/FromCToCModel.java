package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author moonsky
 */
final class FromCToCModel extends AbstractMapModel {

    private final static String PACKAGE = MappingUtil.class.getPackage().getName();
    private final static String INTERFACE = BeanMapping.class.getName();

    FromCToCModel(
        ProcessingEnvironment env,
        String thisClassname,
        Map<String, PropertyModel> propertiesModelMap,
        Map<String, MappedPropsMap> targetModelsMap
    ) {
        super(new Implementation(env), env, thisClassname, propertiesModelMap, targetModelsMap);
    }

    private static String format(String classname) { return classname.replace('.', '_'); }

    private void buildMapMapping(
        MappingAdder builder, String thisClassname, Map<String, PropertyModel> thisModelMap
    ) {
        Set<Map.Entry<String, PropertyModel>> entries = thisModelMap.entrySet();
        {
            // fromMap(Object,Map)
            List<String> fields = CollectUtils.reduce(entries, (props, entry) -> {
                props.add(getImpl().fromMapField(entry.getValue()));
                return props;
            }, new ArrayList<>());
            builder.add(getImpl().fromMapMethod(thisClassname, fields));
        }
        {
            // toMap(Object,Map)
            List<String> fields = CollectUtils.reduce(entries, (props, entry) -> {
                props.add(getImpl().toMapField(entry.getValue()));
                return props;
            }, new ArrayList<>());
            builder.add(getImpl().toMapMethod(thisClassname, fields));
        }
        {
            // newThis(Map)
            builder.add(getImpl().newThisAsMapMethod(thisClassname));
        }
    }

    private void buildObjectMapping(
        MappingAdder builder, String thisClassname, Map<String, PropertyModel> thisModelMap
    ) {
        Set<Map.Entry<String, PropertyModel>> entries = thisModelMap.entrySet();
        {
            // toString(Object)
            List<String> fields = CollectUtils.reduce(entries, (props, entry, idx) -> {
                props.add(getImpl().toStringField(entry.getValue(), idx == 0));
                return props;
            }, new ArrayList<>());
            builder.add(getImpl().toStringMethod(thisClassname, fields));
        }
        {
            // copy(Object)
            List<String> fields = CollectUtils.reduce(entries, (props, entry, idx) -> {
                props.add(getImpl().copyField(entry.getValue()));
                return props;
            }, new ArrayList<>());
            builder.add(getImpl().copyMethod(thisClassname, fields));
        }
    }


    private void build$safeWithThat(
        MappingAdder builder, String thisClassname, String thatClassname,//
        Map<String, PropertyModel> thisModelMap,//
        Map<String, PropertyModel> thatModelMap
    ) {
        Set<Map.Entry<String, PropertyModel>> entries = thisModelMap.entrySet();
        {
            List<String> fields = CollectUtils.reduce(entries, (props, thisEntry, idx) -> {
                final String name = thisEntry.getKey();
                PropertyModel thisModel = thisEntry.getValue();
                PropertyModel thatModel = thatModelMap.get(name);
                if (isUsable(thisModel, thatModel)) {
                    props.add(getImpl().toThatField(thisModel, thatModel));
                }
                return props;
            }, new ArrayList<>());
            builder.add(getImpl().safeToThatMethod(thisClassname, thatClassname, fields));
        }
        {
            List<String> fields = CollectUtils.reduce(entries, (props, thisEntry, idx) -> {
                final String name = thisEntry.getKey();
                PropertyModel thisModel = thisEntry.getValue();
                PropertyModel thatModel = thatModelMap.get(name);
                if (isUsable(thisModel, thatModel)) {
                    props.add(getImpl().fromThatField(thatModel, thisModel));
                }
                return props;
            }, new ArrayList<>());
            builder.add(getImpl().safeFromThatMethod(thisClassname, thatClassname, fields));
        }
    }

    private boolean isUsable(PropertyModel thisModel, PropertyModel thatModel) {
        boolean hasThis = thisModel != null && thisModel.hasPublicGetterMethod();
        boolean hasThat = thatModel != null && thatModel.hasPublicSetterMethod();
        return hasThat && hasThis;
    }

    private void buildBeanMapping(
        MappingAdder builder,
        String fromClassname,
        String thatClassname,
        Map<String, PropertyModel> thisModelMap,
        Map<String, PropertyModel> thatModelMap
    ) {
        builder.add("TO_" + format(thatClassname)).add(" {");
        build$safeWithThat(builder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        builder.add(getImpl().fromThatMethod(fromClassname, thatClassname));
        builder.add(getImpl().toThatMethod(fromClassname, thatClassname));
        builder.add(getImpl().newThatOnEmptyConstructor(fromClassname, thatClassname));
        builder.add(getImpl().newThisOnEmptyConstructor(fromClassname, thatClassname));
        builder.add("},");
    }

    public String getBeanMappingClassname() {
        return "BeanMapping_" + format(getThisClassname());
    }

    @Override
    protected void writeJavaFile(Filer filer) throws IOException {
        JavaFileObject java = filer.createSourceFile(getBeanMappingClassname());
        try (Writer jw = java.openWriter(); PrintWriter writer = new PrintWriter(jw)) {
            writer.println(implementation());
            writer.flush();
        }
    }

    private String implementation() {
        final MappingAdder builder = new MappingAdder();
        final String thisClassname = getThisClassname();
        Map<String, PropertyModel> thisModelMap = getPropertiesModelMap();
        builder.add("package ").add(PACKAGE).add(';');
        builder.add("enum ").add(getBeanMappingClassname());
        builder.add(";");
        buildObjectMapping(builder, thisClassname, thisModelMap);
        buildMapMapping(builder, thisClassname, thisModelMap);
        builder.add("}");
        return builder.toString();
    }

    @Override
    public String toString() {
        return implementation();
    }
}
