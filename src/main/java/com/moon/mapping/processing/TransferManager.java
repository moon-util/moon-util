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
final class TransferManager {

    private final List<Map<String, TransferInfo>> convertsOfAll = new ArrayList<>();

    private final Map<String, TransferInfo> builtIn = new HashMap<>();

    private final ImportManager importManager;

    public TransferManager(ImportManager importManager, Class<?>... transfers) {
        this(importManager, null, getClassnamesOrEmpty(transfers));
    }

    public TransferManager(ImportManager importManager, String classname, String... transfers) {
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

    private void loadConverter(Map<String, TransferInfo> transfers, String classname) {
        load(transfers, importManager, classname);
    }

    public void loadConverter(String classname) {
        convertsOfAll.add(0, load(importManager, classname));
    }

    public TransferInfo findBuiltIn(Class<?> returnType, Class<?>... getterTypes) {
        return findBuiltIn(toTypedKey(returnType.getCanonicalName(), getterTypes));
    }

    public TransferInfo findBuiltIn(String returnType, Class<?>... getterTypes) {
        return findBuiltIn(toTypedKey(returnType, getterTypes));
    }

    public TransferInfo findBuiltIn(String returnType, String... getterTypes) {
        return findBuiltIn(toTypedKey(returnType, getterTypes));
    }

    public TransferInfo find(Class<?> returnType, Class<?>... getterTypes) {
        return find(toTypedKey(returnType.getCanonicalName(), getterTypes));
    }

    public TransferInfo find(String returnType, Class<?>... getterTypes) {
        return find(toTypedKey(returnType, getterTypes));
    }

    public TransferInfo find(String returnType, String... getterTypes) {
        return find(toTypedKey(returnType, getterTypes));
    }

    public TransferInfo findInAll(Class<?> returnType, Class<?>... getterTypes) {
        return findInAll(toTypedKey(returnType.getCanonicalName(), getterTypes));
    }

    public TransferInfo findInAll(String returnType, Class<?>... getterTypes) {
        return findInAll(toTypedKey(returnType, getterTypes));
    }

    public TransferInfo findInAll(String returnType, String... getterTypes) {
        return findInAll(toTypedKey(returnType, getterTypes));
    }

    private TransferInfo findInAll(String key) {
        TransferInfo info = find(key);
        return info == null ? findBuiltIn(key) : info;
    }

    private TransferInfo findBuiltIn(String key) { return this.builtIn.get(key); }

    private TransferInfo find(String key) {
        List<Map<String, TransferInfo>> list = this.convertsOfAll;
        for (Map<String, TransferInfo> transferInfoMap : list) {
            TransferInfo info = transferInfoMap.get(key);
            if (info != null) {
                return info;
            }
        }
        return null;
    }

    private static Map<String, TransferInfo> load(ImportManager importManager, String... classnames) {
        Map<String, TransferInfo> converts = new HashMap<>();
        for (int i = 0; i < classnames.length; i++) {
            load(converts, importManager, classnames[i]);
        }
        return converts;
    }

    private static void load(Map<String, TransferInfo> converts, ImportManager importManager, String classname) {
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
                String methodName = ElementUtils.getSimpleName(convert);
                String returnType = convert.getReturnType().toString();
                TransferInfo info = new TransferInfo(importManager, classname, methodName, params);
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
