package com.moon.processor.file;

import com.moon.processor.holder.ConstManager;
import com.moon.processor.holder.Importable;
import com.moon.processor.mapping.*;
import com.moon.processor.model.DeclareMapping;
import com.moon.processor.model.DeclareMethod;
import com.moon.processor.model.DeclareProperty;
import com.moon.processor.utils.Collect2;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author benshaoye
 */
public class DeclMapping implements ScriptsProvider, ImporterAware {

    private final ConstManager constManager;
    private final DeclareProperty fromProp;
    private final DeclareProperty toProp;
    private final DeclareMapping mapping;
    private final MappingType type;
    private final String fromName;
    private final String toName;

    private DeclMapping(
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

    public static DeclMapping convert(
        ConstManager constManager,
        DeclareProperty thisProp,
        DeclareProperty thatProp,
        DeclareMapping mapping,
        MappingType type
    ) { return new DeclMapping(constManager, thisProp, thatProp, mapping, type, "self", "that"); }

    /* getters & setters */

    @Override
    public Importable getImportable() { return getConstManager(); }

    public ConstManager getConstManager() { return constManager; }

    public DeclareProperty getFromProp() { return fromProp; }

    public DeclareProperty getToProp() { return toProp; }

    public DeclareMapping getMapping() { return mapping; }

    public MappingType getType() { return type; }

    public String getFromName() { return fromName; }

    public String getToName() { return toName; }

    @Override
    public List<String> getScripts() {
        List<String> scriptsForIgnored = mappingOnIgnored();
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
            return scripter.getScripts(getConstManager());
        }
        DeclareMethod getter = from.getGetter();
        DeclareMethod setter = to.getSetter();
        if (getter == null) {
            return setter == null ? Collections.emptyList() : nonMappingSetter(setter);
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
        return scripter.getScripts(getConstManager());
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
    private List<String> onMappingOnSimilar(MappingScripter<ConstManager> detail) {
        String getterType = detail.getGetterType();
        // 返回数据是基本数据类型没有默认值（因为基本数据类型不可能为 null）
        if (Test2.isPrimitive(getterType)) {
            return detail.getScripts(getConstManager());
        }
        String defaultVal = mapping.getDefaultValue();
        @SuppressWarnings("all") String propType = type == MappingType.SETTER //
                                                   ? detail.getSetterType() : detail.getGetterType();
        String var = getConstManager().defaultOf(propType, defaultVal);
        if (var == null) {
            return detail.getScripts(getConstManager());
        } else {
            MappingDefaultVal val = MappingDefaultVal.of(var, type);
            return detail.getScriptsOnDefaultVal(getConstManager(), val);
        }
    }

    private List<String> mappingOnIgnored() {
        DeclareProperty from = getFromProp(), to = getToProp();
        if (from == null && to == null) {
            return Collections.emptyList();
        }
        if (from == null) {
            DeclareMethod setter = to.getSetter();
            return setter == null ? Collections.emptyList() : nonMappingSetter(setter);
        }
        if (to == null) {
            DeclareMethod getter = from.getGetter();
            return getter == null ? Collections.emptyList() : nonMappingGetter(getter);
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

    private List<String> nonMappingGetter(DeclareMethod m) {
        String classname = getConstManager().onImported(m.getThisClassname());
        String comment = String2.format("忽略: {@link {}#{}()}", classname, m.getName());
        return Collect2.list(String2.onInlineDocCommentOf(comment));
    }

    private List<String> nonMappingSetter(DeclareMethod m) {
        String comment = "忽略: {@link {}#{}({})}";
        String classname = getConstManager().onImported(m.getThisClassname());
        String setterType = getConstManager().onImported(m.getActualType());
        comment = String2.format(comment, classname, m.getName(), setterType);
        return Collect2.list(String2.onInlineDocCommentOf(comment));
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
}
