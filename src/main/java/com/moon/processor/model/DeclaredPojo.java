package com.moon.processor.model;

import com.moon.processor.Completable;
import com.moon.processor.manager.NameManager;
import com.moon.processor.utils.Element2;
import com.moon.processor.utils.Test2;

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

    public DeclaredPojo(TypeElement declareElement, NameManager registry) {
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

    public boolean isAbstracted() { return abstracted; }

    @Override
    public void onCompleted() { values().forEach(Completable::onCompleted); }

    public DefJavaFiler getDefJavaFiler() {
        return null;
    }
}
