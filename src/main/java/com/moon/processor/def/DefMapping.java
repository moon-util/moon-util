package com.moon.processor.def;

import com.moon.processor.manager.ConstManager;
import com.moon.processor.mapping.*;
import com.moon.processor.model.DeclareMapping;
import com.moon.processor.model.DeclareMethod;
import com.moon.processor.model.DeclareProperty;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

import java.util.Map;

import static com.moon.processor.utils.Const2.EMPTY;

/**
 * @author benshaoye
 */
public class DefMapping {

    private final ConstManager constManager;
    private final DeclareProperty fromProp;
    private final DeclareProperty toProp;
    private final DeclareMapping mapping;
    private final MappingType type;
    private final String fromName;
    private final String toName;

    private DefMapping(
        ConstManager constManager,
        DeclareProperty fromProp,
        DeclareProperty toProp,
        DeclareMapping mapping,
        MappingType type,
        String fromName,
        String toName
    ) {
        this.constManager = constManager;
        this.fromProp = fromProp;
        this.type = type;
        this.toProp = toProp;
        this.mapping = mapping;
        this.fromName = fromName;
        this.toName = toName;
    }

    public static DefMapping convert(
        ConstManager constManager,
        DeclareProperty thisProp,
        DeclareProperty thatProp,
        DeclareMapping mapping,
        MappingType type
    ) { return new DefMapping(constManager, thisProp, thatProp, mapping, type, "self", "that"); }

    public static DefMapping returning(String script) { return new Returning(script); }

    public static DefMapping scriptOf(String script) { return new Script(script); }

    public DeclareProperty getFromProp() { return fromProp; }

    public DeclareProperty getToProp() { return toProp; }

    public DeclareMapping getMapping() { return mapping; }

    public String getFromName() { return fromName; }

    public String getToName() { return toName; }

    public String[] getScripts() {
        String[] scriptsForIgnored = mappingOnIgnored();
        if (scriptsForIgnored != null) {
            return scriptsForIgnored;
        }
        DeclareProperty from = getFromProp(), to = getToProp();

        // 1. 注入方的 injector 和输出方的 provider 匹配
        // 2. 注入方的 injector 和输出方的 getter 匹配
        // 3. 注入方的 setter 重载和输出方的 provider 匹配
        @SuppressWarnings("all")//
        MappingScripter<ConstManager> scripter = mappingWithConverters(//
            to.findInjectorsFor(from.getThisElement()),
            from.findProvidersFor(to.getThisElement()),
            to.getSetters(),
            from.getGetter());
        if (scripter != null) {
            return scripter.getScripts(constManager);
        }
        DeclareMethod getter = from.getGetter();
        DeclareMethod setter = to.getSetter();
        if (getter == null) {
            return setter == null ? EMPTY : nonMappingSetter(setter);
        }

        // 4. getter/setter 相似类型匹配(要求 getter 类型是 setter 类型的子类)
        // 这里只要存在 setter 类型与 getter 类型兼容的即可实现映射
        // 即 setter 方法重载默认就是转换器，但优先级低于主动声明的转换器
        scripter = defaultMappingWithSetterMethod(getter);
        if (scripter != null) {
            return onMappingOnSimilar(scripter);
        }

        // TODO 5. 注入方和输出方的格式化、默认值和类型转换
        // 上面已经考虑了主动声明转换器、setter 重载默认转换器
        // 故这里只考虑默认 getter/setter 直接对应的默认转换

        scripter = new MappingConvertibleDetail(getFromName(), getToName(), getter, setter, mapping, type);
        return scripter.getScripts(constManager);
    }

    /**
     * 数据格式化、默认值、类型转换
     *
     * @param getter
     *
     * @return
     */
    private Object mappingWithTolerant(DeclareMethod getter) {

        return null;
    }

