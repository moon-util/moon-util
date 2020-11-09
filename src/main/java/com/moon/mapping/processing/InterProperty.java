package com.moon.mapping.processing;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.moon.mapping.processing.CollectUtils.simpleGroup;

/**
 * @author moonsky
 */
final class InterProperty {

    private final String name;
    private List<InterMethod> settersArr;
    private List<InterMethod> gettersArr;
    private List<Managed> fields;

    public InterProperty(String name) { this.name = name; }

    public String getName() { return name; }

    public List<InterMethod> getSettersArr() { return settersArr; }

    public List<InterMethod> getGettersArr() { return gettersArr; }

    public List<InterMethod> ensureSettersArr() {
        return settersArr == null ? (settersArr = new ArrayList<>()) : settersArr;
    }

    public List<InterMethod> ensureGettersArr() {
        return gettersArr == null ? (gettersArr = new ArrayList<>()) : gettersArr;
    }

    public List<Managed> getFields() { return fields; }

    public List<Managed> ensureManagedArr() { return fields == null ? (fields = new ArrayList<>()) : fields; }

    final void addSetter(InterMethod setter) { ensureSettersArr().add(setter); }

    final void addGetter(InterMethod setter) { ensureGettersArr().add(setter); }

    String toDeclareField() { return toDeclared(Managed::toDeclareField); }

    String toDeclareSetter() { return toDeclared(Managed::toDeclareSetter); }

    String toDeclareGetter() { return toDeclared(Managed::toDeclareGetter); }

    String toStringMethod(boolean onFirst) {
        return toDeclared(managed -> managed.toStringMethod(onFirst));
    }

    String toCallGetterArr() {
        return toDeclared(",", Managed::toCallGetterForHashCode);
    }

    String toEqualsString(String varName) {
        return toDeclared(" && ", managed -> managed.toEqualsString(varName));
    }

    private String toDeclared(Function<Managed, String> stringifier) {
        return toDeclared("", stringifier);
    }

    private String toDeclared(String delimiter, Function<Managed, String> stringifier) {
        return String.join(delimiter, CollectUtils.reduce(ensureManagedArr(), (declares, field) -> {
            declares.add(stringifier.apply(field));
            return declares;
        }, new ArrayList<>()));
    }

    void onParseCompleted(String name) {
        doParseCompleted(name);
    }

    protected final void doParseCompleted(String name) {
        Map<String, InterMethod> gettersMap = simpleGroup(ensureGettersArr(), InterMethod::getFactActualType);
        Map<String, InterMethod> settersMap = simpleGroup(ensureSettersArr(), InterMethod::getFactActualType);
        List<Managed> managedArr = new ArrayList<>();
        int index = 0;
        for (Map.Entry<String, InterMethod> entry : gettersMap.entrySet()) {
            InterMethod getter = entry.getValue();
            String declareType = getter.getDeclareType();
            String actualType = getter.getActualType();
            String factActualType = entry.getKey();
            InterMethod setter = settersMap.remove(factActualType);
            if (setter == null) {
                setter = new InterMethod(toSetterName(getter), declareType, actualType);
            }
            managedArr.add(new Managed(name, index++, factActualType, getter, setter));
        }
        for (Map.Entry<String, InterMethod> entry : settersMap.entrySet()) {
            InterMethod setter = entry.getValue();
            String declareType = setter.getDeclareType();
            String actualType = setter.getActualType();
            String factActualType = entry.getKey();
            InterMethod getter = new InterMethod(toGetterName(setter), declareType, actualType);
            managedArr.add(new Managed(name, index++, factActualType, getter, setter));
        }
        ensureManagedArr().addAll(managedArr);
    }

    private static String toSetterName(InterMethod getter) {
        String name = getter.getMethodName();
        return "set" + name.substring(name.startsWith("is") ? 2 : 3);
    }

    private static String toGetterName(InterMethod setter) {
        String name = setter.getMethodName().substring(3);
        TypeMirror type = setter.getElem().getParameters().get(0).asType();
        return (type.getKind() == TypeKind.BOOLEAN ? "is" : "get") + name;
    }

    private static boolean isPrimitive(String type) {
        switch (type) {
            case "byte":
            case "short":
            case "int":
            case "long":
            case "float":
            case "double":
            case "boolean":
            case "char":
                return true;
            default:
                return false;
        }
    }

    private static class Managed {

        private final String name;
        private final int index;
        private final String factActualType;
        private final InterMethod getter;
        private final InterMethod setter;

        private Managed(
            String name, int index, String factActualType, InterMethod getter, InterMethod setter
        ) {
            this.name = name;
            this.index = index;
            this.factActualType = factActualType;
            this.getter = getter;
            this.setter = setter;
        }

        String toDeclareField() {
            return ElementUtils.concat("private ", getFactActualType(), " ", getPropertyName(), ";");
        }

        public String toDeclareSetter() {
            MappingAdder adder = new MappingAdder();
            InterMethod setter = getSetter();
            if (setter.isOverride()) {
                adder.add("@Override ");
            }
            adder.add("public void ").add(setter.getMethodName()).add("(");
            adder.add(getFactActualType()).addSpace().add("var").add(") {");
            adder.add("this.").add(getPropertyName()).add("=var;}");
            return adder.toString();
        }

        public String toDeclareGetter() {
            if (getIndex() > 0) {
                return "";
            }
            MappingAdder adder = new MappingAdder();
            InterMethod getter = getGetter();
            if (getter.isOverride()) {
                adder.add("@Override ");
            }
            adder.add("public ").add(getFactActualType()).addSpace().add(getter.getMethodName()).add("(){");
            adder.add("return this.").add(getPropertyName()).add(";}");
            return adder.toString();
        }

        public String toStringMethod(boolean onFirst) {
            final MappingAdder adder = new MappingAdder();
            if (onFirst && getIndex() == 0) {
                adder.add("builder.append(\"").add(getPropertyName()).add("=\");");
            } else {
                adder.add("builder.append(\", ").add(getPropertyName()).add("=\");");
            }
            adder.add("builder.append(").add(toCallGetterForHashCode()).add(");");
            return adder.toString();
        }

        public String toCallGetterForHashCode() {
            if (getIndex() > 0) {
                return getPropertyName();
            } else {
                return getGetter().getMethodName() + "()";
            }
        }

        public String toEqualsString(String varName) {
            String callGetter = toCallGetterForHashCode();
            MappingAdder adder = new MappingAdder();
            if (isPrimitive(getFactActualType())) {
                adder.add(callGetter).add("==").add(varName).add(".").add(callGetter);
            } else {
                adder.add("java.util.Objects.equals(").add(callGetter).add(",").add(varName);
                adder.add(".").add(callGetter).add(")");
            }
            return adder.toString();
        }

        public String getPropertyName() {
            return getIndex() == 0 ? getName() : (getName() + getIndex());
        }

        public String getName() { return name; }

        public int getIndex() { return index; }

        public String getFactActualType() { return factActualType; }

        public InterMethod getGetter() { return getter; }

        public InterMethod getSetter() { return setter; }
    }
}
