package com.moon.mapping.processing;

import com.moon.mapping.annotation.MappingFor;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author moonsky
 */
class ClassDefinition extends LinkedHashMap<String, ClassProperty> implements JavaFileWritable {

    protected final static String IMPL_SUFFIX = "Implementation";

    /**
     * 声明{@link MappingFor}的类
     */
    private final String thisClassname;
    /**
     * 声明{@link MappingFor}的类
     */
    private final TypeElement thisElement;
    /**
     * 构造器: 按参数个数排序
     */
    private final List<ExecutableElement> constructors;

    ClassDefinition(TypeElement thisElement) {
        this.thisClassname = ElementUtils.getQualifiedName(thisElement);
        this.thisElement = thisElement;
        constructors = new ArrayList<>();
    }

    public String getThisClassname() { return thisClassname; }

    public TypeElement getThisElement() { return thisElement; }

    /**
     * 返回实现类类名，
     * 如果是接口，返回自动生成实现类的类名
     * 否则返回{@link #thisClassname}
     */
    protected final String getImplClassname() {
        if (isInterface()) {
            String packageName = getPackageName();
            String simpleName = ElementUtils.getSimpleName(getThisClassname());
            return ElementUtils.concat(packageName, ".", simpleName, IMPL_SUFFIX);
        } else {
            return getThisClassname();
        }
    }

    private final String getSimpleImplClassname() {
        return ElementUtils.getSimpleName(getImplClassname());
    }

    private final String getPackageName() {
        return ElementUtils.getPackageName(getThisElement());
    }

    public final boolean isInterface() { return thisElement.getKind().isInterface(); }

    final void addConstructor(ExecutableElement constructor) {
        this.constructors.add(constructor);
    }

    @Override
    @SuppressWarnings("all")
    public void writeJavaFile() throws IOException {
        if (isInterface()) {
            final String pkgName = getPackageName();
            final String classname = getSimpleImplClassname();
            StringAdder adder = new StringAdder();
            adder.add("package ").add(pkgName).add(";");
            adder.add("public class ").add(classname)//
                .add(" implements ").add(getThisClassname()).add("{");
            adder.add("public ").add(classname).add("(){}");
            InterDefinition interDef = InterfaceUtils.toPropertiesMap(getThisElement());
            adder.add(interDef.implementation(classname));
            adder.add("}");
            EnvironmentUtils.newJavaFile(classname, adder);
        }
    }

    final ClassDefinition onParseCompleted() {
        this.constructors.sort(Comparator.comparingInt(o -> o.getParameters().size()));
        forEach((k, property) -> property.onParseCompleted());
        return this;
    }
}
