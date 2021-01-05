package com.moon.processor.model;

import com.moon.processor.manager.ConstManager;
import com.moon.processor.utils.Holder;
import com.moon.processor.utils.HolderGroup;
import com.moon.processor.utils.Log2;
import com.moon.processor.utils.Test2;

import java.util.Map;

/**
 * @author benshaoye
 */
public class DefMapping {

    private final static String[] EMPTY = {};

    private final ConvertType convertType;
    private final ConstManager constManager;
    private final DeclareProperty fromProp;
    private final DeclareProperty toProp;
    private final DeclareMapping mapping;
    private final String fromName;
    private final String toName;

    private DefMapping(
        ConvertType convertType,
        ConstManager constManager,
        DeclareProperty fromProp,
        DeclareProperty toProp,
        DeclareMapping mapping,
        String fromName,
        String toName
    ) {
        this.convertType = convertType;
        this.constManager = constManager;
        this.fromProp = fromProp;
        this.toProp = toProp;
        this.mapping = mapping;
        this.fromName = fromName;
        this.toName = toName;
    }

    public static DefMapping forward(
        ConvertType convertType,
        ConstManager constManager,
        DeclareProperty thisProp,
        DeclareProperty thatProp,
        DeclareMapping mapping
    ) {
        return new DefMapping(convertType, constManager, thisProp, thatProp, mapping, "self", "that");
    }

    public static DefMapping backward(
        ConvertType convertType,
        ConstManager constManager,
        DeclareProperty thisProp,
        DeclareProperty thatProp,
        DeclareMapping mapping
    ) {
        return new DefMapping(convertType, constManager, thatProp, thisProp, mapping, "that", "self");
    }

    public static DefMapping returning(String script) { return new Returning(script); }

    public DeclareProperty getFromProp() { return fromProp; }

    public DeclareProperty getToProp() { return toProp; }

    public DeclareMapping getMapping() { return mapping; }

    public String getFromName() { return fromName; }

    public String getToName() { return toName; }

    public ConvertType getConvertType() { return convertType; }

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

        // TODO 4. 注入方和输出方的格式化和默认值

        // 5. getter/setter 相似类型匹配
        return defaultMappingWithSetterMethod();
    }

    private String[] mappingWithConverters(
        Map<String, String> injectors,
        Map<String, String> providers,
        Map<String, DeclareMethod> settersMap,
        DeclareMethod getter
    ) {
        // 1. 注入方的 injector 和输出方的 provider 匹配
        Log2.warning("Injectors: {}", injectors);
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
                return onSimpleMapping(getter.getName(), getterActualType);
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

    private String[] defaultMappingWithSetterMethod() {
        DeclareProperty from = getFromProp();
        DeclareMethod getter = from.getGetter();
        if (getter != null) {
            String getterActualType = getter.getActualType();
            return defaultMappingWithSetterMethod(getterActualType, getter.getName());
        }
        return EMPTY;
    }

    private String[] defaultMappingWithSetterMethod(String getterType, String getterName) {
        Map<String, DeclareMethod> toSettersMethod = getToProp().getSetters();
        DeclareMethod setter = toSettersMethod.get(getterType);
        if (setter != null) {
            return onSimpleMapping(getterName, setter.getName());
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
            return onSimpleMapping(getterName, matchedSetter.getName());
        }
        return EMPTY;
    }

    private String[] onSimpleMapping(String getterName, String setterName) {
        return onSimpleMapping(getFromName(), getterName, getToName(), setterName);
    }

    private static String[] onSimpleMapping(String fromName, String getter, String toName, String setter) {
        String t0 = "{toName}.{setter}({fromName}.{getter}());";
        HolderGroup group = Holder.of(Holder.fromName, Holder.getter, Holder.toName, Holder.setter);
        String script = group.on(t0, fromName, getter, toName, setter);
        return new String[]{script};
    }

    private static final class Returning extends DefMapping {

        private final String[] scripts;

        public Returning(String script) {
            super(null, null, null, null, null, null, null);
            this.scripts = new String[]{"return " + script + ";"};
        }

        @Override
        public String[] getScripts() { return scripts; }
    }
}
