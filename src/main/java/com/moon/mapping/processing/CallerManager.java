package com.moon.mapping.processing;

import com.moon.mapping.convert.Convert;
import com.moon.mapping.convert.JodaConvert;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.moon.mapping.processing.StringUtils.getClassnamesOrEmpty;

/**
 * @author moonsky
 */
final class CallerManager {

    private final List<Map<String, CallerInfo>> convertsOfAll = new ArrayList<>();

    private final Map<String, CallerInfo> builtIn = new HashMap<>();

    private final ImportManager importManager;

    public CallerManager(ImportManager importManager, Class<?>... transfers) {
        this(importManager, null, getClassnamesOrEmpty(transfers));
    }

    public CallerManager(ImportManager importManager, String classname, String... transfers) {
        this.importManager = importManager;
        String[] names = getClassnamesOrEmpty(JodaConvert.class, Convert.class);
        for (String name : names) {
            loadConverter(builtIn, name);
        }
        loadConverter(classname);
        for (String transfer : transfers) {
            loadConverter(transfer);
        }
    }

    private void loadConverter(Map<String, CallerInfo> transfers, String classname) {
        load(transfers, importManager, classname);
    }

    public void loadConverter(String classname) {
        convertsOfAll.add(0, load(importManager, classname));
    }

    public CallerInfo find(Class<?> returnType, Class<?>... getterTypes) {
        return find(toTypedKey(returnType.getCanonicalName(), getterTypes));
    }

    public CallerInfo find(String returnType, Class<?>... getterTypes) {
        return find(toTypedKey(returnType, getterTypes));
    }

    public CallerInfo find(String returnType, String... getterTypes) {
        return find(toTypedKey(returnType, getterTypes));
    }

    public CallerInfo findInAll(Class<?> returnType, Class<?>... getterTypes) {
        return findInAll(toTypedKey(returnType.getCanonicalName(), getterTypes));
    }

    public CallerInfo findInAll(String returnType, Class<?>... getterTypes) {
        return findInAll(toTypedKey(returnType, getterTypes));
    }

    public CallerInfo findInAll(String returnType, String... getterTypes) {
        return findInAll(toTypedKey(returnType, getterTypes));
    }

    private CallerInfo findInAll(String key) {
        CallerInfo info = find(key);
        return info == null ? findBuiltIn(key) : info;
    }

    private CallerInfo findBuiltIn(String key) { return this.builtIn.get(key); }

    private CallerInfo find(String key) {
        for (Map<String, CallerInfo> transferInfoMap : this.convertsOfAll) {
            CallerInfo info = transferInfoMap.get(key);
            if (info != null) {
                return info;
            }
        }
        return null;
    }

    private static Map<String, CallerInfo> load(ImportManager importManager, String... classnames) {
        Map<String, CallerInfo> converts = new HashMap<>();
        for (String classname : classnames) {
            load(converts, importManager, classname);
        }
        return converts;
    }

    private static void load(Map<String, CallerInfo> converts, ImportManager importManager, String classname) {
        if (classname == null) {
            return;
        }
        TypeElement unsafeConvert = EnvUtils.getUtils().getTypeElement(classname);
        if (unsafeConvert == null) {
            return;
        }
        for (Element element : unsafeConvert.getEnclosedElements()) {
            if (DetectUtils.isMethod(element)) {
                ExecutableElement convert = (ExecutableElement) element;
                List<String> params = convert.getParameters()
                    .stream()
                    .map(var -> var.asType().toString().replaceAll("[^\\w\\d.]", ""))
                    .collect(Collectors.toList());
                String methodName = ElemUtils.getSimpleName(convert);
                String returnType = convert.getReturnType().toString();
                CallerInfo info = new CallerInfo(importManager, classname, methodName, params);
                converts.put(toTypedKey(returnType, params), info);
            }
        }
    }

    private static String toTypedKey(String returnType, Class<?>... paramTypes) {
        return toTypedKey(returnType, getClassnamesOrEmpty(paramTypes));
    }

    private static String toTypedKey(String returnType, String... paramTypes) {
        return "(" + String.join(",", paramTypes) + ") -> " + returnType;
    }

    private static String toTypedKey(String returnType, Iterable<String> paramTypes) {
        return "(" + String.join(",", paramTypes) + ") -> " + returnType;
    }
}
