package com.moon.mapping.processing;

import com.moon.mapping.annotation.MappingFor;

import javax.annotation.processing.Filer;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author moonsky
 */
class DefinitionDetail extends LinkedHashMap<String, PropertyDetail> implements JavaFileWritable {

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

    DefinitionDetail(TypeElement thisElement) {
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
    public void writeJavaFile(Filer filer) throws IOException {
        if (isInterface()) {
            StringBuilder content = new StringBuilder();
            content.append("package ").append(getPackageName()).append(";");
            content.append("public class ").append(getSimpleImplClassname())//
                .append(" implements ").append(getThisClassname()).append("{");
            // generate codes ...
            content.append("}");
        }
    }

    final DefinitionDetail onParseCompleted() {
        this.constructors.sort(Comparator.comparingInt(o -> o.getParameters().size()));
        forEach((k, property) -> property.onParseCompleted());
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DefinitionDetail{");
        sb.append(super.toString());
        sb.append(", thisClassname='").append(thisClassname).append('\'');
        sb.append(", thisElement=").append(thisElement);
        sb.append(", constructors=").append(constructors);
        sb.append('}');
        return sb.toString();
    }
}
