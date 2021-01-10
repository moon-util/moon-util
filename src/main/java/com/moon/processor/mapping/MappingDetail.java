package com.moon.processor.mapping;

import com.moon.mapper.convert.DefaultValue;
import com.moon.processor.manager.ConstManager;
import com.moon.processor.manager.Importable;
import com.moon.processor.utils.Holder;
import com.moon.processor.utils.HolderGroup;

/**
 * @author benshaoye
 */
public class MappingDetail implements MappingScripter<ConstManager> {

    private final static HolderGroup SET = Holder.of(Holder.toName, Holder.setter, Holder.cast);
    private final static HolderGroup GET = Holder.of(Holder.fromName, Holder.getter, Holder.cast);
    private final static HolderGroup TYPE_VALUE_VAR = Holder.of(Holder.type, Holder.value, Holder.var);

    private final String fromName;
    private final String toName;
    private final String getterName;
    private final String setterName;
    private final String getterType;
    private final String setterType;
    private final boolean setterGeneric;
    private final boolean getterGeneric;

    public MappingDetail(
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

    @Override
    public String[] getScriptsOnDefaultVal(ConstManager cm, MappingDefaultVal defaultVal) {
        String get = toGetValueScript(cm);
        String t = "{type}.ifNull({value}, {var})";
        get = TYPE_VALUE_VAR.on(t, cm.onImported(DefaultValue.class), get, defaultVal.getDefaultVar());
        String script = Holder.value.on(toSetValueScript(cm), get);
        return new String[]{script};
    }

    private String toSetValueScript(ConstManager cm) {
        String t0 = "{toName}.{setter}({cast}{value});";
        String cast = getterGeneric ? "" : (setterGeneric ? toCast(setterType, cm) : "");
        return SET.on(t0, getToName(), getSetterName(), cast);
    }

    private String toGetValueScript(Importable cm) {
        String t0 = "{cast}{fromName}.{getter}()";
        String cast = getterGeneric ? toCast(getterType, cm) : "";
        return GET.on(t0, getFromName(), getGetterName(), cast);
    }

    private String toCast(String type, Importable cm) {
        return Holder.cast.on("({cast}) ", cm.onImported(type));
    }

    public String getToName() { return toName; }

    public String getFromName() { return fromName; }

    public String getGetterName() { return getterName; }

    public String getSetterName() { return setterName; }

    @Override
    public String getGetterType() { return getterType; }

    @Override
    public String getSetterType() { return setterType; }

    @Override
    public String[] getScripts(ConstManager cm) {
        String get = toGetValueScript(cm);
        String set = toSetValueScript(cm);
        return new String[]{Holder.value.on(set, get)};
    }
}
