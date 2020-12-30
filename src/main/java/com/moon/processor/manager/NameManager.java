package com.moon.processor.manager;

import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * 类全名管理器
 *
 * @author benshaoye
 */
public class NameManager {

    private final Map<String, String> registeredClasses = new HashMap<>();

    public NameManager() { }

    /**
     * 获取接口/抽象类实现类类名
     *
     * @param interfaceClass 接口/抽象类
     *
     * @return 实现类类名
     */
    public String getImplClassname(TypeElement interfaceClass) {
        String thisName = Element2.getQualifiedName(interfaceClass);
        if (Test2.isAbstractClass(interfaceClass)) {
            String implName = registeredClasses.get(thisName);
            if (implName != null) {
                return implName;
            }
            String packageName = Element2.getPackageName(interfaceClass);
            String simpleName = Element2.getSimpleName(interfaceClass);
            // 没考虑泛型
            implName = packageName + '.' + simpleName + "Implementation";
            return ensureOnly(thisName, implName);
        }
        return thisName;
    }

    /**
     * 获取 Mapper 类名
     *
     * @param thisClass thisClass 源
     * @param thatClass thatClass 目标
     *
     * @return Mapper 类名
     */
    public String getMapperClassname(TypeElement thisClass, TypeElement thatClass) {
        String thisName = Element2.getQualifiedName(thisClass);
        String thatName = Element2.getQualifiedName(thatClass);
        String key = thisName + ":" + thatName;
        String mapperName = registeredClasses.get(key);
        if (mapperName != null) {
            return mapperName;
        }
        String packageName = Element2.getPackageName(thisClass);
        String thisSimple = Element2.getSimpleName(thisName);
        String thatSimple = Element2.getSimpleName(thatClass);
        mapperName = String.format("%s.%sTo%sMapper", packageName, thisSimple, thatSimple);
        return ensureOnly(key, mapperName);
    }

    private String ensureOnly(String key, String implName) {
        for (int i = 0; registeredClasses.containsValue(implName); i++) {
            implName += i;
        }
        registeredClasses.put(key, implName);
        return implName;
    }
}
