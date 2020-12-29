package com.moon.processor.model;

import com.moon.processor.Completable;
import com.moon.processor.JavaFileWriteable;
import com.moon.processor.JavaWriter;
import com.moon.processor.manager.ClassnameManager;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Test2;

import javax.annotation.processing.Filer;
import javax.lang.model.element.TypeElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
public class DeclareClass extends LinkedHashMap<String, DeclareProperty> implements JavaFileWriteable, Completable {

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

    private final Map<String, Map<String, DeclareMapping>> fieldAttrMap = new HashMap<>();
    private final Map<String, Map<String, DeclareMapping>> setterAttrMap = new HashMap<>();
    private final Map<String, Map<String, DeclareMapping>> getterAttrMap = new HashMap<>();

    public DeclareClass(TypeElement declareElement, ClassnameManager registry) {
        this.declareElement = declareElement;
        // 这里的 thisClassname 没考虑泛型
        this.thisClassname = Element2.getQualifiedName(declareElement);
        this.abstracted = Test2.isAbstractClass(declareElement);
        String name = this.getThisClassname();
        if (abstracted) {
            name = registry.getImplClassname(declareElement);
        }
        this.implClassname = name;
    }

    public TypeElement getDeclareElement() { return declareElement; }

    public String getThisClassname() { return thisClassname; }

    public String getImplClassname() { return implClassname; }

    public boolean isAbstract() { return abstracted; }

    public boolean isWritten() { return written; }

    public boolean isAbstracted() { return abstracted; }

    public Map<String, Map<String, DeclareMapping>> getFieldAttrMap() { return fieldAttrMap; }

    public Map<String, Map<String, DeclareMapping>> getSetterAttrMap() { return setterAttrMap; }

    public Map<String, Map<String, DeclareMapping>> getGetterAttrMap() { return getterAttrMap; }

    public final void addFieldAttr(String name, DeclareMapping attr) {
        getFieldAttrMap().computeIfAbsent(attr.getTargetCls(), k -> new HashMap<>(4)).put(name, attr);
    }

    public final void addSetterAttr(String name, DeclareMapping attr) {
        getSetterAttrMap().computeIfAbsent(attr.getTargetCls(), k -> new HashMap<>(4)).put(name, attr);
    }

    public final void addGetterAttr(String name, DeclareMapping attr) {
        getGetterAttrMap().computeIfAbsent(attr.getTargetCls(), k -> new HashMap<>(4)).put(name, attr);
    }

    @Override
    public void writeJavaFile(JavaWriter filer) {
        if (isAbstract() && !written) {
            this.doWriteJavaFile(filer);
        }
    }

    private boolean written = false;

    private void doWriteJavaFile(JavaWriter filer) {

        // TODO write implementation java file
        written = true;
    }

    @Override
    public void onCompleted() { values().forEach(Completable::onCompleted); }
}
