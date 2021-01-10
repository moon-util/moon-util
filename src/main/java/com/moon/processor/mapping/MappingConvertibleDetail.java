package com.moon.processor.mapping;

import com.moon.mapper.convert.DefaultValue;
import com.moon.processor.manager.ConstManager;
import com.moon.processor.manager.Importable;
import com.moon.processor.model.DeclareMapping;
import com.moon.processor.model.DeclareMethod;
import com.moon.processor.utils.Const2;
import com.moon.processor.utils.Holder;
import com.moon.processor.utils.HolderGroup;

/**
 * @author benshaoye
 */
public class MappingConvertibleDetail implements MappingScripter<ConstManager> {

    private final String fromName;
    private final String toName;
    private final DeclareMethod getter;
    private final DeclareMethod setter;
    private final DeclareMapping mapping;
    private final MappingType type;

    public MappingConvertibleDetail(
        String fromName,
        String toName,
        DeclareMethod getter,
        DeclareMethod setter,
        DeclareMapping mapping,
        MappingType type
    ) {
        this.fromName = fromName;
        this.toName = toName;
        this.type = type;
        this.getter = getter;
        this.setter = setter;
        this.mapping = mapping;
    }

    @Override
    public String[] getScriptsOnDefaultVal(ConstManager cm, MappingDefaultVal v) {
        return getScripts(cm);
    }

    @Override
    public String[] getScripts(ConstManager cm) {
        return Const2.EMPTY;
    }

    @Override
    public String getGetterType() { return getter.getActualType(); }

    @Override
    public String getSetterType() { return setter.getActualType(); }

    public DeclareMapping getMapping() { return mapping; }

    public String getFromName() { return fromName; }

    public MappingType getType() { return type; }

    public String getToName() { return toName; }

    interface Mapper {

        String[] doMapping(ConstManager cm, DeclareMapping mapping);
    }

    private final static HolderGroup GROUP;
    private final static HolderGroup TYPE0_NAME;

    static {
        GROUP = Holder.of(Holder.type, Holder.cast, Holder.fromName, Holder.getter);
        TYPE0_NAME = Holder.of(Holder.type0, Holder.name);
    }

    private enum ToString {
        /** 来源 */
        fromPrimitiveNumber {},
        fromPackagingNumber {},
        fromMathNumber {},
        fromUtilDate {},
        fromJdk8Date {},
        fromJodaDate {},
        fromObject {};
    }

    private enum ToPrimitiveNumber {

    }

    private static String getValueScript(DeclareMethod getter, ConstManager cm, String fromName, String defaultVar) {
        String type = getter.getActualType();
        boolean getterGeneric = getter.isGenericDeclared();
        String cast = getterGeneric ? toCast(type, cm) : "";
        String t0 = "{type} {var} = {type0}.ifNull({cast}{fromName}.{getter}(), {name});";
        t0 = GROUP.on(t0, type, cast, fromName, getter.getName());
        return TYPE0_NAME.on(t0, cm.onImported(DefaultValue.class), defaultVar);
    }

    private static String setValueScript(DeclareMethod setter, ConstManager cm, String fromName, String defaultVar) {
        String type = setter.getActualType();
        boolean getterGeneric = setter.isGenericDeclared();
        String cast = getterGeneric ? toCast(type, cm) : "";
        String t0 = "{toName}.{setter}({cast}{type0}.ifNull({var}, {name}));";
        t0 = GROUP.on(t0, type, cast, fromName, setter.getName());
        return TYPE0_NAME.on(t0, cm.onImported(DefaultValue.class), defaultVar);
    }

    private static String setValueScript(DeclareMethod setter, ConstManager cm, String fromName) {
        String type = setter.getActualType();
        boolean getterGeneric = setter.isGenericDeclared();
        String t0 = "{toName}.{setter}({cast}{var})";
        String cast = getterGeneric ? toCast(type, cm) : "";
        return GROUP.on(t0, type, cast, fromName, setter.getName());
    }

    private static String getValueScript(DeclareMethod getter, ConstManager cm, String fromName) {
        String type = getter.getActualType();
        boolean getterGeneric = getter.isGenericDeclared();
        String t0 = "{type} {var} = {cast}{fromName}.{getter}();";
        String cast = getterGeneric ? toCast(type, cm) : "";
        return GROUP.on(t0, type, cast, fromName, getter.getName());
    }

    private static String toCast(String type, Importable cm) {
        return Holder.type.on("({type}) ", cm.onImported(type));
    }
}
