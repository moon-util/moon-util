package com.moon.processor.model;

import com.moon.mapper.convert.DefaultValue;
import com.moon.processor.manager.ConstManager;
import com.moon.processor.utils.*;
import sun.rmi.runtime.Log;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * @author benshaoye
 */
public class DefMapping {

    private final static String[] EMPTY = {};

    private final ConstManager constManager;
    private final DeclareProperty fromProp;
    private final DeclareProperty toProp;
    private final DeclareMapping mapping;
    private final String fromName;
    private final String toName;

    private DefMapping(
        ConstManager constManager,
        DeclareProperty fromProp,
        DeclareProperty toProp,
        DeclareMapping mapping,
        String fromName,
        String toName
    ) {
        this.constManager = constManager;
        this.fromProp = fromProp;
        this.toProp = toProp;
        this.mapping = mapping;
        this.fromName = fromName;
        this.toName = toName;
    }

    public static DefMapping convert(
        ConstManager constManager, DeclareProperty thisProp, DeclareProperty thatProp, DeclareMapping mapping
    ) { return new DefMapping(constManager, thisProp, thatProp, mapping, "self", "that"); }

    public static DefMapping returning(String script) { return new Returning(script); }

    public DeclareProperty getFromProp() { return fromProp; }

    public DeclareProperty getToProp() { return toProp; }

    public DeclareMapping getMapping() { return mapping; }

    public String getFromName() { return fromName; }

    public String getToName() { return toName; }

    public ConstManager getConstManager() { return constManager; }

    public String[] getScripts() {
        DeclareProperty from = getFromProp(), to = getToProp();

        // 1. 注入方的 injector 和输出方的 provider 匹配
        // 2. 注入方的 injector 和输出方的 getter 匹配
        // 3. 注入方的 setter 重载和输出方的 provider 匹配
        @SuppressWarnings("all")//
        String[] scripts = mappingWithConverters(//
            to.findInjectorsFor(from.getThisElement()),
            from.findProvidersFor(to.getThisElement()),
            to.getSetters(),
            from.getGetter());
        if (scripts != null) {
            return scripts;
        }

        DeclareMapping pMapping = from.getForwardMapping(to.getThisElement());
        DeclareMapping iMapping = to.getBackwardMapping(from.getThisElement());

        Log2.warn("iMapping: {}.", iMapping);
        Log2.warn("pMapping: {}.", pMapping);

        // 4. getter/setter 相似类型匹配(要求 getter 类型是 setter 类型的子类)
        MappingDetail detail = defaultMappingWithSetterMethod();
        if (detail != null) {
            String getterType = detail.getGetterType();
            if (Test2.isPrimitive(getterType)) {
                return detail.getScripts();
            }
            String iDefaultVal = iMapping.getDefaultValue();
            String pDefaultVal = pMapping.getDefaultValue();
            String var = defaultVarFor(constManager, detail.getSetterType(), iDefaultVal);
            if (var == null) {
                var = defaultVarFor(constManager, detail.getGetterType(), pDefaultVal);
            } else {
                return detail.getScriptsOnDefaultVal(var, constManager);
            }
            if (var == null) {
                return detail.getScripts();
            } else {
                return detail.getScriptsOnDefaultVal(var, constManager);
            }
        }

        // TODO 5. 注入方和输出方的格式化、默认值和类型转换

        return EMPTY;
    }

    private String[] mappingWithConverters(
        Map<String, String> injectors,
        Map<String, String> providers,
        Map<String, DeclareMethod> settersMap,
        DeclareMethod getter
    ) {
        // 1. 注入方的 injector 和输出方的 provider 匹配
        for (Map.Entry<String, String> injectEntry : injectors.entrySet()) {
            String provideMethod = providers.get(injectEntry.getKey());
            if (provideMethod != null) {
                return onSimpleMapping(provideMethod, injectEntry.getValue());
            }
        }
        // 2. 注入方的 injector 和输出方的 getter 匹配
        if (getter != null) {
            String getterActualType = getter.getActualType();
            String setterName = injectors.get(getterActualType);
            if (setterName != null && getter.getName() != null) {
                return onSimpleMapping(getter.getName(), setterName);
            }
        }
        // 3. 注入方的 setter 重载和输出方的 provider 匹配
        for (Map.Entry<String, DeclareMethod> entry : settersMap.entrySet()) {
            String provideMethod = providers.get(entry.getKey());
            if (provideMethod == null) {
                continue;
            }
            return onSimpleMapping(provideMethod, entry.getValue().getName());
        }
        return null;
    }

    private MappingDetail defaultMappingWithSetterMethod() {
        DeclareMethod getter = getFromProp().getGetter();
        if (getter != null) {
            String getterActualType = getter.getActualType();
            return defaultMappingWithSetterMethod(getterActualType, getter.getName());
        }
        return null;
    }

