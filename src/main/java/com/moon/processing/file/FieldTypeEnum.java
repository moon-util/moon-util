package com.moon.processing.file;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author benshaoye
 */
public enum FieldTypeEnum {
    /** 字段范围: 静态、实例字段 */
    STATIC,
    MEMBER;

    public static Map<String, JavaField> getScopedFieldsMap(
        JavaField field, Map<FieldTypeEnum, Map<String, JavaField>> allFieldsMap
    ) { return (field.isStatic() ? STATIC : MEMBER).getScopedMap(allFieldsMap); }

    public Map<String, JavaField> getScopedMap(Map<FieldTypeEnum, Map<String, JavaField>> allFieldsMap) {
        return allFieldsMap.computeIfAbsent(this, k -> new LinkedHashMap<>());
    }
}