package com.moon.processor.model;

import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.Objects;

/**
 * @author benshaoye
 */
public class DeclareMethod {

    private final String thisClassname;
    private final ExecutableElement method;
    private final String name;
    private final String declareType;
    private final String actualType;
    /**
     * 是否是否主动声明的
     */
    private final boolean declared;
    private final boolean lombokGenerated;
    private final boolean defaultMethod;
    private final boolean abstractMethod;

    public DeclareMethod(
        String thisClassname,
        ExecutableElement method,
        String declareType,
        String actualType,
        boolean declared,
        boolean lombokGenerated
    ) {
        this(thisClassname, method, Element2.getSimpleName(method), declareType, actualType, declared, lombokGenerated);
    }

    public DeclareMethod(
        String thisClassname,
        ExecutableElement method,
        String name,
        String declareType,
        String actualType,
        boolean declared,
        boolean lombokGenerated
    ) {
        this.method = method;
        this.name = name;
        this.declareType = declareType;
        this.actualType = actualType;
        this.declared = declared;
        this.thisClassname = thisClassname;
        this.lombokGenerated = lombokGenerated;
        this.defaultMethod = Test2.isAny(method, Modifier.DEFAULT);
        this.abstractMethod = Test2.isAny(method, Modifier.ABSTRACT);
    }

    public static DeclareMethod ofLombok(
        String thisClassname, String setterName, String declareType, String actualType
    ) { return new DeclareMethod(thisClassname, null, setterName, declareType, actualType, false, true); }

    public static DeclareMethod ofDeclared(
        String thisClassname, ExecutableElement method, String declareType, String actualType
    ) { return new DeclareMethod(thisClassname, method, declareType, actualType, true, false); }

    public ExecutableElement getMethod() { return method; }

    public String getThisClassname() { return thisClassname; }

    public String getName() { return name; }

    public String getDeclareType() { return declareType; }

    public String getActualType() { return actualType; }

    public boolean isGenericDeclared() { return !Objects.equals(getDeclareType(), getActualType()); }

    public boolean isLombokGenerated() { return lombokGenerated; }

    /**
     * 是否是生成的
     *
     * @return true|false
     */
    public boolean isGenerated() { return !isDeclared(); }

    /**
     * 是否是声明的
     *
     * @return true|false
     */
    public boolean isDeclared() { return declared; }

    public boolean isDefaultMethod() { return defaultMethod; }

    public boolean isAbstractMethod() { return abstractMethod; }
}
