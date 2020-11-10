package com.moon.mapping.processing;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.Function;

import static com.moon.mapping.processing.CollectUtils.simpleGroup;

/**
 * @author moonsky
 */

final class InterProperty extends BaseProperty<InterMethod> implements Mappable {

    private Managed primaryManaged;
    private Map<String, Managed> fields;

    InterProperty(String name, TypeElement enclosingElement) { super(name, enclosingElement); }

    public Managed getPrimaryManaged() { return primaryManaged; }

    public void setPrimaryManaged(Managed primaryManaged) { this.primaryManaged = primaryManaged; }

    public Map<String, Managed> getFields() { return fields; }

    public Map<String, Managed> ensureFields() { return fields == null ? (fields = new LinkedHashMap<>()) : fields; }

    /*
    mapping 实现
     */

    @Override
    public boolean hasGetterMethod() { return getGetter() != null; }

    @Override
    public boolean hasSetterMethod() { return getSetter() != null; }

    @Override
    public String getGetterName() { return getGetter().getMethodName(); }

    @Override
    public String getSetterName() { return getSetter().getMethodName(); }

    @Override
    public String getGetterFinalType() { return getGetter().getComputedType(); }

    @Override
    public String getSetterFinalType() { return getSetter().getComputedType(); }

    @Override
    public boolean isPrimitiveGetter() { return StringUtils.isPrimitive(getGetterFinalType()); }

    @Override
    public boolean isPrimitiveSetter() { return StringUtils.isPrimitive(getSetterFinalType()); }

    /*
    接口实现
     */

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

    String toCloneMethod(String cloneName) {
        return toDeclared(managed -> managed.toCloneField(cloneName));
    }

    private String toDeclared(Function<Managed, String> stringifier) {
        return toDeclared("", stringifier);
    }

    private String toDeclared(String delimiter, Function<Managed, String> stringifier) {
        List<Managed> fields = new ArrayList<>();
        Managed primary = getPrimaryManaged();
        if (primary != null) {
            fields.add(primary);
        }
        fields.addAll(ensureFields().values());
        return String.join(delimiter, CollectUtils.reduce(fields, (declares, field) -> {
            declares.add(stringifier.apply(field));
            return declares;
        }, new ArrayList<>()));
    }

    @Override
    public void onCompleted() { doCompleted(); }

    protected final void doCompleted() {
        int index = 0;
        final String propertyName = getName();
        final Map<String, InterMethod> settersMap = simpleGroup(ensureSetterArr(), InterMethod::getComputedType);
        Iterator<InterMethod> getterItr = ensureGetterArr().iterator();
        InterMethod getterMethod = null, setterMethod = null;
        Managed primary = null;
        while (getterItr.hasNext()) {
            getterMethod = getterItr.next();
            String actualType = getterMethod.getActualType();
            String declareType = getterMethod.getDeclareType();
            String factActualType = getterMethod.getComputedType();
            setterMethod = settersMap.remove(getterMethod.getComputedType());
            if (setterMethod == null) {
                setterMethod = new InterMethod(toSetterName(getterMethod), declareType, actualType);
            }
            primary=new Managed(propertyName, index++, factActualType, getterMethod, setterMethod);
        }
        final List<Managed> managedArr = new ArrayList<>();
        for (Map.Entry<String, InterMethod> entry : settersMap.entrySet()) {
            InterMethod setter = entry.getValue();
            String declareType = setter.getDeclareType();
            String actualType = setter.getActualType();
            String factActualType = entry.getKey();
            InterMethod getter = new InterMethod(toGetterName(setter), declareType, actualType);
            managedArr.add(new Managed(propertyName, index++, factActualType, getter, setter));
        }
        if (getterMethod != null) {
            // 只要声明了 getter 就走这里
            this.setGetter(getterMethod);
            this.setSetter(setterMethod);
            this.setPrimaryManaged(primary);
        } else if (managedArr.size() == 1) {
            // 没有 getter 且只有一个 setter 走这里
            Managed managed = managedArr.get(0);
            this.setGetter(managed.getGetter());
            this.setSetter(managed.getSetter());
            this.setPrimaryManaged(managed);
            return;
        }
        // 没有 getter 且有多个 setter 时走这里
        Map<String, Managed> managedMap = ensureFields();
        for (Managed managed : managedArr) {
            managedMap.put(managed.getFactActualType(), managed);
        }
    }

    protected final void doParseCompleted() {
        final String name = getName();
        Map<String, InterMethod> gettersMap = simpleGroup(ensureGetterArr(), InterMethod::getComputedType);
        Map<String, InterMethod> settersMap = simpleGroup(ensureSetterArr(), InterMethod::getComputedType);
        final Map<String, Managed> propsMap = new LinkedHashMap<>();
        int index = 0;
        for (Map.Entry<String, InterMethod> entry : gettersMap.entrySet()) {
            // 同名 getter 最多只有一个，即这里面最多只会走一次
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
        ensureFields().clear();
        ensureFields().putAll(propsMap);
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
            adder.add(setter.isDeclaration(), "@Override ");
            adder.add("public void ").add(setter.getMethodName()).add("(");
            adder.add(getFactActualType()).space().add("var").add(") {");
            adder.add("this.").add(getFieldName()).add("=var;}");
            return adder.toString();
        }

        public String toDeclareGetter() {
            if (getIndex() > 0) {
                return "";
            }
            StringAdder adder = new StringAdder();
            InterMethod getter = getGetter();
            adder.add(getter.isDeclaration(), "@Override ");
            adder.add("public ").add(getFactActualType()).space().add(getter.getMethodName()).add("(){");
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
            if (StringUtils.isPrimitive(getFactActualType())) {
                adder.add(callGetter).add("==").add(varName).add(".").add(callGetter);
            } else {
                adder.add("java.util.Objects.equals(").add(callGetter).add(",").add(varName);
                adder.add(".").add(callGetter).add(")");
            }
            return adder.toString();
        }

        public String toCloneField(String cloneName) {
            String field = getFieldName();
            return new StringAdder().add(cloneName)
                .dot()
                .add(field)
                .add("=")
                .add("this.")
                .add(field)
                .add(";")
                .toString();
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
