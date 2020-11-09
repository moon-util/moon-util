package com.moon.mapping.processing;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author moonsky
 */
final class InterDefinition extends LinkedHashMap<String, InterProperty> {

    private final TypeElement interfaceElem;

    public InterDefinition(TypeElement interfaceElem) {
        this.interfaceElem = interfaceElem;
    }

    public TypeElement getInterfaceElem() { return interfaceElem; }

    final InterDefinition onParseCompleted() {
        forEach((key, prop) -> prop.onParseCompleted(key));
        return this;
    }

    final String implementation(String classname) {
        List<String> impl = new ArrayList<>();
        for (Map.Entry<String, InterProperty> entry : entrySet()) {
            InterProperty prop = entry.getValue();
            impl.add(prop.toDeclareField());
            impl.add(prop.toDeclareGetter());
            impl.add(prop.toDeclareSetter());
        }
        impl.add(toEqualsAndHashCode(classname));
        impl.add(toStringMethod(classname));
        return String.join("", impl);
    }

    final String toStringMethod(String classname) {
        MappingAdder adder = new MappingAdder().add("@Override public String toString() {");
        List<String> scripts = CollectUtils.reduce(entrySet(), (list, entry) -> {
            list.add(entry.getValue().toStringMethod(list.isEmpty()));
            return list;
        }, new ArrayList<>());
        adder.add("final StringBuilder builder = new StringBuilder().append(\"").add(classname).add("{\");");
        adder.add(String.join("", scripts));
        return adder.add("return builder.append('}').toString();}").toString();
    }

    final String toEqualsAndHashCode(String classname) {
        MappingAdder adder = new MappingAdder();
        // hashCode
        adder.add("@Override public int hashCode(){ return java.util.Objects.hash(");
        adder.add(String.join(",", CollectUtils.reduce(entrySet(), (list, prop) -> {
            list.add(prop.getValue().toCallGetterArr());
            return list;
        }, new ArrayList()))).add(");}");

        // equals
        adder.add("@Override public boolean equals(Object thatObject) {");
        adder.add("if (this == thatObject) { return true; }");
        adder.add("if (thatObject == null) { return false; }");
        adder.add("if (!(thatObject instanceof ").add(classname).add(")) { return false; }");
        adder.add(classname).addSpace().add("that=").add("(").add(classname).add(")");
        adder.add("thatObject;return ");
        String equals = String.join(" && ", CollectUtils.reduce(entrySet(), (list, prop) -> {
            list.add(prop.getValue().toEqualsString("that"));
            return list;
        }, new ArrayList()));
        return adder.add(equals).add(";}").toString();
    }
}
