package com.moon.processor.model;

import com.moon.processor.manager.ConstManager;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Holder;
import com.moon.processor.utils.HolderGroup;
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
        switch (convertType) {
            case FORWARD:
                return getForwardScripts();
            case BACKWARD:
                return getBackwardScripts();
            default:
                return EMPTY;
        }
    }

    private String[] getForwardScripts() {
        DeclareProperty from = getFromProp(), to = getToProp();
        String toClass = Element2.getQualifiedName(to.getThisElement());
        Map<String, String> providers = from.findProvidersFor(toClass);
        Map<String, DeclareMethod> toSettersMethod = to.getSetters();
        String type = to.getFinalActualType();
        // 首先根据指定类型值查找是否有对应 setter
        if (type != null) {
            DeclareMethod setter = toSettersMethod.get(type);
            String provideMethod = providers.get(type);
            if (provideMethod != null && setter != null) {
                return onSimpleMapping(getFromName(), provideMethod, getToName(), setter.getName());
            }
        }
        // 然后根据遍历 setter，查找对应类型的转换器
        for (Map.Entry<String, DeclareMethod> entry : toSettersMethod.entrySet()) {
            String provideMethod = providers.get(entry.getKey());
            if (provideMethod == null) {
                continue;
            }
            return onSimpleMapping(getFromName(), provideMethod, getToName(), entry.getValue().getName());
        }
        // TODO 然后执行默认转换规则（这也是大多数情况）

        // 最后进行 getter/setter 相似类型匹配
        DeclareMethod getter = from.getGetter();
        if (getter != null) {
            String getterActualType = getter.getActualType();
            DeclareMethod setter = toSettersMethod.get(getterActualType);
            if (setter != null) {
                return onSimpleMapping(getFromName(), getter.getName(), getToName(), setter.getName());
            }
            String setterType = null;
            DeclareMethod matchedSetter = null;
            for (Map.Entry<String, DeclareMethod> entry : toSettersMethod.entrySet()) {
                DeclareMethod tempMethod = entry.getValue();
                String tempSetterType = tempMethod.getActualType();
                if (Test2.isSubtypeOf(getterActualType, tempSetterType)) {
                    if (setterType == null || Test2.isSubtypeOf(tempSetterType, setterType)) {
                        setterType = tempSetterType;
                        matchedSetter = tempMethod;
                    }
                }
            }
            if (matchedSetter != null) {
                return onSimpleMapping(getFromName(), getter.getName(), getToName(), matchedSetter.getName());
            }
        }
        return EMPTY;
    }

    private static String[] onSimpleMapping(String fromName, String getter, String toName, String setter) {
        String t0 = "{toName}.{setter}({fromName}.{getter}());";
        HolderGroup group = Holder.of(Holder.fromName, Holder.getter, Holder.toName, Holder.setter);
        String script = group.on(t0, fromName, getter, toName, setter);
        return new String[]{script};
    }

    private String[] getBackwardScripts() {
        DeclareProperty from = getFromProp(), to = getToProp();
        String fromClass = Element2.getQualifiedName(from.getThisElement());
        String toClass = Element2.getQualifiedName(to.getThisElement());
        Map<String, String> injectors = to.findProvidersFor(fromClass);

        return EMPTY;
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
