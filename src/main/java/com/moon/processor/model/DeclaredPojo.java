package com.moon.processor.model;

import com.moon.processor.manager.NameManager;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Test2;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashMap;
import java.util.Map;

import static javax.lang.model.element.ElementKind.INTERFACE;

/**
 * POJO 定义
 * <p>
 * 描述只有 field、setter、getter、hashCode、toString、equals 这些方法的类
 * 如：Entity、DTO、DO、BO、VO
 * <p>
 * key: property name
 * value: property declaration (field + getter + setter)
 *
 * @author benshaoye
 */
public class DeclaredPojo extends LinkedHashMap<String, DeclareProperty> implements Completable {

    /**
     * 声明类
     */
    private final TypeElement declareElement;
    /**
     * {@link TypeElement#getQualifiedName()}
     * {@link #declareElement}
     */
    private final String thisClassname;
    /**
     * 实现类
     */
    private final String implClassname;
    /**
     * 是否是抽象类
     */
    private final boolean abstracted;
    private final boolean interfaced;

    public DeclaredPojo(TypeElement declareElement, NameManager registry) {
        this.declareElement = declareElement;
        // 这里的 thisClassname 没考虑泛型
        this.thisClassname = Element2.getQualifiedName(declareElement);
        this.interfaced = Test2.isElemKind(declareElement, INTERFACE);
        this.abstracted = Test2.isAny(declareElement, Modifier.ABSTRACT);
        String name = this.getThisClassname();
        if (abstracted) {
            name = registry.getImplClassname(declareElement);
        }
        this.implClassname = name;
    }

    public TypeElement getDeclareElement() { return declareElement; }

    public String getThisClassname() { return thisClassname; }

    public String getImplClassname() { return implClassname; }

    public boolean isAbstract() { return isAbstracted() || isInterfaced(); }

    public boolean isAbstracted() { return abstracted; }

    public boolean isInterfaced() { return interfaced; }

    private String getPackage() {
        return Element2.getPackageName(getDeclareElement());
    }

    @Override
    public void onCompleted() { values().forEach(Completable::onCompleted); }

    private final static DefParameters PARAMS = DefParameters.of();

    public DefJavaFiler getDefJavaFiler() {
        if (isAbstracted()) {
            DefJavaFiler filer = newDefJavaFiler();
            for (Map.Entry<String, DeclareProperty> propertyEntry : entrySet()) {
                DeclareProperty prop = propertyEntry.getValue();
                String name = propertyEntry.getKey();
                DeclareMethod getter = prop.getGetter();
                if (getter != null) {
                    String type = getter.getActualType();
                    filer.privateField(name, type);
                    filer.publicMethod(getter.getName(), type, PARAMS).override().returning(name);
                }
            }
            // return filer;
        }
        return null;
    }

    private DefJavaFiler newDefJavaFiler() {
        String implName = getImplClassname(), pkg = "";
        int lastIdx = implName.lastIndexOf('.');
        if (lastIdx >= 0) {
            pkg = implName.substring(0, lastIdx);
        }
        DefJavaFiler filer = DefJavaFiler.classOf(pkg, implName);
        if (isAbstracted()) {
            filer.extend(getThisClassname());
        } else if (isInterfaced()) {
            filer.implement(getThisClassname());
        }
        return filer;
    }
}