    /**
     * 类型兼容的通过 getter/setter 映射，允许数据类型默认值
     *
     * @param detail
     *
     * @return
     */
    private String[] onMappingOnSimilar(MappingScripter<ConstManager> detail) {
        String getterType = detail.getGetterType();
        // 返回数据是基本数据类型没有默认值（因为基本数据类型不可能为 null）
        if (Test2.isPrimitive(getterType)) {
            return detail.getScripts(constManager);
        }

        // TODO 这一段暂时注释，不删除
        // DeclareMapping pMapping = from.getForwardMapping(to.getThisElement());
        // DeclareMapping iMapping = to.getBackwardMapping(from.getThisElement());
        //
        // // 非基本数据类型可能有默认值
        // String iDefaultVal = iMapping.getDefaultValue();
        // String pDefaultVal = pMapping.getDefaultValue();
        // String var = defaultVarFor(constManager, detail.getSetterType(), iDefaultVal);
        // if (var == null) {
        //     var = defaultVarFor(constManager, detail.getGetterType(), pDefaultVal);
        // } else {
        //     return detail.getScriptsOnDefaultVal(constManager, var);
        // }
        String defaultVal = mapping.getDefaultValue();
        @SuppressWarnings("all")
        String propType = type == MappingType.SETTER //
            ? detail.getSetterType() : detail.getGetterType();
        String var = constManager.defaultOf(propType, defaultVal);
        if (var == null) {
            return detail.getScripts(constManager);
        } else {
            MappingDefaultVal val = MappingDefaultVal.of(var, type);
            return detail.getScriptsOnDefaultVal(constManager, val);
        }
    }

    private String[] mappingOnIgnored() {
        DeclareProperty from = getFromProp(), to = getToProp();
        if (from == null && to == null) {
            return EMPTY;
        }
        if (from == null) {
            DeclareMethod setter = to.getSetter();
            return setter == null ? EMPTY : nonMappingSetter(setter);
        }
        if (to == null) {
            DeclareMethod getter = from.getGetter();
            return getter == null ? EMPTY : nonMappingGetter(getter);
        }
        return null;
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
                return onSimpleMapping(provideMethod, injectMethod);
            }
        }
        // 2. 注入方的 injector 和输出方的 getter 匹配
        if (getter != null) {
            String getterActualType = getter.getActualType();
            DeclareMethod injectMethod = injectors.get(getterActualType);
            if (injectMethod != null && getter.getName() != null) {
                return onSimpleMapping(getter, injectMethod);
            }
        }
        // 3. 注入方的 setter 重载和输出方的 provider 匹配
        for (Map.Entry<String, DeclareMethod> entry : settersMap.entrySet()) {
            DeclareMethod provideMethod = providers.get(entry.getKey());
            DeclareMethod setterMethod = entry.getValue();
            if (provideMethod == null) {
                continue;
            }
            return onSimpleMapping(provideMethod, setterMethod);
        }
        return null;
    }

    private MappingDetail defaultMappingWithSetterMethod(DeclareMethod getter) {
        String getterType = getter.getActualType();
        Map<String, DeclareMethod> toSettersMethod = getToProp().getSetters();
        DeclareMethod setter = toSettersMethod.get(getterType);
        if (setter != null) {
            return onMappingDetail(getter, setter);
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
            return onMappingDetail(getter, matchedSetter);
        }
        return null;
    }

    private String[] nonMappingGetter(DeclareMethod m) {
        String classname = constManager.onImported(m.getThisClassname());
        String comment = String2.format("忽略: {@link {}#{}()}", classname, m.getName());
        return new String[]{String2.onInlineDocCommentOf(comment)};
    }

    private String[] nonMappingSetter(DeclareMethod m) {
        String comment = "忽略: {@link {}#{}({})}";
        String classname = constManager.onImported(m.getThisClassname());
        String setterType = constManager.onImported(m.getActualType());
        comment = String2.format(comment, classname, m.getName(), setterType);
        return new String[]{String2.onInlineDocCommentOf(comment)};
    }

    private MappingDetail onSimpleMapping(DeclareMethod getter, DeclareMethod setter) {
        return new MappingSimplifyDetail(getFromName(),
            getToName(),
            getter.getName(),
            setter.getName(),
            getter.isGenericDeclared(),
            setter.isGenericDeclared());
    }

    private MappingDetail onMappingDetail(DeclareMethod getter, DeclareMethod setter) {
        return new MappingDetail(getFromName(),
            getToName(),
            getter.getName(),
            setter.getName(),
            getter.getActualType(),
            getter.getActualType(),
            getter.isGenericDeclared(),
            setter.isGenericDeclared());
    }

    private static final class Returning extends DefMapping {

        private final String[] scripts;

        public Returning(String script) {
            super(null, null, null, null, null, null, null);
            this.scripts = new String[]{"return " + script};
        }

        @Override
        public String[] getScripts() { return scripts; }
    }

    private static final class Script extends DefMapping {

        private final String[] scripts;

        public Script(String script) {
            super(null, null, null, null, null, null, null);
            this.scripts = new String[]{script};
        }

        @Override
        public String[] getScripts() { return scripts; }
    }
}
