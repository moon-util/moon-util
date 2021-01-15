package com.moon.processor.holder;

import com.moon.processor.utils.Element2;
import com.moon.processor.utils.String2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.Map;

/**
 * 类全名管理器
 *
 * @author benshaoye
 */
public class NameHolder {

    private final Map<String, String> registeredClasses = new HashMap<>();

    public NameHolder() { }

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
        return templateClassnameAs("Mapper", thisClass, thatClass, "%s.%sTo%sMapper");
    }

    public String getCopierClassname(TypeElement thisClass, TypeElement thatClass) {
        return templateClassnameAs("Copier", thisClass, thatClass, "%s.%sTo%sCopier");
    }

    private String templateClassnameAs(String impl, TypeElement thisClass, TypeElement thatClass, String template) {
        String thisName = Element2.getQualifiedName(thisClass);
        String thatName = Element2.getQualifiedName(thatClass);
        String key = String2.keyOf(impl, thisName, thatName);
        String mapperName = registeredClasses.get(key);
        if (mapperName != null) {
            return mapperName;
        }
        String packageName = Element2.getPackageName(thisClass);
        String thisSimple = Element2.getSimpleName(thisName);
        String thatSimple = Element2.getSimpleName(thatClass);
        mapperName = String.format(template, packageName, thisSimple, thatSimple);
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
