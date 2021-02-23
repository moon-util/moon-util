package com.moon.processing.file;

import com.moon.processing.decl.VarHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.moon.processing.file.FieldScope.getScopedFieldsMap;
import static java.util.Collections.unmodifiableSet;

/**
 * 类级别字段管理器
 *
 * @author benshaoye
 */
public class FieldsScoped extends BaseScoped<JavaField> {

    private final VarHelper vars = new VarHelper();
    private final MethodsScoped methodsScoped;
    private final String classname;
    private final Map<String, JavaField> fieldContentSymbolMapped = new HashMap<>();

    public FieldsScoped(String classname, MethodsScoped methodsScoped, boolean inInterface) {
        super(methodsScoped.getImporter(), inInterface);
        this.methodsScoped = methodsScoped;
        this.classname = classname;
    }

    public VarHelper vars() { return vars; }

    public String nextVar() { return vars.next(getMemberMap().keySet()); }

    public String nextConstVar() { return vars.nextConst(getMemberMap().keySet()); }

    public JavaField declareField(String fieldName, String typeTemplate, Object... types) {
        JavaField field = newUsingField(fieldName, typeTemplate, types);
        getMemberMap().put(fieldName, field);
        return field;
    }

    public JavaField useField(String fieldName, String typeTemplate, Object... types) {
        JavaField field = getMemberMap().get(fieldName);
        return field == null ? declareField(fieldName, typeTemplate, types) : field;
    }

    public JavaField useFinalField(Consumer<JavaFieldValue> valueBuilder, String typeTemplate, Object... types) {
        JavaField field = newUsingField(nextVar(), typeTemplate, types).valueOf(valueBuilder);
        return usingContentSymbolWith(field, false);
    }

    public JavaField useConstField(Consumer<JavaFieldValue> valueBuilder, String typeTemplate, Object... types) {
        JavaField field = newUsingField(nextConstVar(), typeTemplate, types).valueOf(valueBuilder);
        // 这里的 inInterface 只是为了控制符号，在实际应用中是“不太合规”的
        return usingContentSymbolWith(field, !inInterface());
    }

    private JavaField usingContentSymbolWith(JavaField field, boolean wasConst) {
        final String contentSymbol = field.getContentSymbol();
        String symbol = wasConst ? ("static>>" + contentSymbol) : contentSymbol;
        JavaField definedField = fieldContentSymbolMapped.get(symbol);
        if (definedField != null) {
            return definedField;
        } else {
            if (wasConst) {
                field.withConst();
            }
            getMemberMap().put(field.getFieldName(), field);
            fieldContentSymbolMapped.put(symbol, field);
            return field;
        }
    }

    private JavaField newUsingField(String fieldName, String typeTemplate, Object... types) {
        JavaField field = new JavaField(getImporter(), classname, methodsScoped, fieldName, typeTemplate, types);
        return field;
    }

    public Set<String> getDeclaredFieldsName() {
        return unmodifiableSet(getMemberMap().keySet());
    }

    public Map<FieldScope, Map<String, JavaField>> getGroupedFieldsMap() {
        Map<FieldScope, Map<String, JavaField>> groupedFieldsMap = new HashMap<>(2);
        Map<String, JavaField> fieldsMap = getMemberMap();
        for (JavaField field : fieldsMap.values()) {
            Map<String, JavaField> scopedFieldsMap = getScopedFieldsMap(field, groupedFieldsMap);
            scopedFieldsMap.put(field.getFieldName(), field);
        }
        return groupedFieldsMap;
    }
}