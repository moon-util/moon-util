package com.moon.processing.file;

import com.moon.processing.JavaDeclarable;
import com.moon.processing.decl.VarHelper;
import com.moon.processor.holder.Importer;
import com.moon.processor.utils.Collect2;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

/**
 * 用于生成接口类
 *
 * @author benshaoye
 */
public class FileInterfaceImpl extends JavaBlockCommentable implements JavaDeclarable {

    private final String packageName;
    private final String simpleName;
    private final String classname;

    private final Set<String> interfacesSet = new LinkedHashSet<>();
    private final Set<JavaGeneric> generics = new LinkedHashSet<>();
    private final MethodsScoped methodsScoped;
    private final FieldsScoped fieldsScoped;

    public FileInterfaceImpl(String packageName, String simpleName) {
        super(new Importer(packageName));
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.classname = String.join(".", packageName, simpleName);
        MethodsScoped scoped = new MethodsScoped(getImporter(), inInterface());
        this.fieldsScoped = new FieldsScoped(classname, scoped, inInterface());
        this.methodsScoped = scoped;
        annotationGenerated();
        docCommentOf(getClassname(), "", String2.format("@author {}", getClass().getCanonicalName()));
    }

    @Override
    public String getClassname() { return classname; }

    @Override
    public FileInterfaceImpl addModifierWith(Modifier modifier) {
        super.addModifierWith(modifier);
        return this;
    }

    public Set<String> getDeclaredFieldsName() {
        return fieldsScoped.getDeclaredFieldsName();
    }

    protected boolean inInterface() { return true; }

    protected Map<FieldScope, Map<String, JavaField>> getGroupedFieldsMap() {
        return fieldsScoped.getGroupedFieldsMap();
    }

    protected void afterMethodCreated(JavaMethod method) { }

    protected void afterFieldCreated(JavaField field) { field.withStatic(); }

    public String nextVar() { return fieldsScoped.nextVar(); }

    public String nextConstVar() { return fieldsScoped.nextVar(); }

    public JavaField publicField(String name, Class<?> fieldClass) {
        return publicField(name, fieldClass.getCanonicalName());
    }

    public JavaField publicField(String name, String typeTemplate, Object... types) {
        JavaField field = fieldsScoped.declareField(name, typeTemplate, types);
        afterFieldCreated(field);
        return field;
    }

    public JavaMethod publicMethod(String name) { return publicMethod(name, p -> {}); }

    public JavaMethod publicMethod(String name, Consumer<JavaParameters> parametersBuilder) {
        JavaMethod method = methodsScoped.declareMethod(name, parametersBuilder);
        afterMethodCreated(method);
        return method;
    }

    /**
     * 实现接口
     *
     * @param interfaces
     *
     * @return
     */
    public FileInterfaceImpl implement(Class<?>... interfaces) {
        Collect2.addAll(Class::getCanonicalName, interfacesSet, interfaces);
        return this;
    }

    public FileInterfaceImpl implementOf(String interfaceTemplate, Object... types) {
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
    public FileInterfaceImpl implement(String... interfaces) {
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
    public FileInterfaceImpl addGenericWith(String name) {
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
    public FileInterfaceImpl addGenericWith(String name, Consumer<JavaGeneric> genericUsing) {
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
        fieldsScoped.appendTo(addr);
        appendMethods(addr);
        return returning(addr, importMark);
    }

    protected final void appendMethods(JavaAddr addr) {
        methodsScoped.appendTo(addr);
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
