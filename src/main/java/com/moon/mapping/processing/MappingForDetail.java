package com.moon.mapping.processing;

import com.moon.mapping.BeanMapping;
import com.moon.mapping.MappingUtil;

import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author moonsky
 */
final class MappingForDetail extends ClassDefinition {

    protected final static String PACKAGE = MappingUtil.class.getPackage().getName();
    protected final static String INTERFACE = BeanMapping.class.getName();

    private final MappingFactory factory;
    private final Map<String, ClassDefinition> mappingForDetailsMap;

    MappingForDetail(TypeElement thisElement, Map<String, ClassDefinition> mappingForDetailsMap) {
        super(thisElement);
        this.factory = new MappingFactory();
        this.mappingForDetailsMap = mappingForDetailsMap;
    }

    public final MappingFactory getFactory() { return factory; }

    public Map<String, ClassDefinition> getMappingForDetailsMap() { return mappingForDetailsMap; }

    @Override
    public void writeJavaFile() throws IOException {
        super.writeJavaFile();
        EnvironmentUtils.newJavaFile(getBeanMappingClassname(), () -> {
            try {
                return this.implementation();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private String getBeanMappingClassname() {
        return NamingUtils.getBeanMappingEnumName(getImplClassname());
    }

    private String implementation() throws IOException {
        final StringAdder adder = new StringAdder();
        adder.add("package ").add(PACKAGE).add(';');
        adder.add("enum ").add(getBeanMappingClassname()).add(" implements ").add(INTERFACE).add("{TO,");
        final Map<String, ClassDefinition> mappingForDetailsMap = getMappingForDetailsMap();
        for (Map.Entry<String, ClassDefinition> entry : mappingForDetailsMap.entrySet()) {
            ClassDefinition definition = entry.getValue();
            definition.writeJavaFile();
            addBeanMapping(adder, definition);
        }
        adder.add(";");
        addObjectMapping(adder);
        addMapMapping(adder);
        adder.add("}");
        return adder.toString();
    }

    private void addBeanMapping(StringAdder adder, ClassDefinition thatDef) {
        String thatClassname = thatDef.getThisClassname();
        adder.add("TO_" + NamingUtils.format(thatClassname)).add(" {");
        build$safeWithThat(adder, thatDef);
        adder.add(getFactory().copyForwardMethod(getThisClassname(), thatClassname));
        adder.add(getFactory().copyBackwardMethod(getThisClassname(), thatClassname));
        adder.add(getFactory().newThatOnEmptyConstructor(getThisClassname(), thatClassname));
        adder.add(getFactory().newThisOnEmptyConstructor(getThisClassname(), thatClassname));
        adder.add("},");
    }

    private void build$safeWithThat(StringAdder adder, ClassDefinition thatDef) {
        final String thatClassname = thatDef.getThisClassname();
        final String thisClassname = getThisClassname();
        {
            Collection<String> fields = reducing(thisProp -> {
                ClassProperty thatProp = thatDef.get(thisProp.getName());
                if (isUsable(thisProp, thatProp)) {
                    return getFactory().copyForwardField(thisProp, thatProp);
                }
                return null;
            });
            adder.add(getFactory().safeCopyForwardMethod(thisClassname, thatClassname, fields));
        }
        {
            Collection<String> fields = reducing(thisProp -> {
                ClassProperty thatProp = thatDef.get(thisProp.getName());
                if (isUsable(thatProp, thisProp)) {
                    return getFactory().copyBackwardField(thatProp, thisProp);
                }
                return null;
            });
            adder.add(getFactory().safeCopyBackwardMethod(thisClassname, thatClassname, fields));
        }
    }

    private void addObjectMapping(StringAdder adder) {
        {
            // toString(Object)
            if (isInterface()) {
                adder.add("@Override public String toString(Object that) {");
                adder.add("return that == null ? \"null\" : that.toString();");
                adder.add("}");
            } else {
                @SuppressWarnings("all")
                Collection<String> fields = reducing((list, property) ->//
                    getFactory().toStringField(property, list.isEmpty()));
                adder.add(getFactory().toStringMethod(getThisClassname(), fields));
            }
        }
        {
            // copy(Object)
            Collection<String> fields = reducing(getFactory()::cloneField);
            adder.add(getFactory().cloneMethod(getThisClassname(), fields));
        }
    }

    private void addMapMapping(StringAdder adder) {
        {
            // fromMap(Object,Map)
            Collection<String> fields = reducing(getFactory()::fromMapField);
            adder.add(getFactory().fromMapMethod(getThisClassname(), fields));
        }
        {
            // toMap(Object,Map)
            Collection<String> fields = reducing(getFactory()::toMapField);
            adder.add(getFactory().toMapMethod(getThisClassname(), fields));
        }
        {
            // newThis(Map)
            adder.add(getFactory().newThisAsMapMethod(getThisClassname()));
        }
    }

    private Set<String> getSortedKeys() {
        Set<String> sorts = new LinkedHashSet<>();
        sorts.add("id");
        return sorts;
    }

    private Collection<String> reducing(Function<ClassProperty, String> serializer) {
        return reducing((m, prop) -> serializer.apply(prop));
    }

    private Collection<String> reducing(BiFunction<Map<String, String>, ClassProperty, String> serializer) {
        final Map<String, String> parsedFields = new LinkedHashMap<>();
        final Set<String> sortKeys = getSortedKeys();
        for (String key : sortKeys) {
            ClassProperty property = get(key);
            if (property != null) {
                String field = serializer.apply(parsedFields, property);
                if (isNotBlank(field)) {
                    parsedFields.put(key, field);
                }
            }
        }
        return CollectUtils.reduce(entrySet(), (parsed, entry) -> {
            if (sortKeys.contains(entry.getKey())) {
                return parsed;
            }
            String field = serializer.apply(parsed, entry.getValue());
            if (isNotBlank(field)) {
                parsed.put(entry.getKey(), field);
            }
            return parsed;
        }, parsedFields).values();
    }

    private static boolean isUsable(ClassProperty from, ClassProperty to) {
        boolean hasGetter = from != null && from.hasGetterMethod();
        boolean hasSetter = to != null && to.hasSetterMethod();
        return hasGetter && hasSetter;
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
