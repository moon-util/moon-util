package com.moon.processing.file;

import com.moon.processing.JavaDeclarable;
import com.moon.processor.holder.Importer;
import com.moon.processor.utils.Collect2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Collections.unmodifiableSet;

/**
 * 用于生成接口类
 *
 * @author benshaoye
 */
public class JavaInterfaceFile extends JavaBlockCommentable implements JavaDeclarable {

    private final String packageName;
    private final String simpleName;
    private final String classname;

    private final Set<String> interfacesSet = new LinkedHashSet<>();
    private final Set<JavaGeneric> generics = new LinkedHashSet<>();
    private final Map<String, JavaField> fieldsMap = new LinkedHashMap<>();
    private final Map<String, JavaMethod> methodsMap = new LinkedHashMap<>();

    public JavaInterfaceFile(String packageName, String simpleName) {
        super(new Importer(packageName));
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.classname = String.join(".", packageName, simpleName);
        annotationGenerated();
        docCommentOf(getClassname(), "", String2.format("@author {}", getClass().getCanonicalName()));
    }

    @Override
    public String getClassname() { return classname; }

    @Override
    public JavaInterfaceFile addModifierWith(Modifier modifier) {
        super.addModifierWith(modifier);
        return this;
    }

    public Set<String> getDeclaredFieldsName() {
        return unmodifiableSet(getFieldsMap().keySet());
    }

    protected boolean inInterface() { return true; }

    protected final Map<String, JavaMethod> getMethodsMap() { return methodsMap; }

    protected final Map<String, JavaField> getFieldsMap() { return fieldsMap; }

    protected Map<FieldScope, Map<String, JavaField>> getGroupedFieldsMap() {
        Map<FieldScope, Map<String, JavaField>> groupedFieldsMap = new HashMap<>();
        Map<String, JavaField> fieldsMap = getFieldsMap();
        for (JavaField field : fieldsMap.values()) {
            Map<String, JavaField> scopedFieldsMap = FieldScope.getScopedFieldsMap(field, groupedFieldsMap);
            scopedFieldsMap.put(field.getFieldName(), field);
        }
        return groupedFieldsMap;
    }

    protected enum FieldScope {
        /** 字段范围: 静态、实例字段 */
        STATIC,
        MEMBER;

        public static Map<String, JavaField> getScopedFieldsMap(
            JavaField field, Map<FieldScope, Map<String, JavaField>> allFieldsMap
        ) { return (field.isStatic() ? STATIC : MEMBER).getScopedMap(allFieldsMap); }

        public Map<String, JavaField> getScopedMap(Map<FieldScope, Map<String, JavaField>> allFieldsMap) {
            return allFieldsMap.computeIfAbsent(this, k -> new LinkedHashMap<>());
        }
    }

    protected void afterMethodCreated(JavaMethod method) { }

    protected void afterFieldCreated(JavaField field) { field.withStatic(); }

    public JavaField publicField(String name, Class<?> fieldClass) {
        return publicField(name, fieldClass.getCanonicalName());
    }

    public JavaField publicField(String name, String typeTemplate, Object... types) {
        JavaField field = new JavaField(getImporter(), inInterface(), getClassname(), name, typeTemplate, types);
        afterFieldCreated(field);
        getFieldsMap().put(field.getFieldName(), field);
        return field;
    }

    public JavaMethod publicMethod(String name) { return publicMethod(name, p -> {}); }

    public JavaMethod publicMethod(String name, Consumer<JavaParameters> parametersBuilder) {
        JavaParameters parameters = new JavaParameters(getImporter());
        parametersBuilder.accept(parameters);
        JavaMethod method = new JavaMethod(getImporter(), name, parameters, inInterface());
        afterMethodCreated(method);
        getMethodsMap().put(method.getUniqueKey(), method);
        return method;
    }

    /**
     * 实现接口
     *
     * @param interfaces
     *
     * @return
     */
    public JavaInterfaceFile implement(Class<?>... interfaces) {
        Collect2.addAll(Class::getCanonicalName, interfacesSet, interfaces);
        return this;
    }

    public JavaInterfaceFile implementOf(String interfaceTemplate, Object... types) {
        interfacesSet.add(Formatter.with(interfaceTemplate, types));
        return this;
    }

    /**
     * 实现接口
     *
     * @param interfaces
     *
     * @return
     */
    public JavaInterfaceFile implement(String... interfaces) {
        Collect2.addAll(this.interfacesSet, interfaces);
        return this;
    }

    /**
     * 简单泛型
     *
     * @param name
     *
     * @return
     */
    public JavaInterfaceFile addGenericWith(String name) {
        generics.add(new JavaGeneric(getImporter(), name));
        return this;
    }

    /**
     * 将要自定义泛型边界的泛型
     *
     * @param name
     * @param genericUsing
     *
     * @return
     */
    public JavaInterfaceFile addGenericWith(String name, Consumer<JavaGeneric> genericUsing) {
        JavaGeneric generic = new JavaGeneric(getImporter(), name);
        genericUsing.accept(generic);
        generics.add(generic);
        return this;
    }

    public final String getSimpleName() { return simpleName; }

    @Override
    public String getJavaContent() {
        final JavaAddr addr = newPackagedJavaAddr();
        JavaAddr.Mark importMark = addr.mark();

        super.appendTo(addr.next());
        addr.newAdd("public interface ").add(getSimpleName()).add(getGenericDeclared())
            .add(getInterfacesWillImplemented("extends")).add(" {").start();

        getFieldsMap().forEach((key, field) -> field.appendTo(addr));

        appendMethods(addr);

        return returning(addr, importMark);
    }

    protected final void appendMethods(JavaAddr addr) {
        getMethodsMap().forEach((key, method) -> method.appendTo(addr));
    }

    protected final JavaAddr newPackagedJavaAddr() {
        JavaAddr addr = new JavaAddr();

        String pkg = this.packageName;
        if (!String2.isBlank(pkg)) {
            addr.padAdd("package ").padScript(pkg).next(2);
        }
        return addr;
    }

    protected final String returning(JavaAddr addr, JavaAddr.Mark importMark) {
        addr.newEnd().padAdd("}");
        importMark.with(getImporter().toString("\n"));
        return addr.toString();
    }

    protected String getInterfacesWillImplemented(String keyword) {
        Set<String> interfaces = this.interfacesSet;
        if (interfaces.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(" ").append(keyword.trim()).append(" ");
        for (String anInterface : interfaces) {
            builder.append(onImported(anInterface));
        }
        return builder.toString();
    }

    /**
     * 接口的泛型声明
     *
     * @return 泛型声明
     */
    protected String getGenericDeclared() {
        Set<JavaGeneric> generics = this.generics;
        if (generics.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(", ", "<", ">");
        for (JavaGeneric generic : generics) {
            joiner.add(generic.toString());
        }
        return joiner.toString();
    }

    @Override
    public Set<Modifier> getModifiers() { return Modifier2.FOR_INTERFACE; }

    @Override
    public Set<Modifier> getAllowedModifiers() { return Modifier2.FOR_INTERFACE; }
}
