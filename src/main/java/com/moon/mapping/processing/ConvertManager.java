package com.moon.mapping.processing;

import com.moon.mapping.convert.Convert;
import com.moon.mapping.convert.JodaConvert;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
final class ConvertManager {

    private final Map<String, CallerInfo> converter = new HashMap<>();

    private final ImportManager importManager;

    public ConvertManager(ImportManager importManager) {
        this.importManager = importManager;
        loadPredefinedConvert(Convert.class);
        if (Imported.JODA_TIME) {
            loadPredefinedConvert(JodaConvert.class);
        }
    }

    private void loadPredefinedConvert(Class<?> type) {
        String unsafeConvertName = importManager.onImported(type);
        TypeElement unsafeConvert = EnvUtils.getUtils().getTypeElement(type.getCanonicalName());
        for (Element element : unsafeConvert.getEnclosedElements()) {
            if (DetectUtils.isMethod(element)) {
                ExecutableElement convert = (ExecutableElement) element;
                List<String> params = convert.getParameters()
                    .stream()
                    .map(var -> var.asType().toString().replaceAll("[^\\w\\d.]", ""))
                    .collect(Collectors.toList());
                String returnType = convert.getReturnType().toString();
                String key = toTypedKey(returnType, params);
                CallerInfo converter = toCallConvert(convert, unsafeConvertName, params);
                this.converter.put(key, converter);
            }
        }
    }

    private static CallerInfo toCallConvert(ExecutableElement convert, String unsafeConvertName, List<String> params) {
        String simpleName = ElementUtils.getSimpleName(convert);
        String caller = StringUtils.format(false, "{}.{}", unsafeConvertName, simpleName);
        List<String> vars = new ArrayList<>();
        vars.add("{var}");
        for (int i = 1; i < params.size(); i++) {
            vars.add("{var" + (i - 1) + "}");
        }
        return new CallerInfo(caller, "(" + String.join(", ", vars) + ")", String.join(",", params));
    }

    public CallerInfo find(Class<?> setterType, Class<?>... getterTypes) {
        return find(getName(setterType), getterTypes);
    }

    public CallerInfo find(String setterType, Class<?>... getterTypes) {
        return find(setterType, classnames(getterTypes));
    }

    public CallerInfo find(String setterType, String... getterTypes) {
        return converter.get(toTypedKey(setterType, getterTypes));
    }

    public String convertOrWarnedThenNull(String defaultVar, String setterType, String... getterTypes) {
        return convertOrWarnedThenNull(defaultVar, CallerInfo::toString, setterType, getterTypes);
    }

    // public String ifPresentOrNull(String defaultVar, BiFunction<CallerInfo, String, String> mapping, Class<?> setterType, Class<?>... getterTypes) {
    //     return ifPresentOrNull(defaultVar, mapping, getName(setterType), getterTypes);
    // }
    //
    // public String ifPresentOrNull(String defaultVar, BiFunction<CallerInfo, String, String> mapping, String setterType, Class<?>... getterTypes) {
    //     return ifPresentOrNull(defaultVar, mapping, setterType, classnames(getterTypes));
    // }

    public String convertOrWarnedThenNull(String defaultVar, BiFunction<CallerInfo, String, String> mapping, String setterType, String... getterTypes) {
        CallerInfo info = find(setterType, getterTypes);
        return info == null ? null : mapping.apply(info, defaultVar);
    }

    /*
     * 进入这里的要求确保 getter 类型不是基本数据类型
     */

    public String onSameType() {
        return "{toName}.{setterName}({fromName}.{getterName}());";
    }

    public String onSameType(String defaultVal, String getterType) {
        if (isNullString(defaultVal)) {
            return onSameType();
        }
        String t0 = "{getterType} {var} = {fromName}.{getterName}();" +//
            "{toName}.{setterName}({var} == null ? {value} : {var});";
        t0 = Replacer.getterType.replace(t0, getterType);
        return Replacer.value.replace(t0, defaultVal);
    }

    public String onMapping(String defaultVal, String mapping, String setterType, Class<?>... getterTypes) {
        return useMapping(defaultVal, () -> mapping, setterType, getterTypes);
    }

    public String onMapping(String defaultVal, String mapping, String setterType, String... getterTypes) {
        return useMapping(defaultVal, () -> mapping, setterType, getterTypes);
    }

