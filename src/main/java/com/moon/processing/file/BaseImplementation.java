package com.moon.processing.file;

import java.util.*;

/**
 * @author benshaoye
 */
abstract class BaseImplementation extends FileInterfaceImpl {

    private final List<Scripter> instanceBlock = new ArrayList<>();
    private final List<Scripter> staticBlock = new ArrayList<>();

    public BaseImplementation(String packageName, String simpleName) { super(packageName, simpleName); }

    public BaseImplementation addInstanceScript(CharSequence script) {
        instanceBlock.add(addr -> addr.newScript(script));
        return this;
    }

    public BaseImplementation addStaticScript(CharSequence script) {
        staticBlock.add(addr -> addr.newScript(script));
        return this;
    }

    @Override
    protected void afterMethodCreated(JavaMethod method) { method.withPublic(); }

    @Override
    protected void afterFieldCreated(JavaField field) { field.withPublic(); }

    public JavaField privateField(String name, Class<?> fieldClass) {
        return privateField(name, fieldClass.getCanonicalName());
    }

    public JavaField privateField(String name, String typeTemplate, Object... types) {
        JavaField field = publicField(name, typeTemplate, types);
        field.withPrivate();
        return field;
    }

    public JavaField privateFinalField(String name, Class<?> fieldClass) {
        return privateFinalField(name, fieldClass.getCanonicalName());
    }

    public JavaField privateFinalField(String name, String typeTemplate, Object... types) {
        JavaField field = privateField(name, typeTemplate, types);
        field.withFinal();
        return field;
    }

    public JavaField privateStaticField(String name, Class<?> fieldClass) {
        return privateStaticField(name, fieldClass.getCanonicalName());
    }

    public JavaField privateStaticField(String name, String typeTemplate, Object... types) {
        JavaField field = privateField(name, typeTemplate, types);
        field.withStatic();
        return field;
    }

    public JavaField privateConstField(String name, Class<?> fieldClass) {
        return privateConstField(name, fieldClass.getCanonicalName());
    }

    public JavaField privateConstField(String name, String typeTemplate, Object... types) {
        JavaField field = privateFinalField(name, typeTemplate, types);
        field.withStatic();
        return field;
    }

    protected final void appendFieldsAndBlock(JavaAddr addr) {
        Map<FieldScope, Map<String, JavaField>> groupedFieldsMap = getGroupedFieldsMap();

        Map<String, JavaField> staticFieldsMap = groupedFieldsMap
            .getOrDefault(FieldScope.STATIC, Collections.emptyMap());
        appendFields(addr, staticFieldsMap, true);
        appendBlock(addr, this.staticBlock, true);

        Map<String, JavaField> memberFieldsMap = groupedFieldsMap
            .getOrDefault(FieldScope.MEMBER, Collections.emptyMap());
        appendFields(addr, memberFieldsMap, false);
        appendBlock(addr, this.instanceBlock, false);
    }

    private static void appendFields(JavaAddr addr, Map<String, JavaField> fieldsMap, boolean wasStatic) {
        if (fieldsMap.isEmpty()) {
            return;
        }
        addr.next(2);
        List<LineScripter> blocks = new ArrayList<>();
        for (JavaField field : fieldsMap.values()) {
            field.appendTo(addr);
            if (field.getLineScripter() != null) {
                blocks.add(field.getLineScripter());
            }
        }

        // initialize: instance & static
        blocks.sort(Comparator.comparingInt(LineScripter::length));
        appendBlock(addr, blocks, wasStatic);
    }

    protected static void appendBlock(JavaAddr addr, List<? extends Scripter> scripterList, boolean wasStatic) {
        if (scripterList.isEmpty()) {
            return;
        }
        addr.blankAdd(wasStatic ? "static {" : "{").start();
        for (Scripter scripter : scripterList) {
            scripter.appendTo(addr);
        }
        addr.newEnd("}");
    }
}
