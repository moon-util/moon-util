package com.moon.mapping.processing;

import com.moon.mapping.Convert;
import com.moon.mapping.JodaConvert;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author benshaoye
 */
final class ConvertManager {

    private final Map<String, String> converter = new HashMap<>();

    private final ImportManager importManager;

    public ConvertManager(ImportManager importManager) {
        this.importManager = importManager;
        loadPredefinedConvert(Convert.class);
        if (DetectUtils.IMPORTED_JODA_TIME) {
            loadPredefinedConvert(JodaConvert.class);
        }
    }

    private void loadPredefinedConvert(Class<?> type) {
        String unsafeConvertName = importManager.onImported(type);
        TypeElement unsafeConvert = EnvUtils.getUtils().getTypeElement(type.getCanonicalName());
        for (Element element : unsafeConvert.getEnclosedElements()) {
            if (DetectUtils.isMethod(element)) {
                ExecutableElement convert = (ExecutableElement) element;
                List<String> params = convert.getParameters().stream()
                    .map(var -> var.asType().toString().replaceAll("[^\\w\\d.]", "")).collect(Collectors.toList());
                String returnType = convert.getReturnType().toString();
                String key = toTypedKey(returnType, params);
                this.converter.put(key, toCallConvert(convert, unsafeConvertName));
            }
        }
    }

    private static String toCallConvert(ExecutableElement convert, String unsafeConvertName) {
        String simpleName = ElementUtils.getSimpleName(convert);
        return StringUtils.format(false, "{}.{}({var})", unsafeConvertName, simpleName);
    }

    /**
     * 进入这里的要求确保 getter 类型不是基本数据类型
     *
     * @param model
     *
     * @return
     */
    public String onRefType(final MappingModel model) {
        final String getterType = model.getGetterDeclareType();
        final String setterType = model.getSetterDeclareType();
        return onRefType(setterType, getterType);
    }

    public String onRefType(String setterType, Class<?> getterType) {
        return onRefType(setterType, getterType.getCanonicalName());
    }

    public String onRefType(String setterType, String getterType) {
        String convert = converter.get(toTypedKey(setterType, getterType));
        return convert == null ? null : onRefType(setterType, getterType, convert);
    }

    public String onRefType(String setterType, Class<?> getterType, String mappings) {
        return onRefType(setterType, getterType.getCanonicalName(), mappings);
    }

    public String onRefType(String setterType, String getterType, String mappings) {
        String t0 = StringUtils.isPrimitive(setterType) ? o2p : o2o;
        t0 = Replacer.getterType.replace(t0, importManager.onImported(getterType));
        t0 = Replacer.MAPPINGS.replace(t0, mappings);
        return Replacer.setterType.replace(t0, importManager.onImported(setterType));
    }

    private final static String p2p = "{toName}.{setterName}({MAPPINGS}({fromName}.{getterName}()));";
    private final static String p2o = "{toName}.{setterName}({MAPPINGS}({fromName}.{getterName}()));";

    private final static String o2p = "" +
        "{getterType} {var} = {fromName}.{getterName}();" +
        "if ({var} != null) { {toName}.{setterName}(MAPPINGS); }";
    private final static String o2o = "" +
        "{getterType} {var} = {fromName}.{getterName}();" +
        "if ({var} == null) { {toName}.{setterName}(null); }" +
        "else { {toName}.{setterName}({MAPPINGS}); }";

    private static String toTypedKey(String returnType, String... paramTypes) {
        return "(" + String.join(",", paramTypes) + ") -> " + returnType;
    }

    private static String toTypedKey(String returnType, Iterable<String> paramTypes) {
        return "(" + String.join(",", paramTypes) + ") -> " + returnType;
    }
}
