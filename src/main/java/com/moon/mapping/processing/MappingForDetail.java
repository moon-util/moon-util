package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.*;

/**
 * @author moonsky
 */
final class MappingForDetail extends DefinitionDetail {

    protected final static String PACKAGE = MappingUtil.class.getPackage().getName();
    protected final static String INTERFACE = BeanMapping.class.getName();

    private final BeanMappingFactory factory;
    private final Map<String, DefinitionDetail> mappingForDetailsMap;

    MappingForDetail(TypeElement thisElement, Map<String, DefinitionDetail> mappingForDetailsMap) {
        super(thisElement);
        this.factory = new BeanMappingFactory();
        this.mappingForDetailsMap = mappingForDetailsMap;
    }

    public final BeanMappingFactory getFactory() { return factory; }

    public Map<String, DefinitionDetail> getMappingForDetailsMap() { return mappingForDetailsMap; }

    @Override
    public void writeJavaFile(Filer filer) throws IOException {
        super.writeJavaFile(filer);
        JavaFileObject javaFile = filer.createSourceFile(getBeanMappingClassname());
        try (Writer jw = javaFile.openWriter(); PrintWriter writer = new PrintWriter(jw)) {
            writer.println(implementation());
            writer.flush();
        }
    }

    private String getBeanMappingClassname() {
        return ElementUtils.getBeanMappingEnumName(getImplClassname());
    }

    private String implementation() {
        final MappingAdder adder = new MappingAdder();
        final String thisClassname = getThisClassname();
        DefinitionDetail thisDetailMap = this;
        adder.add("package ").add(PACKAGE).add(';');
        adder.add("enum ").add(getBeanMappingClassname()).add(" implements ").add(INTERFACE).add("{TO,");
        final Map<String, DefinitionDetail> mappingForDetailsMap = getMappingForDetailsMap();
        for (Map.Entry<String, DefinitionDetail> entry : mappingForDetailsMap.entrySet()) {

            addBeanMapping(adder, thisClassname, entry.getKey(), thisDetailMap, entry.getValue());
        }
        adder.add(";");
        addObjectMapping(adder, thisClassname, thisDetailMap);
        addMapMapping(adder, thisClassname, thisDetailMap);
        adder.add("}");
        return adder.toString();
    }

    private void addBeanMapping(
        MappingAdder adder,
        String fromClassname,
        String thatClassname,
        DefinitionDetail thisModelMap,
        DefinitionDetail thatModelMap
    ) {
        adder.add("TO_" + ElementUtils.format(thatClassname)).add(" {");
        build$safeWithThat(adder, fromClassname, thatClassname, thisModelMap, thatModelMap);
        adder.add(getFactory().copyForwardMethod(fromClassname, thatClassname));
        adder.add(getFactory().copyBackwardMethod(fromClassname, thatClassname));
        adder.add(getFactory().newThatOnEmptyConstructor(fromClassname, thatClassname));
        adder.add(getFactory().newThisOnEmptyConstructor(fromClassname, thatClassname));
        adder.add("},");
    }

    private void build$safeWithThat(
        MappingAdder builder, String thisClassname, String thatClassname,//
        Map<String, PropertyDetail> thisModelMap,//
        Map<String, PropertyDetail> thatModelMap
    ) {
        Set<Map.Entry<String, PropertyDetail>> entries = thisModelMap.entrySet();
        {
            List<String> fields = CollectUtils.reduce(entries, (props, thisEntry, idx) -> {
                final String name = thisEntry.getKey();
                PropertyDetail thisModel = thisEntry.getValue();
                PropertyDetail thatModel = thatModelMap.get(name);
                if (isUsable(thisModel, thatModel)) {
                    props.add(getFactory().copyForwardField(thisModel, thatModel));
                }
                return props;
            }, new ArrayList<>());
            builder.add(getFactory().safeCopyForwardMethod(thisClassname, thatClassname, fields));
        }
        {
            List<String> fields = CollectUtils.reduce(entries, (props, thisEntry, idx) -> {
                final String name = thisEntry.getKey();
                PropertyDetail thisModel = thisEntry.getValue();
                PropertyDetail thatModel = thatModelMap.get(name);
                if (isUsable(thisModel, thatModel)) {
                    props.add(getFactory().copyBackwardField(thatModel, thisModel));
                }
                return props;
            }, new ArrayList<>());
            builder.add(getFactory().safeCopyBackwardMethod(thisClassname, thatClassname, fields));
        }
    }

    private void addObjectMapping(
        MappingAdder adder, String thisClassname, Map<String, PropertyDetail> thisModelMap
    ) {
        Set<Map.Entry<String, PropertyDetail>> entries = thisModelMap.entrySet();
        {
            // toString(Object)
            Set<String> sorted = getSortedKeys();
            List<String> fields = new ArrayList<>();
            for (String name : sorted) {
                PropertyDetail detail = thisModelMap.get(name);
                if (detail != null) {
                    fields.add(getFactory().toStringField(detail, fields.isEmpty()));
                }
            }
            CollectUtils.reduce(entries, (list, entry) -> {
                if (!sorted.contains(entry.getKey())) {
                    list.add(getFactory().toStringField(entry.getValue(), list.isEmpty()));
                }
                return list;
            }, fields);
            adder.add(getFactory().toStringMethod(thisClassname, fields));
        }
        {
            // copy(Object)
            List<String> fields = CollectUtils.reduce(entries, (props, entry, idx) -> {
                props.add(getFactory().cloneField(entry.getValue()));
                return props;
            }, new ArrayList<>());
            adder.add(getFactory().cloneMethod(thisClassname, fields));
        }
    }

    private void addMapMapping(
        MappingAdder adder, String thisClassname, Map<String, PropertyDetail> thisModelMap
    ) {
        Set<Map.Entry<String, PropertyDetail>> entries = thisModelMap.entrySet();
        {
            // fromMap(Object,Map)
            List<String> fields = CollectUtils.reduce(entries, (props, entry) -> {
                props.add(getFactory().fromMapField(entry.getValue()));
                return props;
            }, new ArrayList<>());
            adder.add(getFactory().fromMapMethod(thisClassname, fields));
        }
        {
            // toMap(Object,Map)
            List<String> fields = CollectUtils.reduce(entries, (props, entry) -> {
                props.add(getFactory().toMapField(entry.getValue()));
                return props;
            }, new ArrayList<>());
            adder.add(getFactory().toMapMethod(thisClassname, fields));
        }
        {
            // newThis(Map)
            adder.add(getFactory().newThisAsMapMethod(thisClassname));
        }
    }

    private boolean isUsable(PropertyDetail thisModel, PropertyDetail thatModel) {
        boolean hasGetter = thisModel != null && thisModel.hasGetterMethod();
        boolean hasSetter = thatModel != null && thatModel.hasSetterMethod();
        return hasGetter && hasSetter;
    }

    private Set<String> getSortedKeys() {
        Set<String> SORTER = new LinkedHashSet<>();
        SORTER.add("id");
        SORTER.add("pk");
        SORTER.add("primarykey");
        SORTER.add("uk");
        SORTER.add("unquekey");
        return SORTER;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MappingForDetail{");
        sb.append(super.toString());
        sb.append(", mappingForDetailsMap=").append(mappingForDetailsMap);
        sb.append('}');
        return sb.toString();
    }
}
