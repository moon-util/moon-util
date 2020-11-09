package com.moon.mapping.processing;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.moon.mapping.processing.CollectUtils.simpleGroup;

/**
 * @author moonsky
 */
final class InterProperty extends BaseProperty<InterMethod> {

    private List<Managed> fields;

    InterProperty(String name, TypeElement enclosingElement) {
        super(name, enclosingElement);
    }

    public List<Managed> getFields() { return fields; }

    public List<Managed> ensureManagedArr() { return fields == null ? (fields = new ArrayList<>()) : fields; }

    String toDeclareField() { return toDeclared(Managed::toDeclareField); }

    String toDeclareSetter() { return toDeclared(Managed::toDeclareSetter); }

    String toDeclareGetter() { return toDeclared(Managed::toDeclareGetter); }

    String toStringMethod(boolean onFirst) {
        return toDeclared(managed -> managed.toStringMethod(onFirst));
    }

    String toCallGetterArr() {
        return toDeclared(",", Managed::toCallGetter);
    }

    String toEqualsString(String varName) {
        return toDeclared(" && ", managed -> managed.toEqualsString(varName));
    }

    String toCloneMethod() {
        return "";
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

    @Override
    public void onCompleted() { doParseCompleted(); }

    protected final void doParseCompleted() {
        final String name = getName();
        Map<String, InterMethod> gettersMap = simpleGroup(ensureGetterArr(), InterMethod::getFactActualType);
        Map<String, InterMethod> settersMap = simpleGroup(ensureSetterArr(), InterMethod::getFactActualType);
        final Map<String, Managed> propsMap = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<String, InterMethod> entry : gettersMap.entrySet()) {
            final InterMethod getter = entry.getValue();
            String declareType = getter.getDeclareType();
            String actualType = getter.getActualType();
            String factActualType = entry.getKey();
            InterMethod setter = settersMap.remove(factActualType);
            if (setter == null) {
                setter = new InterMethod(toSetterName(getter), declareType, actualType);
            }
            Managed managed = new Managed(name, index++, factActualType, getter, setter);
            propsMap.put(managed.getFieldName(), managed);
        }
        for (Map.Entry<String, InterMethod> entry : settersMap.entrySet()) {
            InterMethod setter = entry.getValue();
            String declareType = setter.getDeclareType();
            String actualType = setter.getActualType();
            String factActualType = entry.getKey();
            InterMethod getter = new InterMethod(toGetterName(setter), declareType, actualType);
            Managed managed = new Managed(name, index++, factActualType, getter, setter);
            propsMap.put(managed.getFieldName(), managed);
        }
        ensureManagedArr().addAll(propsMap.values());
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
            return ElementUtils.concat("private ", getFactActualType(), " ", getFieldName(), ";");
        }

        public String toDeclareSetter() {
            StringAdder adder = new StringAdder();
            InterMethod setter = getSetter();
            if (setter.isOverride()) {
                adder.add("@Override ");
            }
            adder.add("public void ").add(setter.getMethodName()).add("(");
            adder.add(getFactActualType()).addSpace().add("var").add(") {");
            adder.add("this.").add(getFieldName()).add("=var;}");
            return adder.toString();
        }

        public String toDeclareGetter() {
            if (getIndex() > 0) {
                return "";
            }
            StringAdder adder = new StringAdder();
            InterMethod getter = getGetter();
            if (getter.isOverride()) {
                adder.add("@Override ");
            }
            adder.add("public ").add(getFactActualType()).addSpace().add(getter.getMethodName()).add("(){");
            adder.add("return this.").add(getFieldName()).add(";}");
            return adder.toString();
        }

        public String toStringMethod(boolean onFirst) {
            final StringAdder adder = new StringAdder();
            if (onFirst && getIndex() == 0) {
                adder.add("builder.append(\"").add(getFieldName()).add("=\");");
            } else {
                adder.add("builder.append(\", ").add(getFieldName()).add("=\");");
            }
            adder.add("builder.append(").add(toCallGetter()).add(");");
            return adder.toString();
        }

        public String toCallGetter() {
            if (getIndex() > 0) {
                return getFieldName();
            } else {
                return getGetter().getMethodName() + "()";
            }
        }

        public String toEqualsString(String varName) {
            String callGetter = toCallGetter();
            StringAdder adder = new StringAdder();
            if (isPrimitive(getFactActualType())) {
                adder.add(callGetter).add("==").add(varName).add(".").add(callGetter);
            } else {
                adder.add("java.util.Objects.equals(").add(callGetter).add(",").add(varName);
                adder.add(".").add(callGetter).add(")");
            }
            return adder.toString();
        }

        /**
         * 实际字段名
         *
         * @return 字段名
         */
        public String getFieldName() {
            return getIndex() == 0 ? getName() : (getName() + getIndex());
        }

        /**
         * 声明的字段名（可能存在重载，所以不是最终字段名）
         *
         * @return 声明时字段名
         *
         * @see #getFieldName()
         */
        public String getName() { return name; }

        /**
         * 当前是第几个字段，存在重载的情况下，这里可能大于 0
         *
         * @return 当前是第几个字段
         */
        public int getIndex() { return index; }

        public String getFactActualType() { return factActualType; }

        public InterMethod getGetter() { return getter; }

        public InterMethod getSetter() { return setter; }
    }
}
