package com.moon.processor.model;

import com.moon.mapper.convert.DefaultValue;
import com.moon.processor.manager.ConstManager;
import com.moon.processor.utils.*;

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

    public static DefMapping scriptOf(String script) { return new Script(script); }

    public DeclareProperty getFromProp() { return fromProp; }

    public DeclareProperty getToProp() { return toProp; }

    public DeclareMapping getMapping() { return mapping; }

    public String getFromName() { return fromName; }

    public String getToName() { return toName; }

    public String[] getScripts() {
        DeclareProperty from = getFromProp(), to = getToProp();
        // 1. 注入方的 injector 和输出方的 provider 匹配
        // 2. 注入方的 injector 和输出方的 getter 匹配
        // 3. 注入方的 setter 重载和输出方的 provider 匹配
        @SuppressWarnings("all")//
        MappingDetail mapped = mappingWithConverters(//
            to.findInjectorsFor(from.getThisElement()),
            from.findProvidersFor(to.getThisElement()),
            to.getSetters(),
            from.getGetter());
        if (mapped != null) {
            return mapped.getScripts(constManager);
        }

        // 4. getter/setter 相似类型匹配(要求 getter 类型是 setter 类型的子类)
        mapped = defaultMappingWithSetterMethod();
        if (mapped != null) {
            return onMappingOnSimilar(from, to, mapped);
        }

        // TODO 5. 注入方和输出方的格式化、默认值和类型转换

        return EMPTY;
    }

    private String[] onMappingOnSimilar(DeclareProperty from, DeclareProperty to, MappingDetail detail) {
        String getterType = detail.getGetterType();
        // 返回数据是基本数据类型没有默认值
        if (Test2.isPrimitive(getterType)) {
            return detail.getScripts(constManager);
        }

        DeclareMapping pMapping = from.getForwardMapping(to.getThisElement());
        DeclareMapping iMapping = to.getBackwardMapping(from.getThisElement());

        // 非基本数据类型可能有默认值
        String iDefaultVal = iMapping.getDefaultValue();
        String pDefaultVal = pMapping.getDefaultValue();
        String var = defaultVarFor(constManager, detail.getSetterType(), iDefaultVal);
        if (var == null) {
            var = defaultVarFor(constManager, detail.getGetterType(), pDefaultVal);
        } else {
            return detail.getScriptsOnDefaultVal(var, constManager);
        }
        if (var == null) {
            return detail.getScripts(constManager);
        } else {
            return detail.getScriptsOnDefaultVal(var, constManager);
        }
    }

    private MappingDetail mappingWithConverters(
        Map<String, DeclareMethod> injectors,
        Map<String, DeclareMethod> providers,
        Map<String, DeclareMethod> settersMap,
        DeclareMethod getter
    ) {
        // 1. 注入方的 injector 和输出方的 provider 匹配
        for (Map.Entry<String, DeclareMethod> injectEntry : injectors.entrySet()) {
            DeclareMethod provideMethod = providers.get(injectEntry.getKey());
            DeclareMethod injectMethod = injectEntry.getValue();
            if (provideMethod != null) {
                String getterName = provideMethod.getName();
                String setterName = injectMethod.getName();
                return onSimpleMapping(getterName,
                    setterName,
                    provideMethod.isGenericDeclared(),
                    injectMethod.isGenericDeclared());
            }
        }
        // 2. 注入方的 injector 和输出方的 getter 匹配
        if (getter != null) {
            String getterActualType = getter.getActualType();
            DeclareMethod injectMethod = injectors.get(getterActualType);
            if (injectMethod != null && getter.getName() != null) {
                String setterName = injectMethod.getName();
                return onSimpleMapping(getter.getName(),
                    setterName,
                    getter.isGenericDeclared(),
                    injectMethod.isGenericDeclared());
            }
        }
        // 3. 注入方的 setter 重载和输出方的 provider 匹配
        for (Map.Entry<String, DeclareMethod> entry : settersMap.entrySet()) {
            DeclareMethod provideMethod = providers.get(entry.getKey());
            DeclareMethod setterMethod = entry.getValue();
            if (provideMethod == null) {
                continue;
            }
            String setterName = setterMethod.getName();
            String getterName = provideMethod.getName();
            return onSimpleMapping(getterName,
                setterName,
                provideMethod.isGenericDeclared(),
                setterMethod.isGenericDeclared());
        }
        return null;
    }

    private MappingDetail defaultMappingWithSetterMethod() {
        DeclareMethod getter = getFromProp().getGetter();
        if (getter != null) {
            String getterActualType = getter.getActualType();
            return defaultMappingWithSetterMethod(getterActualType, getter.getName(), getter.isGenericDeclared());
        }
        return null;
    }

    private MappingDetail defaultMappingWithSetterMethod(
        String getterType, String getterName, boolean getterGeneric
    ) {
        Map<String, DeclareMethod> toSettersMethod = getToProp().getSetters();
        DeclareMethod setter = toSettersMethod.get(getterType);
        if (setter != null) {
            return onMappingDetail(getterName,
                setter.getName(),
                getterType,
                setter.getActualType(),
                getterGeneric,
                setter.isGenericDeclared());
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
            return onMappingDetail(getterName,
                matchedSetter.getName(),
                getterType,
                matchedSetter.getActualType(),
                getterGeneric,
                matchedSetter.isGenericDeclared());
        }
        return null;
    }

    private MappingDetail onSimpleMapping(
        String getterName, String setterName, boolean getterGeneric, boolean setterGeneric
    ) {
        return new SimpleDetail(getFromName(), getterName, getToName(), setterName, getterGeneric, setterGeneric);
    }

    private MappingDetail onMappingDetail(
        String getterName,
        String setterName,
        String getterType,
        String setterType,
        boolean getterGeneric,
        boolean setterGeneric
    ) {
        return new MappingDetail(getFromName(),
            getterName,
            getToName(),
            setterName,
            getterType,
            setterType,
            getterGeneric,
            setterGeneric);
    }


    private final static HolderGroup SET = Holder.of(Holder.toName, Holder.setter, Holder.cast);
    private final static HolderGroup GET = Holder.of(Holder.fromName, Holder.getter, Holder.cast);
    private final static HolderGroup TYPE_VALUE_VAR = Holder.of(Holder.type, Holder.value, Holder.var);

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
            this.scripts = new String[]{"return " + script};
        }

        @Override
        public String[] getScripts() { return scripts; }
    }

    private static final class Script extends DefMapping {

        private final String[] scripts;

        public Script(String script) {
            super(null, null, null, null, null, null);
            this.scripts = new String[]{script};
        }

        @Override
        public String[] getScripts() { return scripts; }
    }

    private static class SimpleDetail extends MappingDetail {

        private SimpleDetail(
            String fromName,
            String toName,
            String getterName,
            String setterName,
            boolean getterGeneric,
            boolean setterGeneric
        ) {
            super(fromName, toName, getterName, setterName, null, null, getterGeneric, setterGeneric);
        }

        @Override
        public String[] getScriptsOnDefaultVal(String var, ConstManager cm) { return getScripts(cm); }

        @Override
        public String[] getScripts(ConstManager cm) { return super.getScripts(cm); }
    }

    private static class MappingDetail {

        private final String fromName;
        private final String toName;
        private final String getterName;
        private final String setterName;
        private final String getterType;
        private final String setterType;
        private final boolean setterGeneric;
        private final boolean getterGeneric;

        private MappingDetail(
            String fromName,
            String toName,
            String getterName,
            String setterName,
            String getterType,
            String setterType,
            boolean getterGeneric,
            boolean setterGeneric
        ) {
            this.toName = toName;
            this.fromName = fromName;
            this.getterName = getterName;
            this.setterName = setterName;
            this.getterType = getterType;
            this.setterType = setterType;
            this.getterGeneric = getterGeneric;
            this.setterGeneric = setterGeneric;
        }

        public String[] getScriptsOnDefaultVal(String var, ConstManager cm) {
            String get = toGetValueScript(cm);
            String t = "{type}.ifNull({value}, {var})";
            get = TYPE_VALUE_VAR.on(t, cm.onImported(DefaultValue.class), get, var);
            String script = Holder.value.on(toSetValueScript(cm), get);
            return new String[]{script};
        }

        private String toSetValueScript(ConstManager cm) {
            String t0 = "{toName}.{setter}({cast}{value});";
            String cast = getterGeneric ? "" : (setterGeneric ? toCast(setterType, cm) : "");
            return SET.on(t0, getToName(), getSetterName(), cast);
        }

        private String toGetValueScript(ConstManager cm) {
            String t0 = "{cast}{fromName}.{getter}()";
            String cast = getterGeneric ? toCast(getterType, cm) : "";
            return GET.on(t0, getFromName(), getGetterName(), cast);
        }

        private String toCast(String type, ConstManager cm) { return Holder.cast.on("({cast}) ", cm.onImported(type)); }

        public String getToName() { return toName; }

        public String getFromName() { return fromName; }

        public String getGetterName() { return getterName; }

        public String getSetterName() { return setterName; }

        public String getGetterType() { return getterType; }

        public String getSetterType() { return setterType; }

        public String[] getScripts(ConstManager cm) {
            String get = toGetValueScript(cm);
            String set = toSetValueScript(cm);
            return new String[]{Holder.value.on(set, get)};
        }
    }
}
