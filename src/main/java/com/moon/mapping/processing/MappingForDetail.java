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
import java.util.function.BiFunction;
import java.util.function.Function;

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
        EnvironmentUtils.newJavaFile(filer, getBeanMappingClassname(), writer -> {
            writer.println(implementation());
            writer.flush();
        });
    }

    private String getBeanMappingClassname() {
        return ElementUtils.getBeanMappingEnumName(getImplClassname());
    }

    private String implementation() {
        final MappingAdder adder = new MappingAdder();
        adder.add("package ").add(PACKAGE).add(';');
        adder.add("enum ").add(getBeanMappingClassname()).add(" implements ").add(INTERFACE).add("{TO,");
        final Map<String, DefinitionDetail> mappingForDetailsMap = getMappingForDetailsMap();
        for (Map.Entry<String, DefinitionDetail> entry : mappingForDetailsMap.entrySet()) {
            addBeanMapping(adder, entry.getValue());
        }
        adder.add(";");
        addObjectMapping(adder);
        addMapMapping(adder);
        adder.add("}");
        return adder.toString();
    }

    private void addBeanMapping(MappingAdder adder, DefinitionDetail thatDef) {
        String thatClassname = thatDef.getThisClassname();
        adder.add("TO_" + ElementUtils.format(thatClassname)).add(" {");
        build$safeWithThat(adder, thatDef);
        adder.add(getFactory().copyForwardMethod(getThisClassname(), thatClassname));
        adder.add(getFactory().copyBackwardMethod(getThisClassname(), thatClassname));
        adder.add(getFactory().newThatOnEmptyConstructor(getThisClassname(), thatClassname));
        adder.add(getFactory().newThisOnEmptyConstructor(getThisClassname(), thatClassname));
        adder.add("},");
    }

    private void build$safeWithThat(MappingAdder adder, DefinitionDetail thatDef) {
        final Set<Map.Entry<String, PropertyDetail>> entries = entrySet();
        final String thatClassname = thatDef.getThisClassname();
        final String thisClassname = getThisClassname();
        {
            List<String> fields = CollectUtils.reduce(entries, (props, thisEntry, idx) -> {
                final String name = thisEntry.getKey();
                PropertyDetail thisModel = thisEntry.getValue();
                PropertyDetail thatModel = thatDef.get(name);
                if (isUsable(thisModel, thatModel)) {
                    props.add(getFactory().copyForwardField(thisModel, thatModel));
                }
                return props;
            }, new ArrayList<>());
            adder.add(getFactory().safeCopyForwardMethod(thisClassname, thatClassname, fields));
        }
        {
            List<String> fields = CollectUtils.reduce(entries, (props, thisEntry, idx) -> {
                final String name = thisEntry.getKey();
                PropertyDetail thisModel = thisEntry.getValue();
                PropertyDetail thatModel = thatDef.get(name);
                if (isUsable(thisModel, thatModel)) {
                    props.add(getFactory().copyBackwardField(thatModel, thisModel));
                }
                return props;
            }, new ArrayList<>());
            adder.add(getFactory().safeCopyBackwardMethod(thisClassname, thatClassname, fields));
        }
    }

    private void addObjectMapping(MappingAdder adder) {
        {
            // toString(Object)
            List<String> fields = reducing((list, property) ->//
                getFactory().toStringField(property, list.isEmpty()));
            adder.add(getFactory().toStringMethod(getThisClassname(), fields));
        }
        {
            // copy(Object)
            List<String> fields = reducing(getFactory()::cloneField);
            adder.add(getFactory().cloneMethod(getThisClassname(), fields));
        }
    }

    private void addMapMapping(MappingAdder adder) {
        {
            // fromMap(Object,Map)
            List<String> fields = reducing(getFactory()::fromMapField);
            adder.add(getFactory().fromMapMethod(getThisClassname(), fields));
        }
        {
            // toMap(Object,Map)
            List<String> fields = reducing(getFactory()::toMapField);
            adder.add(getFactory().toMapMethod(getThisClassname(), fields));
        }
        {
            // newThis(Map)
            adder.add(getFactory().newThisAsMapMethod(getThisClassname()));
        }
    }

    private boolean isUsable(PropertyDetail thisModel, PropertyDetail thatModel) {
        boolean hasGetter = thisModel != null && thisModel.hasGetterMethod();
        boolean hasSetter = thatModel != null && thatModel.hasSetterMethod();
        return hasGetter && hasSetter;
    }

    private Set<String> getSortedKeys() {
        Set<String> sorts = new LinkedHashSet<>();
        sorts.add("id");
        return sorts;
    }

    private List<String> reducing(Function<PropertyDetail, String> serializer) {
        final List<String> fields = new ArrayList<>();
        final Set<String> sortKeys = getSortedKeys();
        for (String key : sortKeys) {
            PropertyDetail property = get(key);
            if (property != null) {
                String field = serializer.apply(property);
                if (isNotBlank(field)) {
                    fields.add(field);
                }
            }
        }
        return CollectUtils.reduce(entrySet(), (list, entry) -> {
            if (sortKeys.contains(entry.getKey())) {
                return list;
            }
            String field = serializer.apply(entry.getValue());
            if (isNotBlank(field)) { list.add(field); }
            return list;
        }, fields);
    }

    private List<String> reducing(BiFunction<List<String>, PropertyDetail, String> serializer) {
        final List<String> fields = new ArrayList<>();
        final Set<String> sortKeys = getSortedKeys();
        for (String key : sortKeys) {
            PropertyDetail property = get(key);
            if (property != null) {
                String field = serializer.apply(fields, property);
                if (isNotBlank(field)) {
                    fields.add(field);
                }
            }
        }
        return CollectUtils.reduce(entrySet(), (list, entry) -> {
            if (sortKeys.contains(entry.getKey())) {
                return list;
            }
            String field = serializer.apply(list, entry.getValue());
            if (isNotBlank(field)) { list.add(field); }
            return list;
        }, new ArrayList<>());
    }

    private static boolean isNotBlank(String str) {
        if (str == null) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
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