    public String useMapping(
        String defaultVal, Supplier<String> mapper, String setterType, Class<?>... getterTypes
    ) {
        String[] types = Arrays.stream(getterTypes).map(Class::getCanonicalName).toArray(String[]::new);
        return useMapping(defaultVal, mapper, setterType, types);
    }

    public String useMapping(
        String defaultVal, Supplier<String> mapper, String setterType, String... getterTypes
    ) {
        String t0;
        String getterType = getterTypes[0];
        if (StringUtils.isPrimitive(getterType)) {
            t0 = "{toName}.{setterName}({MAPPINGS});";
        } else if (isNullString(defaultVal)) {
            t0 = StringUtils.isPrimitive(setterType) ? o2p : o2o;
        } else {
            t0 = StringUtils.isPrimitive(setterType) ? default_o2p : default_o2o;
            t0 = Replacer.value.replace(t0, defaultVal);
        }
        t0 = Replacer.MAPPINGS.replace(t0, mapper.get());
        t0 = Replacer.setterType.replace(t0, importManager.onImported(setterType));
        t0 = Replacer.getterType.replace(t0, importManager.onImported(getterType));
        return t0;
    }

    public String onConvertSimple(String setterType, Class<?> getterType) {
        return useConvert(null, Object::toString, setterType, getterType);
    }

    public String onConvertSimple(String setterType, String getterType) {
        return useConvert(null, Object::toString, setterType, getterType);
    }

    public String useConvert(
        String defaultVal, Function<CallerInfo, String> mapper, String setterType, Class<?>... getterTypes
    ) {
        String[] types = Arrays.stream(getterTypes).map(Class::getCanonicalName).toArray(String[]::new);
        return useConvert(defaultVal, mapper, setterType, types);
    }

    public String useConvert(
        String defaultVal, Function<CallerInfo, String> mapper, String setterType, String... getterTypes
    ) {
        String t0;
        String getterType = getterTypes[0];
        if (StringUtils.isPrimitive(getterType)) {
            t0 = "{toName}.{setterName}({MAPPINGS});";
        } else if (isNullString(defaultVal)) {
            t0 = StringUtils.isPrimitive(setterType) ? o2p : o2o;
        } else {
            t0 = StringUtils.isPrimitive(setterType) ? default_o2p : default_o2o;
            t0 = Replacer.value.replace(t0, defaultVal);
        }
        CallerInfo callerInfo = converter.get(toTypedKey(setterType, getterTypes));
        if (callerInfo == null) {
            return null;
        }
        t0 = Replacer.MAPPINGS.replace(t0, mapper.apply(callerInfo));
        t0 = Replacer.setterType.replace(t0, importManager.onImported(setterType));
        t0 = Replacer.getterType.replace(t0, importManager.onImported(getterType));
        return t0;
    }

    @SuppressWarnings("all")
    private final static String o2p = "" +//
        "{getterType} {var} = {fromName}.{getterName}();" +//
        "if ({var} != null) { {toName}.{setterName}({MAPPINGS}); }";
    @SuppressWarnings("all")
    private final static String o2o = "" +//
        "{getterType} {var} = {fromName}.{getterName}();" +//
        "if ({var} == null) { {toName}.{setterName}(null); }" +//
        "else { {toName}.{setterName}({MAPPINGS}); }";
    @SuppressWarnings("all")
    private final static String default_o2p = "" +//
        "{getterType} {var} = {fromName}.{getterName}();" +//
        "if ({var} == null) { {toName}.{setterName}({value}); }" +//
        "else { {toName}.{setterName}({MAPPINGS}); }";
    @SuppressWarnings("all")
    private final static String default_o2o = "" +//
        "{getterType} {var} = {fromName}.{getterName}();" +//
        "if ({var} == null) { {toName}.{setterName}({value}); }" +//
        "else { {toName}.{setterName}({MAPPINGS}); }";

    private static String toTypedKey(String returnType, String... paramTypes) {
        return "(" + String.join(",", paramTypes) + ") -> " + returnType;
    }

    private static String toTypedKey(String returnType, Iterable<String> paramTypes) {
        return "(" + String.join(",", paramTypes) + ") -> " + returnType;
    }

    private static boolean isNullString(String value) {
        return value == null || "null".equals(value);
    }

    private static String[] classnames(Class<?>... classes) {
        return Arrays.stream(classes).map(ConvertManager::getName).toArray(String[]::new);
    }

    private static String getName(Class<?> cls) { return cls.getCanonicalName(); }
}
