package com.moon.mapping.processing;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.join;

/**
 * @author moonsky
 */
final class InterDefinition extends BaseDefinition<InterMethod, InterProperty> {

    public InterDefinition(TypeElement enclosingElement) { super(enclosingElement); }

    @Override
    public String getFactThisImplName() {
        return super.getFactThisImplName() + IMPL_SUFFIX;
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
        impl.add(toCloneMethod(classname));
        return join("", impl);
    }

    private String toCloneMethod(String classname) {
        StringAdder adder = new StringAdder();
        adder.add("@Override public ").add(classname).add(" clone() {");
        adder.add(classname).add(" clone = new ").add(classname).add("();");
        return adder.add(join("", reduceList((list, prop) -> {
            list.add(prop.toCloneMethod("clone"));
            return list;
        }))).add(" return clone;}").toString();
    }

    private String toStringMethod(String classname) {
        StringAdder adder = new StringAdder().add("@Override public String toString() {");
        adder.add("final StringBuilder builder = new StringBuilder().append(\"").add(classname);
        return adder.add("{\");").add(join("", reduceList((list, prop) -> {
            list.add(prop.toStringMethod(list.isEmpty()));
            return list;
        }))).add("return builder.append('}').toString();}").toString();
    }

    private String toEqualsAndHashCode(String classname) {
        StringAdder adder = new StringAdder();
        // hashCode
        adder.add("@Override public int hashCode(){ return java.util.Objects.hash(");
        adder.add(join(",", reduceList((list, prop) -> {
            list.add(prop.toCallGetterArr());
            return list;
        }))).add(");}");

        // equals
        adder.add("@Override public boolean equals(Object thatObject) {");
        adder.add("if (this == thatObject) { return true; }");
        adder.add("if (thatObject == null) { return false; }");
        adder.add("if (!(thatObject instanceof ").add(classname).add(")) { return false; }");
        adder.add(classname).space().add("that=").add("(").add(classname).add(")");
        adder.add("thatObject;return ");
        return adder.add(join(" && ", reduceList((list, prop) -> {
            list.add(prop.toEqualsString("that"));
            return list;
        }))).add(";}").toString();
    }
}
