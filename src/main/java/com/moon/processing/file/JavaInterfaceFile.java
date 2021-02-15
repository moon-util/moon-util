package com.moon.processing.file;

import com.moon.processing.JavaDeclarable;
import com.moon.processor.holder.Importer;
import com.moon.processor.utils.String2;

import javax.lang.model.element.Modifier;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author benshaoye
 */
public class JavaInterfaceFile extends JavaAnnotable implements JavaDeclarable {

    private final String packageName;
    private final String simpleName;

    private final Set<String> interfacesSet = new LinkedHashSet<>();
    private final Set<JavaGeneric> generics = new LinkedHashSet<>();
    private final Map<String, JavaField> fieldsMap = new LinkedHashMap<>();
    private final Map<String, JavaMethod> methodsMap = new LinkedHashMap<>();

    public JavaInterfaceFile(String packageName, String simpleName) {
        super(new Importer(packageName));
        this.packageName = packageName;
        this.simpleName = simpleName;
    }

    @Override
    public String getClassname() { return String.join(".", packageName, simpleName); }

    @Override
    public JavaInterfaceFile addModifierWith(Modifier modifier) {
        super.addModifierWith(modifier);
        return this;
    }

    public JavaInterfaceFile publicConstField(String name, String typeTemplate, Object... types) {
        JavaField field = new JavaField(getImporter(), name, typeTemplate, types);
        return this;
    }

    public JavaMethod declareMethod(String name, Consumer<JavaParameters> parametersBuilder) {
        JavaParameters parameters = new JavaParameters(getImporter());
        JavaMethod method = new JavaMethod(getImporter(), name, parameters);
        methodsMap.put(method.getUniqueKey(), method);
        return method;
    }

    public JavaMethod publicAbstractMethod(String name, Consumer<JavaParameters> parametersBuilder) {
        return declareMethod(name, parametersBuilder).addModifierWith(Modifier.ABSTRACT);
    }

    /**
     * 实现接口
     *
     * @param interfaces
     *
     * @return
     */
    public JavaInterfaceFile implement(Class<?>... interfaces) {
        for (Class<?> aClass : interfaces) {
            this.interfacesSet.add(aClass.getCanonicalName());
        }
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
        for (String aClass : interfaces) {
            this.interfacesSet.add(aClass);
        }
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

    @Override
    public String toString() {
        JavaAddr addr = new JavaAddr();

        String pkg = this.packageName;
        if (!String2.isBlank(pkg)) {
            addr.add("package ").script(pkg).next(2);
        }

        JavaAddr.Mark importMark = addr.mark();

        addr.newAdd("public interface ").add(simpleName).add(getGenericDeclared()).add(getInterfacesWillImplemented())
            .add(" {").start();

        fieldsMap.forEach((key, field) -> field.appendTo(addr));

        methodsMap.forEach((key, method) -> method.appendTo(addr));

        addr.newEnd().add("}");
        importMark.with(getImporter().toString("\n"));
        return addr.toString();
    }

    private String getInterfacesWillImplemented() {
        Set<String> interfaces = this.interfacesSet;
        if (interfaces.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder().append(" extends ");
        for (String anInterface : interfaces) {
            builder.append(onImported(anInterface));
        }
        return builder.toString();
    }

    /**
     * 接口的泛型声明
     *
     * @return
     */
    private String getGenericDeclared() {
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