    private MappingDetail defaultMappingWithSetterMethod(String getterType, String getterName) {
        Map<String, DeclareMethod> toSettersMethod = getToProp().getSetters();
        DeclareMethod setter = toSettersMethod.get(getterType);
        if (setter != null) {
            return onMappingDetail(getterName, setter.getName(), getterType, setter.getActualType());
        }
        String setterType = null;
        DeclareMethod matchedSetter = null;
        for (Map.Entry<String, DeclareMethod> entry : toSettersMethod.entrySet()) {
            DeclareMethod tempMethod = entry.getValue();
            String tempSetterType = tempMethod.getActualType();
            if (Test2.isSubtypeOf(getterType, tempSetterType)) {
                if (setterType == null || Test2.isSubtypeOf(tempSetterType, setterType)) {
                    setterType = tempSetterType;
                    matchedSetter = tempMethod;
                }
            }
        }
        if (matchedSetter != null) {
            return onMappingDetail(getterName, matchedSetter.getName(), getterType, matchedSetter.getActualType());
        }
        return null;
    }

    private String[] onSimpleMapping(String getterName, String setterName) {
        return onSimpleMapping(getFromName(), getterName, getToName(), setterName);
    }

    private static String[] onSimpleMapping(
        String fromName, String getterName, String toName, String setterName
    ) { return new String[]{mappingAs(fromName, getterName, toName, setterName)}; }

    private MappingDetail onMappingDetail(String getterName, String setterName, String getterType, String setterType) {
        return onMappingDetail(getFromName(), getterName, getToName(), setterName, getterType, setterType);
    }

    private static MappingDetail onMappingDetail(
        String fromName, String getterName, String toName, String setterName, String getterType, String setterType
    ) { return new MappingDetail(fromName, toName, getterName, setterName, getterType, setterType); }

    private final static HolderGroup GROUP = Holder.of(Holder.fromName, Holder.getter, Holder.toName, Holder.setter);
    private final static HolderGroup TYPE_VAR = Holder.of(Holder.type, Holder.var);

    private static String mappingAs(String fromName, String getter, String toName, String setter) {
        String t0 = "{toName}.{setter}({fromName}.{getter}());";
        return GROUP.on(t0, fromName, getter, toName, setter);
    }

    private static String defaultVarFor(ConstManager cm, String type, String value) {
        if (String2.isNotEmpty(value)) {
            if (Test2.isBasicNumberValue(type, value)) {
                return cm.numberOf(type, value);
            } else if (Test2.isBasicBooleanValue(type, value)) {
                return cm.booleanOf(type, value);
            } else if (Test2.isBasicCharValue(type, value)) {
                return cm.charOf(type, value);
            } else if (Test2.isTypeof(type, BigDecimal.class)) {
                return cm.bigDecimalOf(value);
            } else if (Test2.isTypeof(type, BigInteger.class)) {
                return cm.bigIntegerOf(value);
            } else if (Test2.isEnum(type)) {
                return cm.enumOf(type, value);
            } else if (Test2.isTypeof(type, String.class)) {
                return cm.stringOf(value);
            }
        }
        return null;
    }

    private static final class Returning extends DefMapping {

        private final String[] scripts;

        public Returning(String script) {
            super(null, null, null, null, null, null);
            String trimmed = script.trim();
            char last = trimmed.charAt(trimmed.length() - 1);
            this.scripts = new String[]{"return " + script + (last == ';' ? "" : ";")};
        }

        @Override
        public String[] getScripts() { return scripts; }
    }

    private static class MappingDetail {

        private final String fromName;
        private final String toName;
        private final String getterName;
        private final String setterName;
        private final String getterType;
        private final String setterType;

        private MappingDetail(
            String fromName, String toName, String getterName, String setterName, String getterType, String setterType
        ) {
            this.toName = toName;
            this.fromName = fromName;
            this.getterName = getterName;
            this.setterName = setterName;
            this.getterType = getterType;
            this.setterType = setterType;
        }

        public String[] getScriptsOnDefaultVal(String var, ConstManager cm) {
            String t0 = "{toName}.{setter}({type}.ifNull({fromName}.{getter}(), {var}));";
            t0 = GROUP.on(t0, getFromName(), getGetterName(), getToName(), getSetterName());
            String script = TYPE_VAR.on(t0, cm.onImported(DefaultValue.class), var);
            return new String[]{script};
        }

        public String getToName() { return toName; }

        public String getFromName() { return fromName; }

        public String getGetterName() { return getterName; }

        public String getSetterName() { return setterName; }

        public String getGetterType() { return getterType; }

        public String getSetterType() { return setterType; }

        public String[] getScripts() { return new String[]{toString()}; }

        @Override
        public String toString() { return mappingAs(getFromName(), getGetterName(), getToName(), getSetterName()); }
    }
}
